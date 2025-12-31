package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.HubQueryDTO;
import com.homework.railwayproject.pojo.dto.HubResultDTO;

import java.util.List;

public interface HubAnalysisService {
    List<HubResultDTO> getTopHubs(HubQueryDTO queryDTO);
}
