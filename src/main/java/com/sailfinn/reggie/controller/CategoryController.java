package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.entity.Category;
import com.sailfinn.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Category management
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * Add category
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category: {}", category);
        categoryService.save(category);
        return R.success("Added!");
    }


    /**
     * Multiple page for category info，分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * Delete category by ID
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("Delete category, ID is: {}", ids);

        //categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("Category deleted!");
    }


    /**
     * Revise category info by id
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("Revise category info: {}", category);
        categoryService.updateById(category);

        return R.success("Revised!");
    }

    /**
     * 添加菜品-根据条件查询分类数据
     * Query category info
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //add conditions
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //add sort condition
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
