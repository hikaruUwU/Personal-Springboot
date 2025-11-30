import {ElMessage} from 'element-plus'
//import 'element-plus/es/components/message/style/css';

import('element-plus/es/components/message/style/css')

export const notifyError = (content: string) => {
    ElMessage(
        {
            showClose: true,
            message: content,
            type: 'error',
        }
    )
}

export const notifySuccess = (content: string) => {
    ElMessage(
        {
            showClose: true,
            message: content,
            type: 'success',
        }
    )
}