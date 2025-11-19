package com.homework.railwayproject.web;

import com.homework.railwayproject.exception.ServiceException;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
public class JsonResult<T> implements Serializable
{//返回前端的统一格式
    //从今以后,所有的类都要序列化
    // @ApiModelProperty是类的属性knife4j的提示
    @ApiModelProperty("状态码")
    private Integer state;

    @ApiModelProperty("消息提示")
    private String message;

    //T 泛型的数据类型
    @ApiModelProperty("返回数据")
    private T data;

    // private Object data;//T代表type V value代表值 E element代表元素

    public static JsonResult<Void> ok(){
        return ok(null);
    }
    public static<T> JsonResult<T> ok(T data){
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.state=ServiceCode.OK.getValue();
        jsonResult.data=data;
        return jsonResult;
    }//这个是判断成功的
    //对于jsonResult失败的情况,我们使用全局异常处理
    //我们认为,成功就是成功,失败就是异常->全局异常处理
    public static JsonResult<Void> fail(ServiceException e) {
        return fail(e.getServiceCode(),e.getMessage());
    }
    public static JsonResult<Void> fail(ServiceCode serviceCode,String message){
        JsonResult jsonResult = new JsonResult<>();
        jsonResult.state=serviceCode.getValue();
        jsonResult.message=message;
        return jsonResult;
    }

}
