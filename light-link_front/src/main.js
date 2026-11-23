import { createApp } from 'vue'
import './style.css'
import 'sober'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router/index.js'
import Message from "@/utils/Message.js";
import Request from './utils/Request.js'
import Confirm from './utils/Confirm.js'
import Dialog from './components/Dialog.vue'
import ElementPlus from 'element-plus'
import { t } from './utils/I18NUtil.js'
import { useSettingStore } from './store/SettingStore'
import 'element-plus/dist/index.css'


const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
useSettingStore(pinia).loadSetting()
app.use(ElementPlus)

app.use(router)
app.component('Dialog', Dialog)

app.config.globalProperties.message = Message
app.config.globalProperties.request = Request
app.config.globalProperties.confirm = Confirm;
app.config.globalProperties.t = t;

app.mount('#app')
