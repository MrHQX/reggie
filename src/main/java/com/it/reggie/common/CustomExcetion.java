package com.it.reggie.common;

//自义定异常

public class CustomExcetion extends RuntimeException{
    public CustomExcetion(String msg){
        super(msg);
    }
}
