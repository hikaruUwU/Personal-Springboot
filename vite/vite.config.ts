import {defineConfig, type ProxyOptions} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'


const proxy: Record<string, ProxyOptions> = {
    '/api': {
        target: 'http://192.168.0.115:8080',
        changeOrigin: true,
        ws: true,
        rewrite: path => path.replace(/^\/api/, ''),
    }
}

export default defineConfig({
    plugins: [
        vue(),
        // viteCompression({
        //     verbose: true,
        //     disable: false,
        //     threshold: 10240,
        //     algorithm: 'gzip',
        // }),
        AutoImport(
            {
                resolvers: [
                    ElementPlusResolver()
                ]
            }
        ),
        Components(
            {
                resolvers: [
                    ElementPlusResolver()
                ]
            }
        )
    ],
    server: {
        proxy: proxy
    },
    build:{
        emptyOutDir: true,
        //outDir: '../src/main/resources/static'
        //outDir: 'dist'
    }
})
