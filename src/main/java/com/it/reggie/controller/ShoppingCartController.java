package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import com.it.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 购物车管理
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        log.info(shoppingCart.toString());
        /*
        //获取用户ID，
        BaseContext.setCurrentId((Long) session.getAttribute("user"));
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询传入的的菜品或套餐是否已在购物车（口味，菜品，套餐一样），如果在就number+1
        Long dishId=shoppingCart.getDishId();//菜品ID
        Long setmealId=shoppingCart.getSetmealId();//套餐ID
                            //ps。拿到菜品里的口味
        //DishDto dishDto=dishService.getByIdWithFlavor(dishId);
        //List<DishFlavor> flavor=dishDto.getFlavors();
                             //动态条件构造器，先设置userID条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,);//userid查询条件
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
        BaseContext.setCurrentId((Long) session.getAttribute("user"));
        Long id= BaseContext.getCurrentId();
        ShoppingCart shoppingCart1=shoppingCartService.addCar(shoppingCart,id);
        return R.success(shoppingCart1);
    }

    /**
     * 查购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        log.info("查购物车。。。。");
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        BaseContext.setCurrentId((Long) session.getAttribute("user"));
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts=shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @param session
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(HttpSession session){

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        BaseContext.setCurrentId((Long) session.getAttribute("user"));
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }

    /**
     * 已选菜品或套餐减一
     *
     * @return
     */

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        log.info(shoppingCart.toString());
        BaseContext.setCurrentId((Long) session.getAttribute("user"));
        Long dishId=shoppingCart.getDishId();
        Long setmealId=shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //查是删套餐还是菜品
        if (shoppingCart.getDishId()!=null)
        {//如果是菜品，则检查数量
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart shoppingCart1=shoppingCartService.getOne(queryWrapper);
            Integer number=shoppingCart1.getNumber();
            //查菜品数量是否为1，如果为1直接删除
            if (number==1)
            {
                shoppingCartService.removeById(shoppingCart1.getId());
            }else {
                //数量不是1，则数量减1
                shoppingCart1.setNumber(number-1);
                shoppingCartService.updateById(shoppingCart1);
            }
        }else {
            //是套餐，同上
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart shoppingCart1=shoppingCartService.getOne(queryWrapper);
            Integer number=shoppingCart1.getNumber();
            if (number==1)
            {
                shoppingCartService.removeById(shoppingCart1.getId());
            }else {
                //数量不是1，则数量减1
                shoppingCart1.setNumber(number-1);
                shoppingCartService.updateById(shoppingCart1);
            }
        }
        return R.success("减少操作成功");
    }
}
