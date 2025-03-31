import axios from 'axios'

// 创建一个axios的实例service
const service = axios.create()

// 登录判断
function hasToken() {
    return !(sessionStorage.getItem('token') == '')
}


// 设置为全局变量
export {
    service as axios
}
