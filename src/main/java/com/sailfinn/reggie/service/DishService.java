package com.sailfinn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sailfinn.reggie.dto.DishDto;
import com.sailfinn.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //add dish and flavor data to 2 tables: dish, dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //query dish info and flavors info by id
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
