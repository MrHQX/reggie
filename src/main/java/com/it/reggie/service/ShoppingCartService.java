package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.common.R;
import com.it.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    public ShoppingCart addCar(ShoppingCart shoppingCart, Long userid);
}
