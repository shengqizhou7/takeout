package com.sailfinn.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * customized meta object handler
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * autofill when inserting
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Auto fill common field [insert]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * autofill when updating
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("Auto fill common field [update]");
        log.info(metaObject.toString());

        //ThreadLocal. 每一个http请求都会被分配一个新的thread，ThreadLocal为每个线程提供单独的存储空间
        long id = Thread.currentThread().getId();
        log.info("Thread id is: {}", id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
