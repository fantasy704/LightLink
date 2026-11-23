import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath } from 'url'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
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
      "/admin": {
        target: "http://localhost:8081",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/admin/, ''),
      }
    },
    allowedHosts: ['frp-gap.com', 'frp-oil.com', 'frp-six.com', 'frp-use.com']
  }
})
