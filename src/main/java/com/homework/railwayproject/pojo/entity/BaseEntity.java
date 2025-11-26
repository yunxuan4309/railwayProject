package com.homework.railwayproject.pojo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:48
@Setter
@Getter
public abstract class BaseEntity {
    protected LocalDateTime createTime;//创建时间
    protected LocalDateTime updateTime;//更新时间
    protected String createBy;//创建人
    protected String updateBy;//更新人
    protected Integer status;//数据状态：不同子类的状态意义不一样
    protected Integer isDeleted;//是否删除0,1
}