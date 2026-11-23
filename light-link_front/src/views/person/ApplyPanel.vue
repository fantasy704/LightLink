<template>
<Dialog
        :width="380"
        :show="show"
        @close="emit('close')">
        <template #title>
            {{ proxy.t('apply.title') }}
        </template>
        <template #content>
            <div class="loginFormBox">
                <div class="row">
                    <div class="label">{{ proxy.t('apply.email') }}</div>
                    <el-input class="input" :placeholder="proxy.t('apply.emailPlaceholder')" v-model="formData.email"></el-input>
                </div>
                <div class="row">
                    <div class="label">{{ proxy.t('apply.username') }}</div>
                    <el-input class="input" :placeholder="proxy.t('apply.usernamePlaceholder')" v-model="formData.name"></el-input>
                </div>
                <div class="row">
                    <div class="label">{{ proxy.t('apply.password') }}</div>
                    <el-input class="input" :placeholder="proxy.t('login.passwordPlaceholder')" v-model="formData.password" type="password"></el-input>
                </div>
                <div class="row">
                    <div class="label">{{ proxy.t('apply.confirmPassword') }}</div>
                    <el-input class="input" :placeholder="proxy.t('apply.confirmPasswordPlaceholder')" v-model="confirmPassword" type="password"></el-input>
                </div>
                <div class="row">
                    <div class="label">{{ proxy.t('apply.reason') }}</div>
                    <el-input class="input" type="textarea" :placeholder="proxy.t('apply.reasonPlaceholder')" v-model="formData.info"></el-input>
                </div>
                <s-button class="btn" @click="apply"> {{ proxy.t('apply.submit') }} </s-button>
            </div>
        </template>
    </Dialog>
</template>

<script setup>
    import { ref, getCurrentInstance } from 'vue'
    const { proxy } = getCurrentInstance();
    import { useRoute, useRouter } from 'vue-router';
    const route = useRoute();
    const router = useRouter();
    import {md5} from 'js-md5'

    const props = defineProps({
        show: {
            type: Boolean,
            default: false
        }
    })

    const formData = ref({
        email: '',
        name: '',
        info: '',
        password: '',
    })

    const confirmPassword = ref('')
    const emit = defineEmits(['close'])

    const apply = async () => {
        if(formData.value.password !== confirmPassword.value){
            proxy.message.error(proxy.t('apply.passwordMismatch'))
            return
        }
        const pwd = formData.value.password
        formData.value.password = md5(formData.value.password)
        const result = await proxy.request.get({
            url: '/account/applyAccount',
            params: formData.value
        })
        if(!result){
            proxy.message.error(proxy.t('apply.failed'))
            formData.value.password = pwd
            return;
        }
        proxy.message.success(proxy.t('apply.success'))
        emit('close')
    }
</script>

<style lang="scss" scoped>
.loginFormBox{
    min-width: 300px;
    max-width: 600px;
    min-height: 400px;
    max-height: 700px;
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
            flex: 1;
        }
    }
}
</style>