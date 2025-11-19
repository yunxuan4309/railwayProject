package com.homework.railwayproject.exception.handler;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Set;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:9:56
@Slf4j
@RestControllerAdvice//@RestControllerAdvice定义SpringMvc的全局异常处理类,接收异常,在本类里面匹配异常,调用相关方法解决

public class GlobalExceptionHandler //全局异常处理器
{
    public GlobalExceptionHandler() {
        log.debug("创建全局异常处理器类对象:GlobalExceptionHandler");
    }
    //@ExceptionHandler指定具体异常处理的方法
    @ExceptionHandler
    public JsonResult<Void> handleServiceException(ServiceException e){
        log.warn("程序运行过程中出现异常ServiceException,将统一处理");
        log.warn("异常信息:{}",e.getMessage());//{}站位符
        return JsonResult.fail(e);
    }

    //绑定异常BindException(类型不匹配),约束异常ConstraintViolationException()不满足约束都是spring Validation框架下的异常
    @ExceptionHandler
    public JsonResult<Void> handleBindException(BindException e) {
        log.warn("程序运行过程中出现BindException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        String message = e.getFieldError().getDefaultMessage();
        // StringBuilder stringBuilder = new StringBuilder();
        // List<FieldError> fieldErrors = e.getFieldErrors();
        // for (FieldError fieldError : fieldErrors) {
        //     stringBuilder.append(fieldError.getDefaultMessage());
        // }
        // String message = stringBuilder.toString();
        return JsonResult.fail(ServiceCode.ERROR_BAD_REQUEST, message);
    }

    @ExceptionHandler
    public JsonResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("程序运行过程中出现handleConstraintViolationException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            stringBuilder.append(constraintViolation.getMessage());
        }
        String message = stringBuilder.toString();
        return JsonResult.fail(ServiceCode.ERROR_BAD_REQUEST, message);
    }
    //org.springframework.security.authentication.BadCredentialsException 控制台异常抛出
    //spring Security  异常处理:对InternalAuthenticationServiceException.class无用户名,
    //BadCredentialsException.class,密码错误 返回相同的处理结果

    /*@ExceptionHandler({//此写法是一个数组,可以写多个异常,里面的异常都有一个父类异常AuthenticationException
            InternalAuthenticationServiceException.class,
            BadCredentialsException.class //密码错误,秘钥错误
    })
    public JsonResult<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("程序运行过程中出现AuthenticationException，将统一处理！");
        log.warn("异常类型：{}", e.getClass().getName());
        log.warn("异常信息：{}", e.getMessage());
        String message = "登录失败，用户名或密码错误！";
        return JsonResult.fail(ServiceCode.ERROR_UNAUTHORIZED, message);
    }*/
    //Spring Security 账号禁用异常
    /*@ExceptionHandler
    public JsonResult<Void> handleDisabledException(DisabledException e) {
        log.warn("程序运行过程中出现DisabledException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        String message = "登录失败，账号已经被禁用！";
        return JsonResult.fail(ServiceCode.ERROR_UNAUTHORIZED_DISABLED, message);
    }*/
    //添加访问权限异常,要选用security 框架的AccessDeniedException
    @ExceptionHandler
    public JsonResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("程序运行过程中出现AccessDeniedException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        String message = "拒绝访问，您当前登录的账号无此操作权限！";
        return JsonResult.fail(ServiceCode.ERROR_FORBIDDEN, message);
    }

    // 注意：以下方法存在的意义主要在于：避免因为某个异常未被处理，导致服务器端响应500错误
    // 注意：e.printStackTrace()通常是禁止使用的，因为其输出方式是阻塞式的！
    //      以下方法中使用了此语句，是因为用于发现错误，并不断的补充处理对应的异常的方法
    //      随着开发进度的推进，执行到以下方法的概率会越来越低，
    //      出现由于此语句导致的问题的概率也会越来越低，
    //      甚至补充足够多的处理异常的方法后，根本就不会执行到以下方法了
    //      当项目上线后，可以将此语句删除
    //Throwable是所有异常的父类
    @ExceptionHandler
    public JsonResult<Void> handleThrowable(Throwable e) {
        log.warn("程序运行过程中出现Throwable，将统一处理！");
        log.warn("异常类型：{}", e.getClass());
        log.warn("异常信息：{}", e.getMessage());
        String message = "服务器忙，请稍后再次尝试！（开发过程中，如果看到此提示，请检查控制台的信息，并补充处理异常的方法）";
        // String message = "服务器忙，请稍后再尝试！"; // 项目上线时应该使用此提示文本
        e.printStackTrace(); // 打印异常的跟踪信息，主要是为了在开发阶段更好的检查出现异常的原因
        return JsonResult.fail(ServiceCode.ERROR_UNKNOWN, message);
    }


}