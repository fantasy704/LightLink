import axios from 'axios'
import VueCookies from 'vue-cookies'

const responseTypeJson = 'json'
const postContentType = 'application/form-data'

const instance = axios.create({
    withCredentials: true,
    baseURL: '/admin',
    timeout: 30 * 1000
})

instance.interceptors.request.use(config => {
    return config;
},error => {
    return Promise.reject('Network Error!')
})

instance.interceptors.response.use(
    response => {
        const { errorCallback, showError = true, responseType } = response.config;
        const responseData = response.data;
        if(responseType === 'arraybuffer' || responseType === 'blob'){
            return responseData;
        }
        if(responseData.code === 200){
            return responseData;
        }else if(responseData.code === 901){
            return Promise.reject({showError: false})
        }else{
            if(errorCallback){
                errorCallback(responseData);
            }
            return Promise.reject({showError, msg: responseData.msg})
        }

    },
    error => {
        return Promise.reject({ showError: true, msg: "Network Error!" })
    }
)

const get = async config => {
    const {url, params, responseType = responseTypeJson, errorCallback, showError = true} = config
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
        showError: showError,
        errorCallback: errorCallback
    }).catch(error => {
        return null;
    })
}

const post = config => {
    const {url, params, dataType, responseType = responseTypeJson, errorCallback, showError = true} = config
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
        showError: showError,
        errorCallback: errorCallback
    }).catch(error => {
        return null;
    });
}

export default {
    get,
    post
}