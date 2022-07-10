package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.entity.SetmealDish;
import com.sailfinn.reggie.mapper.SetmealDishMapper;
import com.sailfinn.reggie.service.SetmealdishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealdishService {
}
