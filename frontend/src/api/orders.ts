import { axios } from '../utils/request'
import {CART_MODULE, ORDER_MODULE} from "./_prefix.ts";

export type addressInfo = {
    name: string,
    telephone: string,
    postcode: string,
    address: string,
}
export type checkoutOrderInfo = {
    cartItemIds: number[],
    shipping_address: addressInfo,
    payment_method: string,
    // couponId: number
}

export const checkoutOrder = (orderInfo: checkoutOrderInfo) => {
    return axios.post(`${CART_MODULE}/checkout`, orderInfo)
        .then(res => {
            return res
        })
}
export const getOrders = () => {
    return axios.get(`${ORDER_MODULE}`)
        .then(res => {
            return res
        })
}
export const payForOrder = (orderId: number) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/pay`)
        .then(res => {
            return res
        })
}
// 支付完成后在后端处理支付回调