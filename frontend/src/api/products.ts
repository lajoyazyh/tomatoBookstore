import { axios } from '../utils/request'
import { PRODUCT_MODULE } from './_prefix'

export type Specification = {
    id: number,
    item: string,
    value: string,      // 规格内容
    productId: number,  // 规格对应商品的id
}

export type Stockpile = {
    id: number,
    productId: number,
    amount: number,         // 可售库存
    frozen: number,         // 不售库存
}

export type UpdateProductInfo = {
    id: number,
    title?: string,
    price?: number,         // 不低于0
    rate?: number,          // 0 ~ 10
    description?: string,
    cover?: string,         // 封面url
    detail?: string,
    specifications?: Specification[],
}

export type CreateProductInfo = {
    title: string,
    price: number,         // 不低于0
    rate: number,          // 0 ~ 10
    description?: string,
    cover?: string,        // 封面url
    detail?: string,
    specifications?: Specification[],
}

export type UpdateStockpileInfo = {
    productId:  number,
    amount: number,
}

export const getAllProducts = () => {
    return axios.get(`${PRODUCT_MODULE}`)
        .then(res => {
            return res
        })
}
export const getTheProduct = (id : number) => {
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
export const createProduct = (createProductInfo: CreateProductInfo) => {
    return axios.post(`${PRODUCT_MODULE}`, createProductInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}
export const deleteProduct = (id: number) => {
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
export const getStockpile = (productId: number) => {
    return axios.get(`${PRODUCT_MODULE}/stockpile/${productId}`)
        .then(res => {
            return res
        })
}