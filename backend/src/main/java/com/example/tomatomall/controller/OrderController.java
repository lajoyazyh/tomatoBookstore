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
import com.example.tomatomall.vo.OrderAllResponse;
import com.example.tomatomall.vo.OrderResponse;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

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

    private static final String ALIPAY_TRADE_PAGE_PAY = "alipay.trade.page.pay";
    private static final String CHARSET_UTF8 = "utf-8";
    private static final String FORMAT_JSON = "JSON";
    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping()
    public Response getOrders(@RequestHeader("token") String token) {
        try {
            // 使用 token 获取用户的订单列表
            OrderResponse orderResponse = orderService.getOrders(token);
            return Response.buildSuccess(orderResponse);
        } catch (IllegalArgumentException e) {
            // 处理非法参数异常，如用户不存在等
            return Response.buildFailure("400", e.getMessage());
        } catch (Exception e) {
            // 处理其他服务器错误
            return Response.buildFailure("500", "服务器错误");
        }
    }
    /**
     * 创建订单并返回支付表单
     * @param orderId  订单ID
     * @return  包含支付表单数据的Map
     */
    @PostMapping("/{orderId}/pay")
    public Response payOrder(@PathVariable Integer orderId) {
        // 根据订单ID查询订单
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            return Response.buildFailure("400", "Order not found");
        }

        // 创建 AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId,
                privateKey, FORMAT_JSON, CHARSET_UTF8, alipayPublicKey, signType);

        // 创建 AlipayTradePagePayRequest
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        // 构建业务参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", String.valueOf(order.getOrderId()));
        bizContent.put("total_amount", String.valueOf(order.getTotalAmount()));
        bizContent.put("subject", "TomatoMall订单支付");
        bizContent.put("product_code", PRODUCT_CODE);
        request.setBizContent(bizContent.toString());
        // 调用 SDK 生成支付表单
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return Response.buildFailure("500", "Failed to generate Alipay payment form");
        }

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("paymentForm", form);
        responseData.put("orderId", String.valueOf(order.getOrderId()));
        responseData.put("totalAmount", String.valueOf(order.getTotalAmount()));
        responseData.put("paymentMethod", order.getPayment_method() != null ? order.getPayment_method() : "Alipay"); // 从订单获取支付方式

        return Response.buildSuccess(responseData);
    }

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
        Map<String, String> params = new HashMap<>();
        try {
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse Alipay notify parameters: " + e.getMessage());
            out.print("fail");
        }


        // 2. 验证支付宝签名(这里!!!!!!!!)
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
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

            System.out.println("Order ID: " + orderId);
            System.out.println("Trade Status: " + tradeStatus);
            System.out.println("Total Amount: " + totalAmount);
            System.out.println("Trade No: " + tradeNo);

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
}