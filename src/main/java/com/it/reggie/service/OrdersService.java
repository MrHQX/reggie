package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.common.R;
import com.it.reggie.entity.Orders;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders,Long id);

    public Page getOrders(String number, int page, int pageSize,String beginTime,String endTime);

    public Page getUserOrders(int page, int pageSize,Long id);

    public void again(Orders orders);
}
