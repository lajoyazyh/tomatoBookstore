// OrderServiceImpl.java
package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    @Autowired
    private TokenUtil tokenUtil;


    @Override
    @Transactional
    public Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String paymentMethod) {
        // 1.  验证cartItemIds是否为空
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("cartItemIds不能为空");
        }
        // 2. 获取用户购物车商品，并验证商品是否存在
        List<Cart> carts = cartRepository.findAllById(cartItemIds);
        if (carts.size() != cartItemIds.size()) {
            throw new IllegalArgumentException("购物车商品不存在");
        }

        // 3. 验证用户是否一致, 并获取用户ID
        Integer userId = null;
        for (Cart cart : carts) {
            if (!cart.getAccount().getUsername().equals(username)) {
                throw new IllegalArgumentException("用户不一致");
            }
            // 从token中获取的username 和 cart 中的username 是一致的，这里只需要获取一次
            if (userId == null) {
                userId = cart.getAccount().getId();
            }
        }

        // 4. 验证库存，计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Stockpile stockpile = stockpileRepository.findByProductId(cart.getProduct().getId());
            if (stockpile == null || stockpile.getAmount() < cart.getQuantity()) {
                throw new IllegalArgumentException("商品" + cart.getProduct().getTitle() + "库存不足");
            }
            totalAmount = totalAmount.add(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        // 5. 创建订单
        Order order = new Order();
        order.setUserId(userId); // 使用上面获取的userId
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING"); // 初始状态为待支付
        order.setCreateTime(new Date());
        orderRepository.save(order);

        // 6.  删除购物车中已下单的商品  这里先不删除，后面支付成功再删除
        //cartRepository.deleteAll(carts);

        return order;
    }

}