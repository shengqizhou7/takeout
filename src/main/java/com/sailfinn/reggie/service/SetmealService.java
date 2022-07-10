package com.sailfinn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sailfinn.reggie.dto.SetmealDto;
import com.sailfinn.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * add combo and save relation of combo and dishes
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * delete combo and relation of combo and dishes
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
