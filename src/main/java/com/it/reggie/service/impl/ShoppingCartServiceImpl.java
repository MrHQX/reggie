package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.mapper.ShoppingCartMapper;
import com.it.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    @Transactional
    public ShoppingCart addCar(ShoppingCart shoppingCart,Long userid) {
         /*
         shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询传入的的菜品或套餐是否已在购物车（口味，菜品，套餐一样），如果在就number+1
        Long dishId=shoppingCart.getDishId();//菜品ID
        Long setmealId=shoppingCart.getSetmealId();//套餐ID
                            //ps。拿到菜品里的口味
        //DishDto dishDto=dishService.getByIdWithFlavor(dishId);
        //List<DishFlavor> flavor=dishDto.getFlavors();
                             //动态条件构造器，先设置userID条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());//userid查询条件
        if (dishId!=null)
        {
                            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            //queryWrapper.eq(ShoppingCart::getDishFlavor,flavor);
        }else {
                            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        //已经存在，+1
        if (cartServiceOne!=null)
        {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }
        //不存在添加购物车，数量默认1
        else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
          */

        shoppingCart.setUserId(userid);//设置用户id
        Long dishId=shoppingCart.getDishId();//菜品ID
        Long setmealId=shoppingCart.getSetmealId();//套餐ID
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userid);//userid查询条件
        if (dishId!=null)
        {
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        //检查是否已在购物车
        ShoppingCart cartServiceOne = this.getOne(queryWrapper);
        //已经存在，+1
        if (cartServiceOne!=null)
        {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            this.updateById(cartServiceOne);
        }
        //不存在添加购物车，数量默认1
        else {
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
        return cartServiceOne;
    }
}
