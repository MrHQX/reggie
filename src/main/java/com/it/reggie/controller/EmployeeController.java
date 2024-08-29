package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Employee;
import com.it.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //MD5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());//Employee::getUsername="username"。查传进来的employee是否有username
        Employee emp = employeeService.getOne(queryWrapper);
        //查询结果
        if (emp==null)
        {
            return R.error("没有查询到用户");
        }
        //密码对比，如果密码不对。
        if (!emp.getPassword().equals(password))//密码不相等则true
        {
            return R.error("密码错误");
        }
        //用户是否被封禁
        if(emp.getStatus()==0)
        {
            return R.error("用户已被封禁");
        }
        request.getSession().setAttribute("employee",emp.getId());
        return  R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清楚session里的用户
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }


    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的ID
        //Long empid  = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empid);
        //employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增成功");
    }



    @GetMapping("/page")
    public R<Page> pageR(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page pageInfo=new Page(page,pageSize);//装查询结果
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);
        //查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //修改员工因袭
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){//前端传的JSON就使用RequstBody
        Long empID  = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empID);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }
    //根据ID查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("查无此人");
    }
}
