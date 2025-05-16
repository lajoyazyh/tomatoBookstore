package com.example.tomatomall.controller;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.vo.CouponVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
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
}