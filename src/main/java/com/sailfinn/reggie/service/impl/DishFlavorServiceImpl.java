package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.entity.DishFlavor;
import com.sailfinn.reggie.mapper.DishFlavorMapper;
import com.sailfinn.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
