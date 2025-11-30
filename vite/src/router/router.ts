import type {RouteRecordRaw} from 'vue-router'
import {createRouter, createWebHashHistory} from 'vue-router'
import {defineComponent, h} from 'vue'
import NProgress from 'nprogress'
import ('nprogress/nprogress.css')

import Login from "../component/Login.vue";
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'index',
        component: Login
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'fallback',
        component: defineComponent({
            render() {
                return h('div', 'fallback')
            }
        })
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach((_t, _f, next) => {
    NProgress.start()
    next()
})
router.afterEach(() => {
    NProgress.done()
})

export default router
