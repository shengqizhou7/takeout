package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.common.CustomException;
import com.sailfinn.reggie.entity.Category;
import com.sailfinn.reggie.entity.Dish;
import com.sailfinn.reggie.entity.Setmeal;
import com.sailfinn.reggie.mapper.CategoryMapper;
import com.sailfinn.reggie.service.CategoryService;
import com.sailfinn.reggie.service.DishService;
import com.sailfinn.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * Delete category by id. Check before delete!
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //Query by category id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);


        //check if current category is related to any dish. If so, throw exception
        if(count1 > 0){
            //related to dish, throw exception
            throw new CustomException("Related to dish. Cannot be deleted");
        }

        //check if current category is related to any combo. If so, throw exception
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            //related to setmeal, throw exception
            throw new CustomException("Related to combo. Cannot be deleted");
        }

        //else, delete category
        super.removeById(id);
    }
}
