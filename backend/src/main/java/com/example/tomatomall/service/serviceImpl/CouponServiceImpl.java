package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Coupon;
import com.example.tomatomall.repository.CouponRepository;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    TokenUtil tokenUtil;
    @Autowired
    private CouponRepository couponRepository;
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
}