import { axios } from '../utils/request'
import { BLOCK_MODULE } from "./_prefix.ts";

// 拉黑用户
export const blockTheUser = (userId: number) => {
    return axios.post(`${BLOCK_MODULE}`,
        { params: {userId: userId } })
        .then(res => {
            return res;
        })
}
// 获取被拉黑用户
export const getBlockedUsers = () => {
    return axios.get(`${BLOCK_MODULE}`)
        .then(res => {
            return res;
        })
}
// "data": [
//     {
//         "id": 2,
//         "userId": 1,
//         "userName": "zzc"
//     }
// ]

// 判断用户是否被拉黑
export const isBlockedUser = (userId: number) => {
    return axios.get(`${BLOCK_MODULE}/${userId}`)
        .then(res => {
            return res;
        })
}
// 取消拉黑
export const cancelUserBlocker = (userId: number) => {
    return axios.delete(`${BLOCK_MODULE}`,
        { params: {userId: userId } })
        .then(res => {
            return res;
        })
}