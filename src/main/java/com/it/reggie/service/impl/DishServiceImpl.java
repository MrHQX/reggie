package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.mapper.DishMapper;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;

    /**
     * 新增
     * @param dishDto
     */
     @Transactional
     @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息表
        this.save(dishDto);
        //获取保存的菜品ID
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        //stream将集合转换成流，
        // map将流中的元素的dishId赋值，
        // collect把流再转集合。
        //使用foreach也可以做到
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        //dishFlavorService.saveBatch(dishDto.getId()); 不能直接这样写，因为请求里的数据没有封装上dishID,需要手动把菜品ID添加进口味里。
    }

    /**
     * 先查菜品，再查口味
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查菜品
        Dish dish=this.getById(id);
        DishDto dishDto=new DishDto();
        //拷贝
        BeanUtils.copyProperties(dish,dishDto);

        //查口味
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list=dishFlavorService.list(queryWrapper);
        //把查到的口味放入对象
         dishDto.setFlavors(list);
        return dishDto;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //先更菜品
        this.updateById(dishDto);
        //查找菜品口味id
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());//
        //清理当前菜品的口味数据，------dishflavor的delete
        dishFlavorService.remove(queryWrapper);
        //更口味-----dishflavor的insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        log.info(flavors.toString());
        //dishDto里的口味list没有菜品id，需要手动添加。
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        //dishFlavorService.saveBatch(dishDto.getId()); 不能直接这样写，因为请求里的数据没有封装上dishID,需要手动把菜品ID添加进口味里。
    }

    /**
     * 删除菜品，包括口味表的里的。
     * @param list
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> list) {
        //先查口味菜品id,再删除
        for (Long item:list)
        {
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(item!=null,DishFlavor::getDishId,item);
            dishFlavorService.remove(queryWrapper);
        }
        dishService.removeByIds(list);
    }
}
