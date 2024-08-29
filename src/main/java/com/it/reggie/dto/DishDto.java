package com.it.reggie.dto;

import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {//!!!!!!!!!!!!!注意这里继承了Dish类！！！！！！！！！！！，所以它有Dish的属性

    private List<DishFlavor> flavors = new ArrayList<>();//口味

    private String categoryName; //菜品分类查询

    private Integer copies;
}
