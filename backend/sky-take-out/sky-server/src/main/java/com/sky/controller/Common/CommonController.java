package com.sky.controller.Common;
import com.sky.constant.MessageConstant;
import com.sky.dto.SearchDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/common")
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private HttpMessageConverters messageConverters;
    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传{}" ,file);
        try {
            String originalFilename = file.getOriginalFilename();
            originalFilename = originalFilename.substring(originalFilename.lastIndexOf("."));
            String originalname=UUID.randomUUID().toString()+originalFilename;
            String path=aliOssUtil.upload(file.getBytes(),originalname);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
