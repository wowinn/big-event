package com.yangzhou.controller;

import com.yangzhou.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class UpLoadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //把文件的内容存储到本地磁盘中
        String originalFilename = file.getOriginalFilename();
        file.transferTo(new File("files/" + originalFilename));
        return Result.success();
    }
}
