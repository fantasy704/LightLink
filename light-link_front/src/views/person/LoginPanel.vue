<template>
    <Dialog
        :width="380"
        :show="show"
        @close="emit('close')">
        <template #title>
            {{ proxy.t('login.title') }}
        </template>
        <template #content>
            <div class="loginFormBox">
                <div class="row">
                    <div class="label">{{ proxy.t('login.account') }}</div>
                    <el-input class="input" :placeholder="proxy.t('login.accountPlaceholder')" v-model="formData.nameOrEmail"></el-input>
                </div>
                <div class="row">
                    <div class="label">{{ proxy.t('login.password') }}</div>
                    <el-input class="input" :placeholder="proxy.t('login.passwordPlaceholder')" type="password" v-model="formData.password"></el-input>
                </div>
                <s-button class="btn" @click="login"> {{ proxy.t('login.title') }} </s-button>
            </div>
        </template>
    </Dialog>
</template>

<script setup>
    import { ref, getCurrentInstance } from 'vue'
    import VueCookies from 'vue-cookies'
    const { proxy } = getCurrentInstance();
    import { useRoute, useRouter } from 'vue-router';
    const route = useRoute();
    const router = useRouter();
    import { useLoginStore } from '@/store/LoginStore.js'
    const loginStore = useLoginStore()
    import {md5} from 'js-md5'

    const props = defineProps({
        show: {
            type: Boolean,
            default: false
        }
    })

    const formData = ref({
        nameOrEmail: '',
        password: ''
    })
    const emit = defineEmits(['close'])

    const login = async () => {
        const pwd = formData.value.password
        formData.value.password = md5(formData.value.password)
        let result = await proxy.request.get({
            url: '/account/login',
            params: formData.value
        })
        if(!result){
            proxy.message.error(proxy.t('login.failed'))
            formData.value.password = pwd
            return
        }
        loginStore.setLoginUser(result.data)
        console.log(result.data)
        VueCookies.set('tokenuser', result.data.token)
        console.log(VueCookies.get('tokenuser'))
        proxy.message.success(proxy.t('login.success'))
        emit('close')
    }
</script>

<style scoped lang="scss">
.loginFormBox{
    min-width: 300px;
    max-width: 600px;
    min-height: 190px;
    max-height: 300px;
    display: flex;
    flex-direction: column;
    .row{
        display: flex;
        align-items: center;
        .label{
            width: 80px;
        }
        .input{
            min-height: 40px;
            margin: 10px 0px;
        }
    }
}
</style>