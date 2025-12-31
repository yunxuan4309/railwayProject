package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.TrainMapper;
import com.homework.railwayproject.pojo.entity.Train;
import com.homework.railwayproject.service.TrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {

    @Override
    public IPage<Train> getTrainPage(Page<Train> page, String trainNumber, String trainId) {
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getIsDeleted, 0);

        if (StringUtils.hasText(trainNumber)) {
            wrapper.like(Train::getTrainNumber, trainNumber);
        }

        if (StringUtils.hasText(trainId)) {
            wrapper.like(Train::getTrainId, trainId);
        }

        wrapper.orderByDesc(Train::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public Train getTrainByCode(Integer trainCode) {
        if (trainCode == null) {
            return null;
        }
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getTrainCode, trainCode);
        wrapper.eq(Train::getIsDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    public Train addTrain(Train train) {
        if (train == null) {
            return null;
        }
        train.setIsDeleted(0);
        save(train);
        return train;
    }

    @Override
    public Train updateTrain(Train train) {
        if (train == null || train.getTrainCode() == null) {
            return null;
        }
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getTrainCode, train.getTrainCode());
        wrapper.eq(Train::getIsDeleted, 0);

        Train existingTrain = getOne(wrapper);
        if (existingTrain == null) {
            return null;
        }

        update(train, wrapper);
        return train;
    }

    @Override
    public Boolean deleteTrain(Integer trainCode) {
        if (trainCode == null) {
            return false;
        }
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getTrainCode, trainCode);
        wrapper.eq(Train::getIsDeleted, 0);

        Train train = getOne(wrapper);
        if (train == null) {
            return false;
        }

        train.setIsDeleted(1);
        return update(train, wrapper);
    }
}
