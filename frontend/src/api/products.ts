import { axios } from '../utils/request'
import { PRODUCT_MODULE } from './_prefix'

export type Specification = {
    id: string,
    item: string,
    value: string,      // 规格内容
    productId: string,  // 规格对应商品的id
}

export type Stockpile = {
    id: string,
    productId: string,
    amount: number,         // 可售库存
    frozen: number,         // 不售库存
}

export type UpdateProductInfo = {
    id: string,
    title?: string,
    price?: number,         // 不低于0
    rate?: number,          // 0 ~ 10
    description?: string,
    cover?: string,         // 封面url
    detail?: string,
    specifications?: Specification[],
}

export type CreatProductInfo = {
    title: string,
    price: number,         // 不低于0
    rate: number,          // 0 ~ 10
    description?: string,
    cover?: string,        // 封面url
    detail?: string,
    specifications?: Specification[],
}

export type UpdateStockpileInfo = {
    productId: string,
    amount: number,
}

export const getAllProducts = () => {
    return axios.get(`${PRODUCT_MODULE}`)
        .then(res => {
            return res
        })
}
export const getTheProduct = (id : string) => {
    return axios.get(`${PRODUCT_MODULE}/${id}`)
        .then(res => {
            return res
        })
}
export const updateProductInfo = (updateProductInfo: UpdateProductInfo) => {
    return axios.put(`${PRODUCT_MODULE}`, updateProductInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}
export const createProduct = (createProductInfo: CreatProductInfo) => {
    return axios.post(`${PRODUCT_MODULE}`, createProductInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}
export const deleteProduct = (id: string) => {
    return axios.delete(`${PRODUCT_MODULE}/${id}`)
        .then(res => {
            return res
        })
}
export const updateStockpile = (updateStockpileInfo: UpdateStockpileInfo) => {
    return axios.patch(`${PRODUCT_MODULE}/stockpile/${updateStockpileInfo.productId}`, updateStockpileInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}
export const getStockpile = (productId: string) => {
    return axios.get(`${PRODUCT_MODULE}/stockpile/${productId}`)
        .then(res => {
            return res
        })
}