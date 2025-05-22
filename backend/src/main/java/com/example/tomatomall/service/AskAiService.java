package com.example.tomatomall.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AskAiService {

    private final WebClient webClient;
    private static final String API_URL = "/compatible-mode/v1/chat/completions";
    private static final String MODEL_NAME = "deepseek-r1";
    private static final String API_KEY = ""; // 应放在环境变量或配置文件中

    public AskAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dashscope.aliyuncs.com").build();
    }

    public String askAi(String prompt) {
        // 构造 messages 数组
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 200);

        // 发起请求并处理响应
        return webClient.post()
                .uri(API_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        Mono.error(new RuntimeException("API 请求失败，状态码：" + response.statusCode())))
                .bodyToMono(String.class)
                .blockOptional()
                .orElse("AI 未返回任何响应");
    }
}
