package com.homework.railwayproject.exception;

import com.homework.railwayproject.web.ServiceCode;
import lombok.Getter;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:9:57
public class ServiceException extends RuntimeException
{
    @Getter
    private ServiceCode serviceCode;

    public ServiceException(ServiceCode serviceCode, String message) {
        //给message属性传值
        super(message);
        this.serviceCode = serviceCode;
    }
}