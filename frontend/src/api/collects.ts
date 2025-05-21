import { axios } from '../utils/request'
import { COLLECTS_MODULE } from "./_prefix.ts";

export type CollectInfo = {
    id: number,     // 收藏本身的id，创建时自动产生
    userId: number,
    productId: number
}

// 用户进行收藏
export const createCollectOf = (productId: number) => {
    return axios.post(`${COLLECTS_MODULE}`,
        { params: {productId: productId } })
        .then(res => {
            return res;
        })
}

// 获取当前用户所有收藏（理论上来说，管理员没有看特定用户的收藏的需要，所以这里只写获取当前用户的收藏就行了）
export const getUserCollects = () => {
    return axios.get(`${COLLECTS_MODULE}`)
        .then(res => {
            return res;
        })
}
// "data": [
//     {
//         "id": 2,
//         "userId": 1,
//         "productId": 5
//     }
// ]

// 根据收藏id进一步获取具体内容
export const getTheCollect = (id: number) => {
    return axios.get(`${COLLECTS_MODULE}/${id}`)
        .then(res => {
            return res;
        })
}
