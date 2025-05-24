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
    const status = updateCouponStatusInfo.status;

    return axios.patch(`${COUPON_MODULE}/${couponId}/status`, null,
        { params: {status: status } })
        .then(res => {
            return res
        })
}
export const deleteTheCoupon = (couponId: number) => {
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
export const getUsableCoupons = (orderTotal: number) => {
    return axios.get(`${COUPON_MODULE}/user`,
        { params: {orderTotal: orderTotal } })
        .then(res => {
            return res
        })
}

/********************************* 辅助函数 *********************************/
export function getTypeText(type: string): string {
    switch (type) {
        case 'FIXED':
            return '礼金';
        case 'PERCENTAGE':
            return '折扣';
        case 'THRESHOLD':
            return '满减';
        default:
            return type;
    }
}
export function getStatusText(status: string): string {
    switch (status) {
        case 'ACTIVE':
            return '可用';
        case 'INACTIVE':
            return '禁用';
        case 'EXPIRED':
            return '已过期';
        default:
            return status;
    }
}
export function formatStringToDate(string: string): Date {
    return new Date(string)
}
export function formatDateToString(date: Date): string {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();

    function pad(num: number): string {
        if (num < 10) return '0' + num.toString();
        return num.toString();
    }
    return `${year}-${pad(month)}-${pad(day)}T${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
}