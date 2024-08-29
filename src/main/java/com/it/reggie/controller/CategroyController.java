package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Category;
import com.it.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategroyController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> pageR(int page,int pageSize){
        Page pageInfo=new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category) {
        String type= String.valueOf(category.getType());
        log.info(type);

        if (type=="1"){
            categoryService.save(category);
            return R.success("分类新增成功");
        }
        else {
            categoryService.save(category);
            return R.success("套餐新增成功");
        }
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除的ID是：{}",ids);
        categoryService.remove(ids);
        //categoryService.removeById(ids);
        return R.success("成功删除");
    }
    @PutMapping
    public R<String> upddate(@RequestBody Category category)
    {
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //lambdaQueryWrapper.eq(Category::getType,category.getType());
        //(条件，where type，==type）
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);


        List<Category> categories=categoryService.list(lambdaQueryWrapper);
        return R.success(categories);
    }


}
