import axios from 'axios'

// 创建一个axios的实例service
const service = axios.create()

// 登录判断
function hasToken() {
    return !(sessionStorage.getItem('token') == '')
}

//当前实例的拦截器，对所有要发送给后端的请求进行处理，在其中加入token
service.interceptors.request.use(
    config => {
        if(hasToken()) {
            config.headers['token'] = sessionStorage.getItem('token')
        }
        return config
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
)

//当前实例的拦截器，对所有从后端收到的请求进行处理，检验http的状态码
service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response;
        } else {
            return Promise.reject();
        }
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
)

// 设置为全局变量
export {
    service as axios
}
