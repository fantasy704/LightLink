import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import VueCookies from 'vue-cookies'

export const useLoginStore = defineStore('loginStore', {
    state: () => {
        return {
            loginUser: null
        }
    },
    actions: {
        setLoginUser(user){
            this.loginUser = user
        },
        clear(){
            this.loginUser = null
        }
    }
})