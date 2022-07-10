package com.sailfinn.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户的ID
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * set value
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * get value
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
