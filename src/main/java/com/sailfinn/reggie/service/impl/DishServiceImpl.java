package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.dto.DishDto;
import com.sailfinn.reggie.entity.Dish;
import com.sailfinn.reggie.entity.DishFlavor;
import com.sailfinn.reggie.mapper.DishMapper;
import com.sailfinn.reggie.service.DishFlavorService;
import com.sailfinn.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * add new dish and save flavor data
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //save fundamental info to table: dish
        //after saving, dishId will be auto-generated
        this.save(dishDto);

        //dish id
        Long dishId = dishDto.getId();

        //dish flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        //add dish id to every flavor we about to save, or we don't know which dish the flavor belongs to
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        //save flavor data to table: dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * when adding new dish, we query dish info and flavors info by id
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //query dish info from table: dish
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //query flavour info from table: dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //update table: dish
        this.updateById(dishDto);

        //delete flavor info--delete in dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //add flavor info--insert in dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
