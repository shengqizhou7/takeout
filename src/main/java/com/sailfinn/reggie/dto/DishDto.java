package com.sailfinn.reggie.dto;

import com.sailfinn.reggie.entity.Dish;
import com.sailfinn.reggie.entity.DishFlavor;
import com.sailfinn.reggie.entity.Dish;
import com.sailfinn.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
