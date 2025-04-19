import { axios } from '../utils/request'
import { CART_MODULE } from "./_prefix.ts";

type newProductInfo = {
    productId: number,
    quantity: number,
}
type updateCartItemInfo = {
    cartItemId: number,
    quantity: number,
}

export const addNewProduct = (newProduct: newProductInfo) => {
    return axios.post(`${CART_MODULE}`, newProduct)
        .then(res => {
            return res
        })
}

export const deleteProduct = (cartItemId: number) => {
    return axios.delete(`${CART_MODULE}/${cartItemId}`)
        .then(res => {
            return res
        })
}

export const updateCartItem = (updateCartItemInfo: updateCartItemInfo) => {
    return axios.patch(`${CART_MODULE}/${updateCartItemInfo.cartItemId}?quantity=${updateCartItemInfo.quantity}`)
        .then(res => {
            return res
        })
}

export const getCart = () => {
    return axios.get(`${CART_MODULE}`)
        .then(res => {
            return res
        })
}