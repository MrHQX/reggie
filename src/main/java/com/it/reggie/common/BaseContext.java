package com.it.reggie.common;

/*
基于TreadLocal封装工具类，用户板寸和获取当前登录用户id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){//设置值
        threadLocal.set(id);
    }
    public static Long getCurrentId(){//获取值
        return threadLocal.get();
    }
}
