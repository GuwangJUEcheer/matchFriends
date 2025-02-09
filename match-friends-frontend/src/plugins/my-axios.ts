import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';

// 定义 Axios 实例
const myaxios: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api', // 修正拼写错误: locahost -> localhost
  timeout: 10000, // 超时时间 (注意: 100000ms 太长了, 一般设为 10s)
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 允许跨域携带 Cookies
});

// 请求拦截器
myaxios.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 示例: 在请求头中添加 Token
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers = { ...config.headers, Authorization: `Bearer ${token}` };
    // }
    return config;
  },
  (error: AxiosError) => {
    console.error('请求拦截错误:', error);
    return Promise.reject(error);
  }
);
