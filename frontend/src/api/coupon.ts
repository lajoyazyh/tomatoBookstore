import { axios } from '../utils/request'
import { COUPON_MODULE } from "./_prefix.ts";

export type CreateCouponInfo = {
    couponName: string,
    couponType: string,
    discountAmount?: number     // when type is 'FIXED' or 'THRESHOLD'
    discountPercentage?: number // when type is 'PERCENTAGE'
    minPurchaseAmount?: number  // when type is 'THRESHOLD'
    validFrom: string,          // example: 2024-01-01T00:00:00
    validUntil: string,            // example: 2024-01-31T23:59:59
    status?: string,
    description?: string
}
export type UpdateCouponInfo = {
    couponId: number,
    couponName?: string,
    couponType?: string,
    discountAmount?: number     // when type is 'FIXED' or 'THRESHOLD'
    discountPercentage?: number // when type is 'PERCENTAGE'
    minPurchaseAmount?: number  // when type is 'THRESHOLD'
    validFrom?: string,          // example: 2024-01-01T00:00:00
    validUntil?: string,            // example: 2024-01-31T23:59:59
    status?: string,
    description?: string
}
export type UpdateCouponStatusInfo = {
    couponId: number,
    status: string,
}

/********************************* 管理员 *********************************/
export const createCoupon = (createCouponInfo: CreateCouponInfo) => {
    return axios.post(`${COUPON_MODULE}`, createCouponInfo)
        .then(res => {
            return res
        })
}
export const getTheCoupon = (couponId: number) => {
    return axios.get(`${COUPON_MODULE}/${couponId}`)
        .then(res => {
            return res
        })
}
export const getAllCoupons = () => {
    return axios.get(`${COUPON_MODULE}`)
        .then(res => {
            return res
        })
}
export const updateCoupon = (updateCouponInfo: UpdateCouponInfo) => {
    const couponId = updateCouponInfo.couponId;
    // 多余的后端会忽略的

    return axios.put(`${COUPON_MODULE}/${couponId}`, updateCouponInfo)
        .then(res => {
            return res
        })
}
export const updateCouponStatus = (updateCouponStatusInfo: UpdateCouponStatusInfo) => {
    const couponId = updateCouponStatusInfo.couponId;

    return axios.patch(`${COUPON_MODULE}/${couponId}/status`,
        { params: {couponId: couponId } })
        .then(res => {
            return res
        })
}
export const deleteCoupon = (couponId: number) => {
    return axios.delete(`${COUPON_MODULE}/${couponId}`)
        .then(res => {
            return res
        })
}

/********************************* 用户 *********************************/
export const receiveCoupon = (couponId: number) => {
    return axios.post(`${COUPON_MODULE}/${couponId}/receive`)
        .then(res => {
            return res
        })
}