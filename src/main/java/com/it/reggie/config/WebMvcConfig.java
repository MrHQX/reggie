package com.it.reggie.config;

import com.it.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
    静态资源映射
    **是通配符
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射启动！！！！");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        //**是通配符
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }


    //扩展mvc框架的消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("消息转换器。。ON");
        //创建新的消息转换器
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        //设置对象转换器，使用jackson吧java转json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的转换器对象追加到mvc框架的容器集合中
        converters.add(0,messageConverter);
    }
}
