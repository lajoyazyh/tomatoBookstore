import { axios } from '../utils/request'
import { BLOCK_MODULE } from './_prefix'

export type User = {
    id: number,
    username: string,
    email: string,
    role: string,
    isBlocked: boolean  // true 表示已被拉黑，false 表示正常状态
}

export type GetUsersParams = {
    search?: string
}

/**
 * 获取普通用户列表接口（管理员视角）
 * @param params 查询参数，可选 search 参数用于关键词搜索
 * @returns Promise 对象，解析后返回后端响应
 */

// export const getUsers = (params: GetUsersParams = {}) => {
//     return axios.get(`${BLOCK_MODULE}`, { params })
//         .then(res => {
//             return res
//         })
// }

export const getUsers = () => {
    return axios.get(`api/accounts`)
        .then(res => res)
}

/** 拉黑用户请求数据结构 */
export type BlockUserRequest = {
    userId: number
}

/**
 * 拉黑指定用户接口
 * @param blockUserRequest 包含需要拉黑的用户ID
 * @returns Promise 对象，解析后返回后端响应
 */
export const blockUser = (blockUserRequest: BlockUserRequest) => {
    return axios.post(`${BLOCK_MODULE}/blockUser`, blockUserRequest, { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}
