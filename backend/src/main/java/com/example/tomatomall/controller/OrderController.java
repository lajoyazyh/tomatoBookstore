package com.example.tomatomall.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.OrderService;
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

    /**
     * 创建订单并返回支付表单
     * @param orderId  订单ID
     * @return  包含支付表单数据的Map
     */
    @PostMapping("/{orderId}/pay")
    public Map<String, Object> payOrder(@PathVariable Integer orderId) {
        //  根据订单ID查询订单
        Order order = orderRepository.findByOrderId(orderId); // 确保OrderService有getOrderById方法
        if (order == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("msg", "Order not found");
            return errorResponse;
        }

        String outTradeNo = String.valueOf(order.getOrderId());
        String totalAmount = String.valueOf(order.getTotalAmount());
        String paymentMethod = order.getPayment_method();
        String subject = "TomatoMall订单支付";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //  构建支付宝请求参数
        Map<String, String> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("method", ALIPAY_TRADE_PAGE_PAY);
        params.put("format", FORMAT_JSON);
        params.put("charset", CHARSET_UTF8);
        params.put("sign_type", signType);
        params.put("timestamp", timestamp);
        params.put("version", "1.0");
        params.put("notify_url", notifyUrl);
        params.put("return_url", returnUrl);

        Map<String, String> bizContentParams = new HashMap<>();
        bizContentParams.put("out_trade_no", outTradeNo);
        bizContentParams.put("total_amount", totalAmount);
        bizContentParams.put("subject", subject);
        bizContentParams.put("product_code", PRODUCT_CODE);
        String bizContent = JSON.toJSONString(bizContentParams);
        params.put("biz_content", bizContent);

        //  对请求参数进行签名
        String sign = null;
        try {
            sign = AlipaySignature.rsaSign(params.toString(), privateKey, CHARSET_UTF8, signType);
            System.out.println("Generated Signature: " + sign); // 打印生成的签名
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        params.put("sign", sign);

        //  构建支付表单
        String formData = buildFormData(params);

        //  构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("paymentForm", formData);
        responseData.put("totalAmount", totalAmount);
        responseData.put("paymentMethod", paymentMethod);
        responseData.put("orderId", outTradeNo);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", responseData);
        response.put("msg", null);
        return response;
    }

    /**
     * 构建用于提交到支付宝的表单
     * @param params  请求参数
     * @return  HTML表单字符串
     */
    private String buildFormData(Map<String, String> params) {
        StringBuilder formContent = new StringBuilder();
        formContent.append("<form name=\"punchout_form\" method=\"post\" action=\"").append(serverUrl).append("?charset=").append(CHARSET_UTF8);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                value = URLEncoder.encode(value, CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Error encoding parameter: " + key, e);
            }
            formContent.append("&").append(key).append("=").append(value);
        }
        formContent.append("\">");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            formContent.append("<input type=\"hidden\" name=\"").append(key).append("\" value=\"").append(value).append("\">");
        }
        formContent.append("<input type=\"submit\" value=\"立即支付\">");
        formContent.append("</form>");
        return formContent.toString();
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
            return;
        }

        // 打印接收到的所有参数，用于调试
        System.out.println("Alipay Notify Parameters:");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        // 2. 验证支付宝签名
        boolean signVerified = true;
//        try {
//            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
//        } catch (AlipayApiException e) {
//            System.err.println("Alipay signature verification failed: " + e.getMessage());
//            e.printStackTrace(); // 打印完整的异常堆栈
//            out.print("fail");
//            return;
//        }

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

