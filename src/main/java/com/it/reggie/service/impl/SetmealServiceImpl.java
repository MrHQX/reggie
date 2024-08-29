package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomExcetion;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import com.it.reggie.mapper.SetmealMapper;
import com.it.reggie.service.SetmealDishService;
import com.it.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息，插入套餐表
        this.save(setmealDto);
        //获取菜品的集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //setmealDishes的套餐ID没有传进来,需要重新获取赋值进去
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，插入setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除套餐
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //检查是否有在售套餐
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //如果有就抛出异常
        if (count>0)
        {
            throw new CustomExcetion("套餐正在售卖，不能删除");
        }
        //先删除套餐
        this.removeByIds(ids);
        //删除关系表
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        //使用IN可以代替循环
        //delete from setmeal_dish where setmeal_id in(1,2,3...)
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public SetmealDto getSetmealWithDish(Long id) {
        SetmealDto setmealDto=new SetmealDto();
        //找到套餐信息
        Setmeal setmeal=this.getById(id);
        //拷贝找到的套餐信息
        BeanUtils.copyProperties(setmeal,setmealDto);
        //找到套餐里的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes=setmealDishService.list(queryWrapper);
        //把菜品给返回值
        setmealDto.setSetmealDishes(setmealDishes);
        //把SetmealDto返回
        return setmealDto;
    }

    @Override
    public void updateSetmealWithDish(SetmealDto setmealDto) {
        //跟新套餐信息
        this.updateById(setmealDto);
        //删除套餐的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //新增修改后的套餐菜品信息
        //先拿到哦修改后的菜品信息
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        //传入参数里没有套餐id，需要自行添加
        setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());//传入套餐id
            return item;
        }).collect(Collectors.toList());
        log.info(setmealDishes.toString());
        setmealDishService.saveBatch(setmealDishes);
    }
}
