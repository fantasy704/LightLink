import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import VueCookies from 'vue-cookies'

const defaultSetting = {
    language: 'zh-CN',
    usingAdvancedNetwork: false,
    httpAddress: "https://frp-use.com:62257",
    wsAddress: "https://frp-use.com:62257"
}

export const useSettingStore = defineStore('settingStore', {
    state: () => {
        return {
            setting: null
        }
    },
    actions: {
        saveSetting(setting){
            this.setting = setting
            VueCookies.set('setting', setting)
        },
        loadSetting(){
            if(!this.setting){
                const cookie = VueCookies.get('setting')
                if(cookie){
                    this.setting = cookie
                }else{
                    this.setting = defaultSetting
                    this.saveSetting(this.setting)
                }
            }
            return this.setting
        },
        setLanguage(language){
            const setting = this.loadSetting()
            setting.language = language
            this.saveSetting(setting)
            VueCookies.set('setting', setting)
        },
        toggleAdvancedNet(){
            const setting = this.loadSetting()
            setting.usingAdvancedNetwork = !setting.usingAdvancedNetwork
            this.saveSetting(setting)
            VueCookies.set('setting', setting)
        }
    }
})