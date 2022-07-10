package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.common.CustomException;
import com.sailfinn.reggie.dto.SetmealDto;
import com.sailfinn.reggie.entity.Setmeal;
import com.sailfinn.reggie.entity.SetmealDish;
import com.sailfinn.reggie.mapper.SetmealMapper;
import com.sailfinn.reggie.service.SetmealService;
import com.sailfinn.reggie.service.SetmealdishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealdishService setmealdishService;

    /**
     * add combo and save relation of combo and dishes
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //save basic combo info: insert in table: setmeal
        this.save(setmealDto);

        //由于前端传入的数据里setmealdish中没有setmealId，所以需要手动插入。该值在上一步save过程中由框架自动赋给dto，get即可
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //save relation of combo and dishes: insert in table: setmeal_dish
        setmealdishService.saveBatch(setmealDishes);
    }

    /**
     * delete combo and relation of combo and dishes
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //SQL: select count(*) from setmeal where id in (1, 2, 3) and status = 1
        //query combo state to decide if we could delete it
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if(count > 0){
            //if no, throw exceptions
            throw new CustomException("Combo is being sold, cannot be deleted!");
        }

        //if yes, delete data in table: setmeal
        this.removeByIds(ids);


        //SQL: delete from setmeal_deal where setmeal_id in (1, 2, 3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        //delete data in table: setmeal_dish
        setmealdishService.remove(lambdaQueryWrapper);



    }
}
