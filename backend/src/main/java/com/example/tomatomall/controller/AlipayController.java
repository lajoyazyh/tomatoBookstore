package com.example.tomatomall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.serverUrl}")
    private String serverUrl;

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Value("${alipay.signType}")
    private String signType;

    @Value("${alipay.appPrivateKey}")
    private String privateKey;

    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    private static final String FORMAT = "JSON";
    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    private static final String CHARSET_UTF8 = "utf-8";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;
    /**
     * 处理支付宝异步通知
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @throws IOException  IO异常
     */
    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // 1. 解析支付宝回调参数
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));


        // 2. 验证支付宝签名(这里!!!!!!!!)
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", signType);
        } catch (AlipayApiException e) {
            System.err.println("Alipay signature verification failed: " + e.getMessage());
            e.printStackTrace(); // 打印完整的异常堆栈
            out.print("fail");
        }

        // 3. 处理业务逻辑
        if (signVerified) {
            System.out.println("Alipay signature verification success!");
            String orderId = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount");
            String tradeNo = params.get("trade_no"); // 支付宝交易号

//            System.out.println("Order ID: " + orderId);
//            System.out.println("Trade Status: " + tradeStatus);
//            System.out.println("Total Amount: " + totalAmount);
//            System.out.println("Trade No: " + tradeNo);

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                try {
                    orderService.handlePaymentSuccess(Integer.parseInt(orderId), tradeNo, totalAmount);
                    out.print("success");
                } catch (Exception e) {
                    System.err.println("Error occurred while handling order: " + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                    out.print("fail");
                }
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                try{
                    orderService.closeOrder(orderId);
                    out.print("success");
                } catch(Exception e){
                    System.err.println("Error occurred while closing order: " + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                    out.print("fail");
                }

            } else {
                System.out.println("Received unknown trade status: " + tradeStatus + " for orderId: " + orderId);
                out.print("success"); // 避免支付宝重复通知
            }
        } else {
            System.err.println("Alipay signature verification failed!哭了");
            out.print("fail");
        }
        out.flush();
        out.close();

    }

    /**
     * 支付宝同步跳转接口
     * @return 支付成功页面
     */
    @GetMapping("/returnUrl")
    public String returnUrl() {
        return "支付成功了，请稍后查看订单状态。";
    }
}