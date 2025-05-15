package com.example.tomatomall.controller;

import com.example.tomatomall.service.ToolsService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tools")
public class ToolsController {

    @Autowired
    private ToolsService toolsService;

    @PostMapping("/image")
    public Response<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = toolsService.uploadImage(file);
            return Response.buildSuccess(imageUrl);
        } catch (Exception e) {
            return Response.buildFailure("400", e.getMessage());
        }
    }
}