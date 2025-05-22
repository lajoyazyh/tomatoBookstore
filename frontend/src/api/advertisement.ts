import { axios } from '../utils/request'
import {ADVERTISE_MODULE} from "./_prefix.ts";

export type updateAdvertiseInfo = {
    id: number,
    title: string,
    content?: string,
    imgUrl?: string,
    productId: number
}

export type createAdvertiseInfo = {
    title: string,
    content?: string,
    imgUrl?: string,
    productId: number
}

//获取所有广告信息
export const getAllAdvertisement = () => {
    return axios.get(`${ADVERTISE_MODULE}`)
        .then(res => {
            return res
        })
}

//更新广告信息
export const updateAdvertisementsInfo = (updateAdvertiseInfo: updateAdvertiseInfo) => {
    return axios.put(`${ADVERTISE_MODULE}`, updateAdvertiseInfo,
        { headers: {'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}

//创建广告
export const createAdvertisement = (createAdvertiseInfo: createAdvertiseInfo) => {
    return axios.post(`${ADVERTISE_MODULE}`, createAdvertiseInfo,
        { headers: {'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}

//删除广告
export const deleteAdvertisement = (id: number) => {
    return axios.delete(`${ADVERTISE_MODULE}/${id}`)
        .then(res => {
            return res
        })
}