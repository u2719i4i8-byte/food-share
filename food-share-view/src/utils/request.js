/**
 * Axios请求封装
 * 功能：创建axios实例、配置请求拦截器、自动添加token
 */
import axios from "axios"
import { getToken } from "@/utils/storage.js";

// API基础路径
const URL_API = '/api/food-share-sys/v1.0'

// 创建axios实例
const request = axios.create({
  baseURL: URL_API,
  timeout: 8000
});

/**
 * 请求拦截器
 * 自动在请求头中添加token
 */
request.interceptors.request.use(config => {
  const token = getToken();
  if (token !== null) {
    config.headers["token"] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default request;
