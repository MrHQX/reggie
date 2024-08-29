package com.it.reggie.controller;

import com.it.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//文件上传下载
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    //上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//MultipartFile XXX需要与请求的name保持一致,file是一个临时文件，需要转存到其他位置
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();

        //originalFilename.lastIndexOf(".")找到变量最后一位“.”的索引位置
        // originalFilename.substring（索引位置），从索引位置开始向后截取
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成文件名
        String fileName = UUID.randomUUID().toString() + suffix;
        //检查目录是否存在
        File dir=new File(basePath);
        if (!dir.exists()){
            //没有目录就创建
            dir.mkdir();
        }
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    //下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流，读取文件
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));

            //输出流，通过输出把文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len=0;//len是读取的长度
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1)//-1代表读取完毕
            {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出流，通过输出把文件写回浏览器

        /*
        FileInputStream：
FileInputStream是Java IO库中的一个类，用于从文件中读取字节数据。它的构造函数接受一个File对象作为参数，表示要读取的文件。在代码中，通过new FileInputStream(new File(basePath+name))创建了一个FileInputStream对象，用于读取要下载的文件内容。

HttpServletResponse.getOutputStream()：
HttpServletResponse是Java Servlet API中的一个类，用于表示HTTP响应。getOutputStream()是HttpServletResponse类的方法之一，用于获取输出流，可以将数据写回到客户端浏览器。在代码中，通过调用response.getOutputStream()获取一个ServletOutputStream对象outputStream，用于将文件内容写回浏览器。

ServletOutputStream.write(byte[], int, int)：
ServletOutputStream是Java Servlet API中的一个类，继承自OutputStream，用于将字节数据写入输出流。write(byte[], int, int)方法用于将指定长度的字节数据写入输出流。在代码中，通过outputStream.write(bytes,0,len)将从文件读取的字节数据写入输出流。

InputStream.read(byte[])：
InputStream是Java IO库中抽象类，用于从输入流中读取字节数据。read(byte[])方法是InputStream类的方法之一，用于从输入流中读取数据，并将读取的字节数据存储到给定的字节数组中。在代码中，通过fileInputStream.read(bytes)读取文件数据，并将读取的字节数据存储到bytes数组中。

OutputStream.flush()：
OutputStream是Java IO库中抽象类，用于将字节数据写入输出流。flush()方法是OutputStream类的方法之一，用于将缓冲区中的数据强制刷新到目标设备中。在代码中，通过outputStream.flush()方法刷新输出流，确保数据被写入目标设备。
         */

    }
}
