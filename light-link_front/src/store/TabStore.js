import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import VueCookies from 'vue-cookies'

export const useTabStore = defineStore('tabStore', {
    state: () => {
        return {
            value: 0
        }
    },
    actions: {
        toggleTab(tab){
            this.value = tab
        }
    }
})