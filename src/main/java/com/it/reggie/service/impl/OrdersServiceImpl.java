package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomExcetion;
import com.it.reggie.dto.OrdersDto;
import com.it.reggie.entity.*;
import com.it.reggie.mapper.OrdersMapper;
import com.it.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private OrdersDetailService ordersDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders, Long userid) {
       //查购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userid);
        List<ShoppingCart> shoppingCarts=shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCarts==null||shoppingCarts.size()==0)
        {
            throw new CustomExcetion("购物车为空");
        }
        //插订单表,需要用户信息，地址信息。
                //查用户
        User user=userService.getById(userid);
                //要查下单地址
        AddressBook addressBook=addressBookService.getById(orders.getAddressBookId());
        if (addressBook==null)
        {
            throw new CustomExcetion("地址有误");
        }
        Long orderid=IdWorker.getId();//生成订单号



        AtomicInteger allAmount=new AtomicInteger(0);//原子操作，保证线程安全
        //批量把购物车的信息传入订单明细，并计算总金额
        List<OrderDetail> list=shoppingCarts.stream().map(item->
        {
            OrderDetail ordersDetail=new OrderDetail();
            BeanUtils.copyProperties(item,ordersDetail);
            ordersDetail.setOrderId(orderid);
            allAmount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return ordersDetail;
        }).collect(Collectors.toList());

        orders.setId(orderid);//订单id
        orders.setNumber(String.valueOf(orderid));//订单号
        orders.setUserId(userid);//下单用户ID
        orders.setUserName(user.getName());//用户名
        orders.setPhone(addressBook.getPhone());//收货手机号
        orders.setAddress((addressBook.getProvinceName()==null?"":addressBook.getProvinceName())
                + (addressBook.getCityName()==null?"":addressBook.getCityName())
                +(addressBook.getDistrictName()==null?"":addressBook.getDistrictName())
                +(addressBook.getDetail()==null?"":addressBook.getDetail()));//地址
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setCheckoutTime(LocalDateTime.now());//结账时间
        orders.setAmount(new BigDecimal(allAmount.get()));//总金额
        orders.setStatus(2);//状态
        this.save(orders);
        //插订单明细表,需要菜品或套餐信息，口味，金额，请求里的（支付方式、备注）
        ordersDetailService.saveBatch(list);
        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }


    /**
     *后台订单分页查询
     * @param number
     * @param page
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page getOrders(String number,int page, int pageSize,String beginTime,String endTime) {
        Page<Orders> pageInfo=new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        if (beginTime!=null&&endTime!=null)
        {
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        if (number!=null)
        {
            queryWrapper.like(number!=null,Orders::getNumber,number);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo,queryWrapper);
        return pageInfo;
    }

    /**
     *  用户订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page getUserOrders(int page, int pageSize,Long id) {
        /*
        Page<Orders> pageInfo=new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Orders::getOrderTime);
        this.page(pageInfo,queryWrapper);
         */
        //创建page
        Page<Orders> pageInfo=new Page(page,pageSize);
        Page<OrdersDto> ordersDtoPage=new Page<>();
        //查询用户的订单信息
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId,id);
        this.page(pageInfo,queryWrapper);
        //查询订单信息后拷贝page属性，忽略查询数据
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        //获取page里的查询到的订单数据
        List<Orders> ordersRecords=pageInfo.getRecords();
        //把订单数据循环给dtolist，其中还需要订单明细表
        List<OrdersDto> list=ordersRecords.stream().map(item->{
            OrdersDto dto=new OrdersDto();
            //把查到的订单表给拷贝给dto
            BeanUtils.copyProperties(item,dto);
            //获取订单id
            Long ordid=item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper2=new LambdaQueryWrapper<>();
            //使用订单id查订单里的明细
            queryWrapper2.eq(OrderDetail::getOrderId,ordid);
            List<OrderDetail> orderDetailList=ordersDetailService.list(queryWrapper2);
            //查到后给dto
            dto.setOrderDetails(orderDetailList);
            return dto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);

        return ordersDtoPage;
    }

    /**
     * 再来一单
     *
     */
    @Override
    @Transactional
    public void again(Orders orders) {
        Long ordersId=orders.getId();//拿到订单id
        Orders orders1=this.getById(ordersId);//拿到userid
        Long userid=orders1.getUserId();
        //根据传来的订单id构造条件
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,ordersId);
        //找出订单里的订单明细.
        List<OrderDetail> orderDetailList=ordersDetailService.list(queryWrapper);
        for (OrderDetail item:orderDetailList
             ) {
            //把菜品信息给购物车对象，然后调用添加购物车
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCartService.addCar(shoppingCart,userid);
        }
    }
}
