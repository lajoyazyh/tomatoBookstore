import { axios } from '../utils/request'
import { ACCOUNT_MODULE } from './_prefix'

export type LoginInfo = {
    username: string,
    password: string
}

export type RegisterInfo = {
    username: string,   // 唯一
    password: string,
    name: string,       // 姓名
    avatar?: string,    // 头像url
    role: string,
    telephone?: string, // 1开头，共11位
    email?: string,     // 需要符合email格式
    location?: string,
}

export type UpdateInfo = {
    username: string,
    password?: string,
    name?: string,
    avatar?: string,
    role?: string,
    telephone?: string,
    email?: string,
    location?: string,
}

// 获取用户详情
export const userInfo = () => {
    return axios.get(`${ACCOUNT_MODULE}`)
        .then(res => {
            return res
        })
}

// 创建用户
export const userRegister = (registerInfo: RegisterInfo) => {
    return axios.post(`${ACCOUNT_MODULE}/register`, registerInfo, { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}

// 登录
export const userLogin = (loginInfo: LoginInfo) => {
    return axios.post(`${ACCOUNT_MODULE}/login`, null, { params: loginInfo })
        .then(res => {
            return res
        })
}

// 更新用户信息
export const userInfoUpdate = (updateInfo: UpdateInfo) => {
    return axios.post(`${ACCOUNT_MODULE}`, updateInfo, { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}