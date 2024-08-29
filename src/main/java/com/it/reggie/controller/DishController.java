package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.*;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 增加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo=new Page(page,pageSize);//装查询结果
        Page<DishDto> dishDtoPage=new Page();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getCreateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝                                                      //records存放查询出来的数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//忽略records

        List<Dish> records = pageInfo.getRecords();//获取查询的Dish数据
        //records没有分类的名称
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //拷贝，把item里的数据拷贝至dishDto
            BeanUtils.copyProperties(item,dishDto);
            //获取item里的分类ID
            Long categoryId = item.getCategoryId();
            //查询分类对象
            Category category=categoryService.getById(categoryId);
            String categoryName=category.getName();
            //把分类输入进dishdto
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    };

    /**
     * 更新菜品界面的查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        /**
         * log.info("查询菜品信息");
         *         Dish dish = dishService.getById(id);//获取菜品对象
         *         //通过菜品id查询口味
         *         DishFlavor dishFlavor=dishFlavorService.getById(dish.getId());
         *         log.info(dishFlavor.toString());
         *         //把口味给返回对象
         */
        DishDto dishDto=dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 查询分类下的菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")//当请求的参数只有部分参数时，不需要@RequestBody，传来的是完整对象的时候需要@RequestBody。
    public R<List<DishDto>> dishlist(Dish dish){
        //条件查询
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //起售为1的菜
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);//查到的菜品信息
        //返回前台的信息缺少
        List<DishDto> dishDtoList=list.stream().map((item)->{
            DishFlavor dishFlavor=new DishFlavor();
            DishDto dishDto=new DishDto();
            //拷贝，把item里的数据拷贝至dishDto
            BeanUtils.copyProperties(item,dishDto);
            //获取item里的菜品id
            Long dishId = item.getId();
            //查询根据菜品id查口味
           LambdaQueryWrapper<DishFlavor> queryWrapper1 =new LambdaQueryWrapper<>();
           queryWrapper1.eq(DishFlavor::getDishId,dishId);
           //把查到的菜品给dto
            dishDto.setFlavors(dishFlavorService.list(queryWrapper1));
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }



    /**
     * 修改停售信息，前端的POST请求方法是：
     *     url: `/dish/status/${params.status}`,
     *     method: 'post',
     *     params: { id: params.id }
     *     --------------------------------------------------------------
     *      ${params.status}是将params对象中的status属性的值动态插入到URL路径中，生成最终的请求路径。例如，如果params.status的值为0，则最终的请求路径为/dish/status/0。
     *      这里的params属性表示要发送的查询参数。它使用了对象字面量语法，将id属性的值从params对象中取出，并作为查询参数发送。例如，如果params.id的值为1664680353836576769，则请求将包含查询参数?id=1664680353836576769。
     *      ---------------------------------------------------------------
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateDish(@PathVariable("status") Integer status, @RequestParam("id") List<String> ids){
        /**
         * Dish dish =dishService.getById(id);
         *         dish.setStatus(status);
         *         dishService.updateById(dish);
         */
        for (String item : ids){
            Dish dish=dishService.getById(item);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        //return R.success("删除成功");

        return R.success("删除成功");
    }
}
