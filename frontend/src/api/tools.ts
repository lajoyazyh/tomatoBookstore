import { axios } from '../utils/request'
import { TOOL_MODULE } from './_prefix'

// 上传图片
export const uploadImage = (payload: any) => {
    return axios.post(`${TOOL_MODULE}/image`, payload, {
        headers: {
            'Content-Type': "multipart/form-data;"
        }
    }).then(res => {
        return res
    })
        .catch(error => {
            console.error("上传图片失败:", error);
            throw error; // 错误向上抛出
        });
}