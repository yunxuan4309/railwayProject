package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.homework.railwayproject.pojo.dto.TicketQueryDTO;
import com.homework.railwayproject.pojo.dto.TicketResultDTO;

public interface TicketQueryService {

    IPage<TicketResultDTO> queryTickets(TicketQueryDTO queryDTO);
}
