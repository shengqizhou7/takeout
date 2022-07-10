package com.sailfinn.reggie.controller;

import com.sailfinn.reggie.common.R;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * file upload and download
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //引用配置文件里的path作为转存地址前缀
    @Value("${reggie.path}")
    private String basePath;

    /**
     * file upload
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    //parameter name should be consistent with what page-name shows
    public R<String> upload(MultipartFile file) {
        //file is a temporary file, needs to be saved, or will be deleted after this request
        log.info(file.toString());

        //obtain original file name. Be careful of duplicate file names
        String originalFilename = file.getOriginalFilename();
        //obtain file suffix, eg. ".jpg"
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //generate file names by UUID in case of duplicate file names
        String fileName = UUID.randomUUID().toString() + suffix;

        //create a directory object
        File dir = new File(basePath);
        //check if current dir exists
        if (!dir.exists()) {
            //create dir
            dir.mkdirs();
        }

        try {
            //save temporary file to designated path
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * file download
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {



        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
