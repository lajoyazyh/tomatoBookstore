package com.example.tomatomall.controller;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.CouponVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private TokenUtil tokenUtil;
    @PostMapping
    public Response createCoupon(@RequestBody CouponVO couponVO) {
        CouponVO createdCoupon = couponService.createCoupon(couponVO);
        return Response.buildSuccess(createdCoupon);
    }
    @GetMapping("/{couponId}")
    public Response getCouponById(@PathVariable Integer couponId) {
        CouponVO couponVO = couponService.getCouponById(couponId);
        if (couponVO != null) {
            return Response.buildSuccess(couponVO);
        } else {
            return Response.buildFailure("400", "优惠券不存在");
        }
    }
    @GetMapping
    public Response getAllCoupons() {
        List<CouponVO> couponVOList = couponService.getAllCoupons();
        return Response.buildSuccess(couponVOList);
    }

    @PutMapping("/{couponId}")
    public Response updateCoupon(@PathVariable Integer couponId, @RequestBody CouponVO couponVO) {
        CouponVO updatedCoupon = couponService.updateCoupon(couponId, couponVO);
        if (updatedCoupon != null) {
            return Response.buildSuccess(updatedCoupon);
        } else {
            return Response.buildFailure("400", "优惠券不存在");
        }
    }

    @DeleteMapping("/{couponId}")
    public Response deleteCoupon(@PathVariable Integer couponId) {
        boolean deleted = couponService.deleteCoupon(couponId);
        if (deleted) {
            return Response.buildSuccess("删除成功");
        } else {
            return Response.buildFailure("400", "优惠券不存在");
        }
    }

    @PatchMapping("/{couponId}/status")
    public Response updateCouponStatus(@PathVariable Integer couponId, @RequestParam String status) {
        CouponVO updatedCoupon = couponService.updateCouponStatus(couponId, status);
        if (updatedCoupon != null) {
            return Response.buildSuccess(updatedCoupon);
        } else {
            return Response.buildFailure("400", "优惠券不存在");
        }
    }
    @PostMapping("/{couponId}/receive")
    public Response receiveCoupon(@RequestHeader("token") String token, @PathVariable Integer couponId) {
        //  从token中获取用户名
        Integer userId= tokenUtil.getAccount(token).getId();
        couponService.receiveCoupon(userId, couponId);
        return Response.buildSuccess("领取成功");
    }

    @GetMapping("/user")
    public Response<List<CouponVO>> getApplicableCouponsForOrder(
            @RequestHeader("token") String token,
            @RequestParam BigDecimal orderTotal) {
        Integer userId = tokenUtil.getAccount(token).getId();
        List<CouponVO> applicableCoupons = couponService.getApplicableCouponsForUser(userId, orderTotal);
//        for (CouponVO coupon : applicableCoupons) {
//            System.out.println(coupon.getCouponName());
//        }
        return Response.buildSuccess(applicableCoupons);
    }
}