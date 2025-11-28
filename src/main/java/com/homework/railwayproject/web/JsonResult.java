package com.homework.railwayproject.web;

import com.homework.railwayproject.exception.ServiceException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:9:52
@Data//自动生产get和set方法
@NoArgsConstructor//无参构造方法
@AllArgsConstructor//全参构造方法  频繁使用的类就加这两个注解
@Accessors(chain=true)//允许链式写法
@Slf4j
public class JsonResult<T> implements Serializable
{//返回前端的统一格式

    // @ApiModelProperty是类的属性knife4j的提示

    private Integer state;


    private String message;

    //T 泛型的数据类型

    private T data;

    // private Object data;//T代表type V value代表值 E element代表元素

    // 修复：添加正确的静态方法
    public static <T> JsonResult<T> ok(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.state = ServiceCode.OK.getValue();
        jsonResult.data = data;
        return jsonResult;
    }

    public static JsonResult<Void> ok() {
        return ok(null);
    }

    public static <T> JsonResult<T> fail(ServiceException e) {
        return fail(e.getServiceCode(), e.getMessage());
    }

    public static <T> JsonResult<T> fail(ServiceCode serviceCode, String message) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.state = serviceCode.getValue();
        jsonResult.message = message;
        return jsonResult;
    }

}
