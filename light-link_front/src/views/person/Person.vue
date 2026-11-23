<template>
<div class="container">
    <Introduce/>
    <div class="loginButtonBox" v-if="loginStore.loginUser === null">
        <s-button class="btn" @click="loginPanelConfig.show = true"> {{ proxy.t('person.login') }} </s-button>
        <s-button class="btn" @click="applyPanelConfig.show = true"> {{ proxy.t('person.applyAccount') }} </s-button>
        <div style="height: 10px"></div>
        <s-button class="btn" @click="settingPanelConfig.show = true" type="outlined">{{ proxy.t('person.clientSetting') }}</s-button>
    </div>
    <div class="userInfoBox" v-else>
        <div class="title">{{ proxy.t('person.profile') }}</div>
        <div class="infoItem">
            <span class="label">{{ proxy.t('person.username') }}</span>
            <span class="value">{{ loginStore.loginUser.name }}</span>
        </div>
        <div class="infoItem">
            <span class="label">{{ proxy.t('person.uid') }}</span>
            <span class="value">{{ loginStore.loginUser.uid }}</span>
        </div>
        <div class="infoItem">
            <span class="label">{{ proxy.t('person.email') }}</span>
            <span class="value">{{ loginStore.loginUser.email }}</span>
        </div>
        <s-button class="logoutBtn" @click="logout">{{ proxy.t('person.logout') }}</s-button>
    </div>
    <div class="version">LightLink V1.0 SNAPSHOT</div>
</div>
<LoginPanel v-if="loginPanelConfig.show" :show="loginPanelConfig.show" @close="closeLoginPanel()"></LoginPanel>
<ApplyPanel v-if="applyPanelConfig.show" :show="applyPanelConfig.show" @close="closeApplyPanel()"></ApplyPanel>
<SettingPanel v-if="settingPanelConfig.show" :show="settingPanelConfig.show" @close="closeSettingPanel()"></SettingPanel>
</template>

<script setup>
    import {ref, reactive, getCurrentInstance, nextTick, onBeforeMount, onMounted} from 'vue';
    import LoginPanel from './LoginPanel.vue';
    import ApplyPanel from './ApplyPanel.vue';
    import Introduce from './Introduce.vue';
    import SettingPanel from './SettingPanel.vue';
    const { proxy } = getCurrentInstance();
    import VueCookies from 'vue-cookies'
    import { useRoute, useRouter } from 'vue-router';
    const route = useRoute();
    const router = useRouter();
    import { useLoginStore } from '@/store/LoginStore.js'
    const loginStore = useLoginStore()
    import { useOnlineStore } from '@/store/OnlineStore.js'
    
    const onlineStore = useOnlineStore()

    const loginPanelConfig = ref({
        show: false
    })
    const applyPanelConfig = ref({
        show: false
    })
    const settingPanelConfig = ref({
        show: false
    })
    const closeLoginPanel = () => {
        loginPanelConfig.value.show = false
    }
    const closeApplyPanel = () => {
        applyPanelConfig.value.show = false
    }
    const closeSettingPanel = () => {
        settingPanelConfig.value.show = false
    }

    const logout = async () => {
        if(onlineStore.isConnected){
            proxy.message.warning(proxy.t('server.msgLeaveFirst'))
            return;
        }
        const result = await proxy.request.get({
            url: '/account/logout'
        })
        if(!result){
            proxy.message.error(proxy.t('person.logoutFailed'))
            return
        }
        VueCookies.remove('tokenuser')
        proxy.message.success(proxy.t('person.logoutSuccess'))
        loginStore.clear()
    }
</script>

<style scoped lang="scss">
.container{
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    
    .loginButtonBox{
        width: 80%;
        max-width: 500px;
        height: 150px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        .btn{
            display: flex;
        }
    }
    .version{
        margin-top: 24px;
        font-size: 12px;
        color: var(--s-color-on-surface-variant, #999);
        letter-spacing: 0.5px;
    }
    .userInfoBox{
        width: 90%;
        max-width: 500px;
        padding: 24px;
        background: var(--s-color-surface-container, #fff);
        border: 1px solid var(--s-color-outline-variant, #e0e0e0);
        border-radius: 16px;
        .title{
            font-size: 22px;
            font-weight: 500;
            color: var(--s-color-on-surface, #111);
            margin-bottom: 20px;
            text-align: center;
            letter-spacing: 0.15px;
        }
        .infoItem{
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid var(--s-color-outline-variant, #e0e0e0);
            .label{
                color: var(--s-color-on-surface-variant, #666);
                font-size: 14px;
                letter-spacing: 0.25px;
            }
            .value{
                color: var(--s-color-on-surface, #111);
                font-size: 14px;
                font-weight: 500;
                letter-spacing: 0.25px;
            }
        }
        .logoutBtn{
            width: 100%;
            margin-top: 16px;
        }
    }
}
</style>