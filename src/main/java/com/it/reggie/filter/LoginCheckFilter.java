package com.it.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查是否登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static  final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //获取请求路径
        String URL=request.getRequestURI();
        //不需要处理的路径
        /*
        String[] urls=new String[]{
                //请求路径
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/addressBook/**"
        };
         */


        String[] UserUrls=new String[]{
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/addressBook/**",
                "/shoppingCart/**",
                "/category/**",
                "/dish/**",
                "/order/**"

        };

        String[] EmpUrls=new String[]{
                "/employee/**",
                "/backend/**",
                "/common/**",
                "/addressBook/**",
                "/category/**",
                "/dish/**",
                "/order/**",
                "/user/login",
                "/user/sendMsg"

        };


        String[] login=new String[]{
                "/user/login",
                "/user/sendMsg",
                "/employee/login",
                "/backend/page/login/**",
                "/backend/index.html",
                "/front/**"
        };



        //放行路径
        boolean check = check(login, URL);
        //未登录过滤
        if (check){
            log.info("本次请求{}不需要处理",URL);
            filterChain.doFilter(request,response);
            return ;
        }




        //检查员工是否已经登录
        if(request.getSession().getAttribute("employee")!=null){
            log.info("员工已登录，ID为{}",request.getSession().getAttribute("employee"));
            Long emp = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(emp);
            if (check(EmpUrls,URL))
            {
                log.info("员工通过:{}",URL);
                filterChain.doFilter(request,response);
                return;
            }
            return;
        }else if(request.getSession().getAttribute("user")!=null){ //检查用户是否登录
            log.info("用户已登录，ID为{}",request.getSession().getAttribute("user"));
            Long userid = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);
            if (check(UserUrls,URL))
            {
                log.info("用户通过:{}",URL);
                filterChain.doFilter(request,response);
                return;
            }
            return;
        }else {
                log.info("用户和员工未登录");
                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            }
    }
    //遍历请求路径
    public boolean check(String[] urls,String URL){
        for (String url:urls)
        {
            boolean match = PATH_MATCHER.match(url, URL);
            if (match){
                return true;
            }
        }
        return false;
    }




}
