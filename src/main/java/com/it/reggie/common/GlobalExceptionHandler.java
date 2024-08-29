package com.it.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理
@RestControllerAdvice(annotations = {RestController.class,Controller.class})
@Slf4j
public class GlobalExceptionHandler {
    //异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)  //完整性约束异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());
        if(e.getMessage().contains("Duplicate entry")){
            String[] s = e.getMessage().split(" ");
            String msg=s[2]+"已存在";
            return R.error(msg);
        }
        return R.error("服务器繁忙，请稍后重试");
    }

    @ExceptionHandler(CustomExcetion.class)  //分类删除异常
    public R<String> exceptionHandler(CustomExcetion e){
        log.error(e.getMessage());

        return R.error(e.getMessage());
    }

}
