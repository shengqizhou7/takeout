package com.sailfinn.reggie.dto;

import com.sailfinn.reggie.entity.Setmeal;
import com.sailfinn.reggie.entity.SetmealDish;
import com.sailfinn.reggie.entity.Setmeal;
import com.sailfinn.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
