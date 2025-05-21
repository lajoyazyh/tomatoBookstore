import { axios } from '../utils/request'
import { COMMENT_MODULE } from "./_prefix.ts";

export type CommentInfo = {
    id: number,     // 评论的id，创建时自动产生
    rating: number,
    content: string,
    productId: number
}
export type CreateCommentInfo = {
    productId: number,
    rating: number,
    content: string
}

// 获取指定商品的评论
export const getCommentsOf = (productId: number) => {
    return axios.get(`${COMMENT_MODULE}`,
        { params: {productId: productId } })
        .then(res => {
            return res;
        });
}

// 删除指定评论
export const deleteTheComment = (commentId: number) => {
    return axios.delete(`${COMMENT_MODULE}/${commentId}`)
        .then(res => {
            return res;
        })
}

// 用户添加评论
export const createComment = (commentInfo: CreateCommentInfo) => {
    const productId = commentInfo.productId;
    const createCommentInfoBody = {
        rating: commentInfo.rating,
        content: commentInfo.content
    };

    return axios.post(`${COMMENT_MODULE}`, createCommentInfoBody,
        { params: {productId: productId } })
        .then(res => {
            return res;
        })
}