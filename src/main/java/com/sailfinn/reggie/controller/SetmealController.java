package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.dto.SetmealDto;
import com.sailfinn.reggie.entity.Category;
import com.sailfinn.reggie.entity.Setmeal;
import com.sailfinn.reggie.service.CategoryService;
import com.sailfinn.reggie.service.SetmealService;
import com.sailfinn.reggie.service.SetmealdishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Setmeal management
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealdishService setmealdishService;

    /**
     * add combo
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("Combo info: {}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("Combo added!");
    }

    /**
     * Combo multiple page
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //page constructor
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //like 模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //sorting
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        //copy
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        //add category name to dtoPage!!
        dtoPage.setRecords(pageInfo.getRecords().stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList()));

        return R.success(dtoPage);
    }

    /**
     * delete combo
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids: {}", ids);

        setmealService.removeWithDish(ids);
        return R.success("Combo deleted!");
    }

    /**
     * query combo info by conditions
     * list meals in a combo
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
