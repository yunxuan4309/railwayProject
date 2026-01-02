package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.mapper.StationMapper;
import com.homework.railwayproject.mapper.TrainMapper;
import com.homework.railwayproject.pojo.dto.HubQueryDTO;
import com.homework.railwayproject.pojo.dto.HubResultDTO;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.pojo.entity.Train;
import com.homework.railwayproject.service.HubAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubAnalysisServiceImpl implements HubAnalysisService {

    @Autowired
    private HighSpeedPassengerCleanMapper highSpeedPassengerCleanMapper;

    @Autowired
    private StationMapper stationMapper;
    
    @Autowired
    private TrainMapper trainMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<HubResultDTO> getTopHubs(HubQueryDTO queryDTO) {
        String trainType = queryDTO.getTrainType();
        Integer topN = queryDTO.getTopN() != null ? queryDTO.getTopN() : 10;
        
        // 生成缓存key
        String cacheKey = generateCacheKey(trainType, topN);
        
        // 尝试从缓存获取
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        List<HubResultDTO> cachedResult = (List<HubResultDTO>) ops.get(cacheKey);
        if (cachedResult != null) {
            log.debug("从缓存获取枢纽分析结果: {}", cacheKey);
            return cachedResult;
        }
        
        // 缓存未命中，执行计算逻辑
        List<HubResultDTO> result = computeHubAnalysis(trainType, topN);
        
        // 存入缓存，有效期2小时
        ops.set(cacheKey, result, 2, java.util.concurrent.TimeUnit.HOURS);
        
        return result;
    }
    
    /**
     * 生成缓存key
     */
    private String generateCacheKey(String trainType, Integer topN) {
        String type = trainType != null ? trainType : "all";
        return "hub:top:" + type + ":" + topN;
    }
    
    /**
     * 执行枢纽分析计算逻辑
     */
    private List<HubResultDTO> computeHubAnalysis(String trainType, Integer topN) {
        // 根据trainType筛选符合条件的trainCode列表
        List<Integer> filteredTrainCodes = new ArrayList<>();
        if (StringUtils.hasText(trainType)) {
            LambdaQueryWrapper<Train> trainWrapper = new LambdaQueryWrapper<>();
            trainWrapper.eq(Train::getIsDeleted, 0);
            
            if ("高铁".equals(trainType)) {
                trainWrapper.like(Train::getTrainId, "G%");
            } else if ("城际".equals(trainType)) {
                trainWrapper.like(Train::getTrainId, "C%");
            } else if ("普速".equals(trainType)) {
                trainWrapper.notLike(Train::getTrainId, "G%")
                       .notLike(Train::getTrainId, "C%");
            }
            
            List<Train> trains = trainMapper.selectList(trainWrapper);
            for (Train train : trains) {
                filteredTrainCodes.add(train.getTrainCode());
            }
        }
        
        LambdaQueryWrapper<HighSpeedPassengerClean> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HighSpeedPassengerClean::getIsDeleted, 0);
        
        if (!filteredTrainCodes.isEmpty()) {
            wrapper.in(HighSpeedPassengerClean::getTrainCode, filteredTrainCodes);
        }

        List<HighSpeedPassengerClean> tickets = highSpeedPassengerCleanMapper.selectList(wrapper);

        Map<Integer, Station> stationMap = new HashMap<>();
        List<Station> stations = stationMapper.selectList(new LambdaQueryWrapper<Station>().eq(Station::getIsDeleted, 0));
        for (Station station : stations) {
            stationMap.put(station.getSiteId(), station);
        }

        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Integer> degreeMap = new HashMap<>();

        for (HighSpeedPassengerClean ticket : tickets) {
            Integer departId = ticket.getDepartStationId();
            Integer arriveId = ticket.getArriveStationId();

            if (departId != null && arriveId != null && !departId.equals(arriveId)) {
                adjacencyList.computeIfAbsent(departId, k -> new HashSet<>()).add(arriveId);
                adjacencyList.computeIfAbsent(arriveId, k -> new HashSet<>()).add(departId);

                degreeMap.put(departId, degreeMap.getOrDefault(departId, 0) + 1);
                degreeMap.put(arriveId, degreeMap.getOrDefault(arriveId, 0) + 1);
            }
        }

        Map<Integer, BigDecimal> betweennessMap = new HashMap<>();
        for (Integer stationId : adjacencyList.keySet()) {
            BigDecimal betweenness = calculateBetweennessCentrality(stationId, adjacencyList);
            betweennessMap.put(stationId, betweenness);
        }

        List<HubResultDTO> results = new ArrayList<>();
        for (Map.Entry<Integer, Station> entry : stationMap.entrySet()) {
            Integer siteId = entry.getKey();
            Station station = entry.getValue();

            if (!degreeMap.containsKey(siteId)) {
                continue;
            }

            HubResultDTO dto = new HubResultDTO();
            dto.setSiteId(siteId);
            dto.setStationName(station.getStationName());
            dto.setStationLevel(station.getStationLevel());
            dto.setDegreeCentrality(degreeMap.get(siteId));
            dto.setBetweennessCentrality(betweennessMap.getOrDefault(siteId, BigDecimal.ZERO));
            dto.setTrainType(trainType != null ? trainType : "全部");

            // 调整评分计算公式，使枢纽级别更有区分度
            // 度中心性需要标准化处理，避免因数值过大导致所有站点都是特级枢纽
            // 使用对数缩放以增强区分度：log(度中心性 + 1) * 10
            BigDecimal degreeScore = new BigDecimal(Math.log(degreeMap.get(siteId) + 1)).multiply(BigDecimal.valueOf(10));
            // 介数中心性保持原有计算方式
            BigDecimal betweennessScore = betweennessMap.getOrDefault(siteId, BigDecimal.ZERO).multiply(BigDecimal.valueOf(1000));
            BigDecimal score = degreeScore.add(betweennessScore);
            dto.setHubLevel(calculateHubLevel(score));

            results.add(dto);
        }

        results.sort((a, b) -> {
            BigDecimal scoreA = BigDecimal.valueOf(a.getDegreeCentrality() * 0.5)
                    .add(a.getBetweennessCentrality().multiply(BigDecimal.valueOf(1000)));
            BigDecimal scoreB = BigDecimal.valueOf(b.getDegreeCentrality() * 0.5)
                    .add(b.getBetweennessCentrality().multiply(BigDecimal.valueOf(1000)));
            return scoreB.compareTo(scoreA);
        });

        return results.stream().limit(topN).collect(Collectors.toList());
    }

    private BigDecimal calculateBetweennessCentrality(Integer nodeId, Map<Integer, Set<Integer>> adjacencyList) {
        BigDecimal betweenness = BigDecimal.ZERO;
        List<Integer> nodes = new ArrayList<>(adjacencyList.keySet());

        for (Integer source : nodes) {
            for (Integer target : nodes) {
                if (source.equals(target) || source.equals(nodeId) || target.equals(nodeId)) {
                    continue;
                }

                List<List<Integer>> allPaths = findAllShortestPaths(source, target, adjacencyList);
                if (allPaths.isEmpty()) {
                    continue;
                }

                int totalPaths = allPaths.size();
                long pathsThroughNode = allPaths.stream()
                        .filter(path -> path.contains(nodeId))
                        .count();

                if (totalPaths > 0) {
                    betweenness = betweenness.add(BigDecimal.valueOf(pathsThroughNode)
                            .divide(BigDecimal.valueOf(totalPaths), 10, RoundingMode.HALF_UP));
                }
            }
        }

        return betweenness;
    }

    private List<List<Integer>> findAllShortestPaths(Integer source, Integer target, Map<Integer, Set<Integer>> adjacencyList) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(new ArrayList<>(List.of(source)));
        visited.add(source);

        int minPathLength = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            List<Integer> currentPath = queue.poll();
            Integer currentNode = currentPath.get(currentPath.size() - 1);

            if (currentNode.equals(target)) {
                if (currentPath.size() < minPathLength) {
                    minPathLength = currentPath.size();
                    result.clear();
                    result.add(new ArrayList<>(currentPath));
                } else if (currentPath.size() == minPathLength) {
                    result.add(new ArrayList<>(currentPath));
                }
                continue;
            }

            if (currentPath.size() >= minPathLength) {
                continue;
            }

            Set<Integer> neighbors = adjacencyList.get(currentNode);
            if (neighbors != null) {
                for (Integer neighbor : neighbors) {
                    if (!currentPath.contains(neighbor)) {
                        List<Integer> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.offer(newPath);
                    }
                }
            }
        }

        return result;
    }

    private String calculateHubLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(200)) >= 0) {
            return "特级枢纽";
        } else if (score.compareTo(BigDecimal.valueOf(150)) >= 0) {
            return "一级枢纽";
        } else if (score.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "二级枢纽";
        } else if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "三级枢纽";
        } else if (score.compareTo(BigDecimal.valueOf(20)) >= 0) {
            return "四级枢纽";
        } else {
            return "五级枢纽";
        }
    }
}
