package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //把新增菜品插入对应口味数据，需要插入两张表
    public void saveWithFlavor(DishDto dishDto);
    //根据ID查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品，需要更新两张表
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品
    public void deleteWithFlavor(List<Long> list);
}
