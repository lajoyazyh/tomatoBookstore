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

import static java.lang.Character.FORMAT;

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
    public Response payOrder(@PathVariable Integer orderId, HttpServletResponse httpResponse) throws Exception {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            return Response.buildFailure("400", "Order not found");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId,
                privateKey, FORMAT_JSON, CHARSET_UTF8, alipayPublicKey, signType);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", String.valueOf(order.getOrderId()));
        bizContent.put("total_amount", String.valueOf(order.getTotalAmount()));
        bizContent.put("subject", "TomatoMall订单支付");
        bizContent.put("product_code", PRODUCT_CODE);
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        httpResponse.setContentType("text/html;charset=" + CHARSET_UTF8);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("orderId", order.getOrderId());
        responseData.put("totalAmount", order.getTotalAmount());
        responseData.put("paymentMethod", "Alipay"); // 或者从订单中获取
        // 注意：这里不再需要包含 paymentForm，因为表单已经直接输出到 HttpServletResponse

        return Response.buildSuccess(responseData);
    }

    private String buildFormData(Map<String, String> params) {
        StringBuilder formContent = new StringBuilder();
        formContent.append("<form name=\"punchout_form\" method=\"post\" action=\"").append(serverUrl).append("?charset=").append(CHARSET_UTF8);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            formContent.append("&").append(key).append("=").append(value); // 不再进行 URL 编码
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
        Map<String, String> params = new HashMap<>();
        try {
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = String.join(",", values);
                params.put(name, valueStr);
            }
            System.out.println("Alipay Notify Parameters: " + params); // 记录接收到的参数
        } catch (Exception e) {
            System.err.println("Failed to parse Alipay notify parameters: " + e.getMessage());
            out.print("fail");
            return;
        }

        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", signType); // 启用签名验证
            System.out.println("Alipay Signature Verification Result: " + signVerified); // 记录验签结果
        } catch (AlipayApiException e) {
            System.err.println("Alipay signature verification failed: " + e.getMessage());
            out.print("fail");
            return;
        }

        if (signVerified) {
            String orderId = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount");
            String tradeNo = params.get("trade_no");

            System.out.println("Order ID: " + orderId + ", Trade Status: " + tradeStatus + ", Total Amount: " + totalAmount + ", Trade No: " + tradeNo);

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                try {
                    orderService.handlePaymentSuccess(Integer.parseInt(orderId), tradeNo, totalAmount); // 处理支付成功的业务逻辑
                    out.print("success"); // 返回纯文本 "success"
                    return;
                } catch (Exception e) {
                    System.err.println("Error occurred while handling payment success for order " + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                    out.print("fail"); // 返回纯文本 "fail"
                    return;
                }
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                try {
                    orderService.closeOrder(orderId); // 处理交易关闭的业务逻辑
                    out.print("success"); // 返回纯文本 "success"
                    return;
                } catch (Exception e) {
                    System.err.println("Error occurred while closing order " + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                    out.print("fail"); // 返回纯文本 "fail"
                    return;
                }
            } else {
                System.out.println("Received unknown trade status: " + tradeStatus + " for orderId: " + orderId);
                out.print("success"); // 对于其他状态，也返回 success，避免支付宝重试
                return;
            }
        } else {
            System.err.println("Alipay signature verification failed!");
            out.print("fail"); // 返回纯文本 "fail"
            return;
        }
    }
}
