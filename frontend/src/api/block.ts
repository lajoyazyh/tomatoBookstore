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

export const getUsers = (partialUsername: String) => {
    return axios.get(`api/accounts/find` ,{
        params:{
            partialUsername: partialUsername,
        }
    })
        .then(res => {
            return res
        })
}

// export const getUsers = () => {
//     return axios.get(`api/accounts`)
//         .then(res => res)
// }

/** 拉黑用户请求数据结构 */
export type BlockUserRequest = {
    userId: number
}

/**
 * 拉黑指定用户接口
 * 请求方法：POST
 * 请求路径：/api/blocks
 * 请求参数：通过 URL 参数传递 userId，例如：/api/blocks?userId=123
 * @param blockUserRequest 包含需要拉黑的用户 ID
 * @returns Promise 对象，解析后返回后端响应
 */
export const blockUser = (blockUserRequest: BlockUserRequest) => {
    return axios.post(`${BLOCK_MODULE}`, null, { params: { userId: blockUserRequest.userId } })
        .then(res => res)
}

/**
 * 取消拉黑指定用户接口
 * 请求方法：POST
 * 请求路径：/api/blocks/unblock
 * 请求参数：通过 URL 参数传递 userId，例如：/api/blocks/unblock?userId=123
 * @param blockUserRequest 包含需要取消拉黑的用户 ID
 * @returns Promise 对象，解析后返回后端响应
 */
export const unblockUser = (blockUserRequest: BlockUserRequest) => {
    return axios.delete(`${BLOCK_MODULE}`, { params: { id: blockUserRequest.userId } })
        .then(res => res)
}


/**
 * 判断某一用户是否被拉黑接口
 * 请求方法：GET
 * 请求路径：/api/blocks/{userId}
 * 参数：userId 必须，表示要判断的用户ID
 */
export const checkUserBlockedStatus = (userId: number) => {
    return axios.get(`${BLOCK_MODULE}/${userId}`)
        .then(res => res)
}