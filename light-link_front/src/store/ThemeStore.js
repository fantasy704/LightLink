import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import VueCookies from 'vue-cookies'

export const useThemeStore = defineStore('themeStore', {
    state: () => {
        return {
            value: 'light'
        }
    },
    actions: {
        init(){
            this.value = VueCookies.get('theme') || 'light';
        },
        toggleTheme(){
            this.value = this.value === 'dark' ? 'light' : 'dark';
            VueCookies.set('theme', this.value);
        }
    }
})