package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import com.it.reggie.entity.OrderDetail;
import com.it.reggie.entity.Orders;
import com.it.reggie.service.OrdersDetailService;
import com.it.reggie.service.OrdersService;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Locale;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersDetailService ordersDetailService;

    /**
     * 提价订单
     * @param orders
     * @param session
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session){
        Long userid= (Long) session.getAttribute("user");
        ordersService.submit(orders,userid);
        return R.success("成功下单");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> getOrders(int page,int pageSize,String number,  String beginTime, String endTime)
    {
        return R.success(ordersService.getOrders(number,page,pageSize,beginTime,endTime));
    }

    /**
     * 派送更新状态
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders)
    {
        log.info(orders.toString());
        Orders orders1 = ordersService.getById(orders.getId());
        orders1.setStatus(orders.getStatus());
        ordersService.updateById(orders1);
        return R.success("修改成功");

    }

    /**
     * 用户订单查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> getUserOrders(int page,int pageSize,HttpSession session){
        Long id= (Long) session.getAttribute("user");
        return R.success(ordersService.getUserOrders(page,pageSize,id));
    }


    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        log.info(orders.toString());
        ordersService.again(orders);
        return R.success("再来一单成功");
    }
}
