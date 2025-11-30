import instance from './axiosInstance.ts'
import type {AxiosResponse} from "axios";
import {notifyError} from '../util/noticePopup.ts'

interface Data<T> {
    code: number,
    data: T
    message: string
}

async function calling<T>(promise: Promise<AxiosResponse<T>>): Promise<T | null> {
    try {
        const res = await promise
        assertCode(res.data)
        return res.data
    } catch (err) {
        notifyError(err as string)
        return null
    }
}

function assertCode(data: unknown) {
    const maybe = data as Partial<Data<unknown>>;

    if (maybe && maybe.code != 200)
        throw new Error(maybe.message || 'Error occurred');
}

export function login({account, password}: { account: string; password: string }) {
    return calling<Data<boolean>>(
        instance.post('/login', {
            'account': account,
            'password': password
        })
    )
}


