import axios from 'axios';

// 创建 Axios 实例
const myaxios = axios.create({
  baseURL: 'http://localhost:8080/api', // 修正拼写错误: locahost -> localhost
  timeout: 100000, // 超时时间
  headers: {
    'Content-Type': 'application/json', // 默认请求类型为 JSON
  },
  withCredentials: true, // 允许跨域携带 Cookies
});

// 请求拦截器
myaxios.interceptors.request.use(
  (config) => {
    // 在发送请求之前做处理，例如添加 Authorization Token
    // const token = localStorage.getItem('token'); // 从本地存储中获取 Token
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`; // 设置 Authorization 头
    // }
    return config;
  },
  (error) => {
    // 请求错误处理
    return Promise.reject(error);
  }
);

// 响应拦截器
myaxios.interceptors.response.use(
  (response) => {
    // 对响应数据进行处理
    return response.data; // 返回处理后的数据
  },
  (error) => {
    // 对响应错误进行处理
    if (error.response) {
      const { status } = error.response;
      if (status === 401) {
        console.error('Unauthorized, redirecting to login...');
        window.location.href = '/login'; // 跳转到登录页面
      } else if (status === 500) {
        console.error('Server error, please try again later.');
      }
    } else {
      console.error('Network error or request timeout.');
    }
    return Promise.reject(error);
  }
);

export default myaxios;
