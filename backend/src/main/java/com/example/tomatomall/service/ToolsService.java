package com.example.tomatomall.service;

import org.springframework.web.multipart.MultipartFile;

public interface ToolsService {
    String uploadImage(MultipartFile file) throws Exception;
}
