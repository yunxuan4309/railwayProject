package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.pojo.entity.Train;

public interface TrainService {

    IPage<Train> getTrainPage(Page<Train> page, String trainNumber, String trainId);

    Train getTrainByCode(Integer trainCode);

    Train addTrain(Train train);

    Train updateTrain(Train train);

    Boolean deleteTrain(Integer trainCode);
}
