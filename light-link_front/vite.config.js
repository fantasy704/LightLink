import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'url'
import vue from '@vitejs/plugin-vue'
import basicSsl from '@vitejs/plugin-basic-ssl'

// https://vite.dev/config/
export default defineConfig({
  plugins: [basicSsl(), vue({
      template: {
        compilerOptions: {
          //允许s-开头的自定义组件
          isCustomElement: (tag) => tag.startsWith('s-')
        }
      }
  })],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    hmr: true,
    proxy: {
      "/api": {
        target: "http://127.0.0.1:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
      "/api/websocket": {
        target: "ws://127.0.0.1:8080",
        ws: true,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      }
    },
    allowedHosts: ['frp-gap.com', 'frp-oil.com', 'frp-six.com', 'frp-use.com']
  }
})