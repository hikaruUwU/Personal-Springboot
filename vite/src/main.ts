import {createApp} from 'vue'

import './assets/public.scss'
import App from './App.vue'
import pinia from './pinia/pinia.ts'
import router from './router/router.ts'
import {i18n} from "./i18n/i18n.ts";
// import 'element-plus/dist/index.css';

createApp(App).use(router).use(pinia).use(i18n).mount('#app')
