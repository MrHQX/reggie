package com.it.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.dto.UserDto;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.utils.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailSender emailSender;


    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> senMsg(@RequestBody User user, HttpSession session){
        //手机短信要钱，就用邮箱代替了，诶。。。。。。。
        String email = user.getPhone();
        String code=EmailSender.generateVerificationCode();
        //code="123456";//固定验证码测试
        session.setAttribute("code",code.toString());//key,value
        EmailSender.senEmail(email,code);
        return R.success("已发送接受");
    }

    /**
     * 登录处理
     * @param userDto
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody UserDto userDto,HttpSession session){
        User user=new User();
        String verifyemail=(String) userDto.getPhone();
        String verifycode =(String) userDto.getCode();//获取登录传入的邮箱和验证码
        String correctcode = (String) session.getAttribute("code");//拿到正确的验证码
        //log.info("code是:{}", verifycode);
        //log.info("correctcode是:{}",correctcode);
        //log.info("email是{}",verifyemail);
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,verifyemail);
        if (userService.getOne(queryWrapper)==null)
        {
            log.info(user.toString()+"不存在");
            user.setPhone(verifyemail);
            user.setName("用户"+EmailSender.generateVerificationCode());
            user.setStatus(1);
            userService.save(user);
            return R.error("没有注册已自动注册");
        }else {
            user=userService.getOne(queryWrapper);

        }
        Long userid=user.getId();
        //查找用户

        //log.info("correctemail是{}",correctemail);
        /**
         *  if (useremail==email&&code==Code)
         *         {
         *
         *             return R.success("登录成功");
         *
         *         }else {
         *             return R.error("用户名或密码错误");
         *         }
         */
        if (correctcode!=null&&correctcode.equals(verifycode))
        {
            //验证码正确
                String correctemail = user.getPhone();//把数据库的邮箱取出
                session.setAttribute("user",userid);//放进session里
                return R.success("登录成功");//登录成功
        }else {
            return R.error("用户名或验证码错误");
        }
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清楚session里的用户
        request.getSession().removeAttribute("user");

        return R.success("退出成功");
    }
}
