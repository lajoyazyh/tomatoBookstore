package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Coupon;
import com.example.tomatomall.po.UserCoupon;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.CouponRepository;
import com.example.tomatomall.repository.UserCouponRepository;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    TokenUtil tokenUtil;
    @Override
    public CouponVO createCoupon(CouponVO couponVO) {
        Coupon coupon = couponVO.toPO();
        Coupon savedCoupon = couponRepository.save(coupon);
        return savedCoupon.toVO();
    }
    @Override
    public CouponVO getCouponById(Integer couponId) {
        Optional<Coupon> couponOptional = couponRepository.findById(couponId);
        return couponOptional.map(Coupon::toVO).orElse(null);
    }
    @Override
    public List<CouponVO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream().map(Coupon::toVO).collect(Collectors.toList());
    }
    @Override
    public CouponVO updateCoupon(Integer couponId, CouponVO couponVO) {
        Optional<Coupon> existingCouponOptional = couponRepository.findById(couponId);
        if (existingCouponOptional.isPresent()) {
            Coupon updatedCoupon = couponVO.toPO();
            updatedCoupon.setCouponId(couponId); // 确保ID一致
            Coupon savedCoupon = couponRepository.save(updatedCoupon);
            return savedCoupon.toVO();
        }
        return null;
    }
    @Override
    public boolean deleteCoupon(Integer couponId) {
        if (couponRepository.existsById(couponId)) {
            couponRepository.deleteById(couponId);
            return true;
        }
        return false;
    }
    @Override
    public CouponVO updateCouponStatus(Integer couponId, String status) {
        Optional<Coupon> existingCouponOptional = couponRepository.findById(couponId);
        if (existingCouponOptional.isPresent()) {
            Coupon existingCoupon = existingCouponOptional.get();
            existingCoupon.setStatus(status);
            Coupon savedCoupon = couponRepository.save(existingCoupon);
            return savedCoupon.toVO();
        }
        return null;
    }
    @Transactional
    public void receiveCoupon(Integer userId, Integer couponId) {
        // 1. 验证优惠券是否存在
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("优惠券不存在"));

        // 2. 验证优惠券是否有效
        if (!"ACTIVE".equals(coupon.getStatus()) || coupon.getValidFrom().isAfter(LocalDateTime.now()) || coupon.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("优惠券不可领取");
        }


//        // 3. 验证用户是否已经领取过该优惠券
//        if (userCouponRepository.findByUserIdAndCouponId(userId, couponId).isPresent()) {
//            throw new IllegalArgumentException("您已领取过该优惠券");
//        }

        // 4. 创建 UserCoupon 记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCoupon.setStatus("UNUSED");
        userCouponRepository.save(userCoupon);
    }
    @Override
    public List<CouponVO> getApplicableCouponsForUser(Integer userId, BigDecimal orderTotal) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 获取用户所有未使用的优惠券记录
        List<UserCoupon> userUnusedCoupons = userCouponRepository.findByUserIdAndStatus(userId, "UNUSED");

        // 2. 筛选出有效的优惠券 (ACTIVE 状态, 在有效期内, 满足最低消费金额)
        return userUnusedCoupons.stream()
                .map(UserCoupon::getCoupon) // 获取关联的 Coupon 对象
                .filter(coupon -> coupon != null &&
                        "ACTIVE".equals(coupon.getStatus()) &&
                        coupon.getValidFrom().isBefore(now) &&
                        coupon.getValidUntil().isAfter(now) &&
                        (coupon.getMinPurchaseAmount() == null || orderTotal.compareTo(coupon.getMinPurchaseAmount()) >= 0))
                .map(Coupon::toVO) // 转换为 CouponVO
                .collect(Collectors.toList());
    }
}