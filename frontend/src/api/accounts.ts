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
export const userInfo = (username: string) => {
    // 从 sessionStorage 中获取 token
    const token = sessionStorage.getItem('token');

    // 检查 token 是否存在
    if (!token) {
        console.error("Token not found in sessionStorage");
        return Promise.reject(new Error("Token not found"));
    }

    // 将 token 添加到请求头中
    return axios.get(`${ACCOUNT_MODULE}/${username}`, {
        headers: {
            token: token
        }
    }).then(res => {
        return res;
    });
};

// 创建用户
export const userRegister = (registerInfo: RegisterInfo) => {
    return axios.post(`${ACCOUNT_MODULE}`, registerInfo, { headers: { 'Content-Type': 'application/json' } })
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
    return axios.put(`${ACCOUNT_MODULE}`, updateInfo, { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}