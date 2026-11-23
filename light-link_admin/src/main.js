import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import request from './utils/Request.js'


const app = createApp(App)
app.config.globalProperties.request = request
app.mount('#app')
