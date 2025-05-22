package com.example.tomatomall.controller;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.Account;


import com.example.tomatomall.service.AskAiService;
import com.example.tomatomall.vo.AskAiRequest;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
public class AskAiController {

    @Resource
    AskAiService askAiService;

    @Autowired
    private TokenUtil tokenUtil;


    @PostMapping("/ask")
    public Response askAi(@RequestBody AskAiRequest request) {
        String response = askAiService.askAi(request.getPrompt());
        return Response.buildSuccess(response);
    }
}
