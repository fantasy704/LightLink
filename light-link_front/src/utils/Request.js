import axios from 'axios'
import VueCookies from 'vue-cookies'
import { ElLoading } from 'element-plus'
import Message from './Message'
import { useSettingStore } from '../store/SettingStore'
import { t } from './I18NUtil'

const responseTypeJson = 'json'
const postContentType = 'application/form-data'

let loading = null;

const PROXY_URL = '/api'

const instance = axios.create({
    withCredentials: true,
    timeout: 30 * 1000
})

instance.interceptors.request.use(config => {
    if(config.baseURL !== useSettingStore().loadSetting().httpAddress){
        config.baseURL = useSettingStore().loadSetting().httpAddress
    }
    if(!useSettingStore().loadSetting().usingAdvancedNetwork){
        config.baseURL = PROXY_URL
    }
    if(config.showLoading){
        loading = ElLoading.service({
            lock: true,
            text: 'Loading......',
            background:'rgba(0, 0, 0, 0.5)'
        })
    }
    return config;
},error => {
    if(error.config.showLoading && loading){
        loading.close();
    }
    Message.error('Network Error!')
    return Promise.reject('Network Error!')
})

instance.interceptors.response.use(
    response => {
        const { showLoading, errorCallback, showError = true, responseType } = response.config;
        if(showLoading && loading){
            loading.close();
        }
        const responseData = response.data;
        if(responseType === 'arraybuffer' || responseType === 'blob'){
            return responseData;
        }
        if(responseData.code === 200){
            return responseData;
        }else if(responseData.code === 901){
            Message.error(t('common.notLoggedIn'))
            return Promise.reject({showError: false})
        }else{
            if(errorCallback){
                errorCallback(responseData);
            }
            return Promise.reject({showError, msg: responseData.msg})
        }

    },
    error => {
        if (error.config.showLoading && loading) {
            loading.close();
        }
        return Promise.reject({ showError: true, msg: "Network Error!" })
    }
)

const get = async config => {
    const {url, params, showLoading = false, responseType = responseTypeJson, errorCallback, showError = true} = config
    const token = VueCookies.get('tokenuser');
    let query = new URLSearchParams(); // 修改这里
    for(let key in params){
        query.append(key, params[key] === undefined ? "" : params[key])
    }
    let headers = {
        'tokenuser': token
    }
    return instance.get(url,{
        headers: headers,
        params: query,
        responseType: responseType,
        showLoading: showLoading,
        showError: showError,
        errorCallback: errorCallback
    }).catch(error => {
        if(error && error.showError){
            Message.error(error.msg)
        }
        return null;
    })
}

const post = config => {
    const {url, params, dataType, showLoading = false, responseType = responseTypeJson, errorCallback, showError = true} = config
    let formData = new FormData();
    let contentType = postContentType;
    for(let key in params){
        formData.append(key, params[key] === undefined ? "" : params[key]);
    }
    if (dataType != null && dataType === 'json') {
        contentType = 'application/json';
    }
    const token = VueCookies.get('token');
    let headers = {
        'Content-Type': contentType,
        'X-Requested-With': 'XMLHttpRequest',
        'token': token
    }
    return instance.post(url, formData, {
        responseType: responseType,
        headers: headers,
        showLoading: showLoading,
        showError: showError,
        errorCallback: errorCallback
    }).catch(error => {
        if (error.showError) {
            Message.error(error.msg);
        }
        return null;
    });
}

export default {
    get,
    post
}