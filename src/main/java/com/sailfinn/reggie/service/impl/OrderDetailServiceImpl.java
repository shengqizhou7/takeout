package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.entity.OrderDetail;
import com.sailfinn.reggie.mapper.OrderDetailMapper;
import com.sailfinn.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
