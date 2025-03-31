import { axios } from '../utils/request'
import { ACCOUNT_MODULE } from './_prefix'

type LoginInfo = {
    username: string,
    password: string
}

type RegisterInfo = {
    username: string,
    password: string,
    name: string,
    avatar?: string,
    role: string,
    telephone?: string,
    email?: string,
    location?: string,
}

type UpdateInfo = {
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