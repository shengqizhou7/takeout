package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailfinn.reggie.common.BaseContext;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.entity.Orders;
import com.sailfinn.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * place order
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("order data: {}", orders);
        orderService.submit(orders);
        return R.success("Order placed");
    }


    /**
     * backend - multiple pages for orders
     * @param page
     * @param pageSize
     * @param orderId
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String orderId, String beginTime, String endTime){
        log.info("page = {}, page size = {}, orderId = {}", page, pageSize, orderId);

        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(orderId != null, Orders::getNumber, orderId);
        queryWrapper.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * front end - pages for orders
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        log.info("page = {}, page size = {}, orderId = {}", page, pageSize);

        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //queryWrapper.like(orderId != null, Orders::getNumber, orderId);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

}
