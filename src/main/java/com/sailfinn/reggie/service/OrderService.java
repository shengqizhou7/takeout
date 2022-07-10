package com.sailfinn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sailfinn.reggie.entity.Orders;
import org.springframework.web.bind.annotation.PostMapping;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
