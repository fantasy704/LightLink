<template>
    <s-page :theme="themeStore.value" class="page">
        <router-view></router-view>
    </s-page>
</template>

<script setup>
    import VueCookies from 'vue-cookies'
    import {onMounted, getCurrentInstance} from 'vue'
    const { proxy } = getCurrentInstance();
    import { useThemeStore } from './store/ThemeStore';
    const themeStore = useThemeStore();
    import { useLoginStore } from '@/store/LoginStore.js'
    const loginStore = useLoginStore()

    const autologin = async () => {
        if(!VueCookies.get('tokenuser')){
            return
        }
        let result = await proxy.request.get({
            url: '/account/autoLogin'
        })
        if(!result){
            return
        }
        loginStore.setLoginUser(result.data)
        VueCookies.set('tokenuser', result.data.token)
    }
    if(VueCookies.get('tokenuser')){
        autologin()
    }
    onMounted(() => {
        
    })
</script>

<style lang="scss">
*{
    margin: 0;
    padding: 0;
}
.page{
    width: 100vw;
    height: 100vh;
    padding: 0;
    margin: 0;
}
</style>