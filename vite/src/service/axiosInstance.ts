import type {AxiosInstance, AxiosResponse, InternalAxiosRequestConfig} from 'axios'
import axios from 'axios'

const instance: AxiosInstance = axios.create({
    baseURL: '/api/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
    responseType: 'json',
})

instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        config.withCredentials = true
        return config
    },
    (error) => {
        return Promise.reject(error);
    }
)

instance.interceptors.response.use(
    (response: AxiosResponse<any>): AxiosResponse => {
        return response
    },
    (error) => {
        return Promise.reject(error)
    }
)

export default instance