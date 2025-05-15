package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.configure.OSSConfig;
import com.example.tomatomall.service.ToolsService;
import com.example.tomatomall.vo.Response;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class ToolsServiceImpl implements ToolsService {

    @Autowired
    private OSS ossClient;
    @Autowired
    private OSSConfig ossConfig;

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        // 验证文件类型和大小
        validateFile(file);

        // 生成唯一文件名
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // 上传到OSS
        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(new PutObjectRequest(ossConfig.getBucketName(), fileName, inputStream));
        } catch (IOException e) {
            throw new IOException("文件上传失败", e);
        }

        // 返回访问URL
        return ossClient.generatePresignedUrl(ossConfig.getBucketName(), fileName, new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365)).toString();
    }

    private void validateFile(MultipartFile file) {
        // 验证大小
        if (file.getSize() > 5 * 1024 * 1024) { // 限制为5MB
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 验证类型
        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new IllegalArgumentException("仅支持JPEG和PNG格式的图片");
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        // 生成唯一的文件名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + fileExtension;
    }
}