import { axios } from '../utils/request'
import { ORDER_MODULE } from "./_prefix.ts";

type addressInfo = {
    name: string,
    telephone: string,
    postCode?: string,
    address: string,
}
type checkoutOrderInfo = {
    cartItemIds: number[],
    shipping_address: addressInfo,
    payment_method: string,
}

export const checkoutOrder = (orderInfo: checkoutOrderInfo) => {
    return axios.post(`${ORDER_MODULE}/checkout`, orderInfo)
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