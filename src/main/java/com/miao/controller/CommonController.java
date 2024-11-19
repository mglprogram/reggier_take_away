package com.miao.controller;

import com.miao.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 缪广亮
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException { //file的参数名要和前端传过来的name的属性值一样
//        file是一个临时文件，需要转存到指定位置
        log.info(file.toString());
//        原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        使用UUID重新生成文件名,防止文件名重复
        String fileName = UUID.randomUUID().toString() + suffix;
//        创建一个目录对象
        File dir = new File(basePath);
//        判断当前目录是否存在
        if (!dir.exists()) {
//            目录不存在，创建
            dir.mkdirs();
        }


//        transferTo：将临时文件传输至指定位置
        file.transferTo(new File(basePath+fileName));
        return Result.success(fileName);

    }

    /**
     * 文件下载
     * @param name
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response) throws IOException {
//        输入流，通过输入流读取文件内容：从客户端读取服务端的文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
//        输出流，通过输出流将文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();

    }
}
