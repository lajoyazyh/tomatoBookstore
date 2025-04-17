package com.example.tomatomall.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    private OrderRepository orderRepository;

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

    private static final String ALIPAY_TRADE_PAGE_PAY = "alipay.trade.page.pay";
    private static final String CHARSET_UTF8 = "utf-8";
    private static final String FORMAT_JSON = "JSON";
    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";

    @PostMapping("/{orderId}/pay")
    public Map<String, Object> payOrder(@PathVariable String orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("msg", "Order not found");
            return errorResponse;
        }

        String outTradeNo = String.valueOf(order.getOrderId());
        String totalAmount = String.valueOf(order.getTotalAmount());
        String paymentMethod = order.getPaymentMethod();
        String subject = "TomatoMall订单支付";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

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

        String sign = null;
        try {
            sign = AlipaySignature.rsaSign(params.toString(), privateKey, CHARSET_UTF8, signType);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        params.put("sign", sign);

        String formData = buildFormData(params);

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

        formContent.append("<input type=\"submit\" value=\"立即支付\">"); // 保留按钮，让用户点击
        formContent.append("</form>");
        //  移除自动提交的 JavaScript 代码
        return formContent.toString();
    }
}