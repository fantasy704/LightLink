<template>
<Dialog
        :width="'500px'"
        :show="show"
        @close="emit('close')">
        <template #title>
            <span style="font-weight: bold;font-size: 18px">{{ proxy.t('setting.title') }}</span>
        </template>
        <template #content>
            <div class="container">
                <div class="setting-type-title">{{ proxy.t('setting.systemSection') }}</div>
                <div class="setting-item">
                    <div class="label">{{ proxy.t('setting.language') }}</div>
                    <div class="content">
                        <el-select v-model="systemConfig.language">
                            <el-option 
                                v-for="item in ['zh-CN', 'en-US']" 
                                :key="item" :label="item" 
                                :value="item" 
                                @click="settingStore.setLanguage(item)">
                            </el-option>
                        </el-select>
                    </div>
                </div>
                <div class="setting-type-title">{{ proxy.t('setting.networkSection') }}</div>
                <div class="setting-item">
                    <div class="label">{{ proxy.t('setting.advancedNetwork') }}</div>
                    <div class="content end">
                        <s-switch :checked="settingStore.setting.usingAdvancedNetwork" @click="changeAdvancedNet()"></s-switch>
                    </div>
                </div>
                <div class="setting-item">
                    <div class="label">{{ proxy.t('setting.httpAddress') }}</div>
                    <div class="content">
                        <el-input v-model="systemConfig.httpAddress" placeholder="http://localhost:8080"></el-input>
                    </div>
                </div>
                <div class="setting-item">
                    <div class="label">
                        <s-button class="test-btn" @click="testHttpConnection">{{ proxy.t('setting.testHttp') }}</s-button>
                    </div>
                    <div class="content">
                        <div class="test-result">
                            {{ testResult.httpMessage }}
                        </div>
                    </div>
                </div>
                <div class="setting-item">
                    <div class="label">{{ proxy.t('setting.wsAddress') }}</div>
                    <div class="content">
                        <el-input v-model="systemConfig.wsAddress" placeholder="ws://localhost:8080/websocket"></el-input>
                    </div>
                </div>
                <div class="setting-item">
                    <div class="label">
                        <s-button class="test-btn" @click="testWSConnection()">{{ proxy.t('setting.testWs') }}</s-button>
                    </div>
                    <div class="content">
                        <div class="test-result">
                            {{ testResult.wsMessage }}
                        </div>
                    </div>
                </div>
                <div class="panel-bottom">
                    <s-button class="save-btn" @click="saveSetting()" :disabled="!testResult.httpSuccess || !testResult.wsSuccess">{{ proxy.t('setting.save') }}</s-button>
                </div>
            </div>
        </template>
    </Dialog>
</template>

<script setup>
    import { ref, getCurrentInstance, onMounted } from 'vue'
    const { proxy } = getCurrentInstance();
    import { useRoute, useRouter } from 'vue-router';
    const route = useRoute();
    const router = useRouter();
    import { useSettingStore } from '../../store/SettingStore';
    const settingStore = useSettingStore()
    import axios from 'axios'

    const systemConfig = ref({
        language: '',
        httpAddress: "",
        wsAddress: ""
    })

    const testResult = ref({
        httpMessage: "",
        wsMessage: "",
        httpSuccess: false,
        wsSuccess: false
    })

    const props = defineProps({
        show: {
            type: Boolean,
            default: false
        }
    })

    const testHttpConnection = async () => {
        const instance = axios.create({
            withCredentials: true,
            baseURL: systemConfig.value.httpAddress,
            timeout: 5000
        })
        testResult.value.httpMessage = proxy.t('setting.testing')
        try{
            const {data} = await instance.get('/web_test/testHttp')
            if(data && data.code === 201){
               testResult.value.httpMessage = proxy.t('setting.httpSuccess')
               testResult.value.httpSuccess = true
            }else{
                throw new Error(proxy.t('setting.httpFailed'))
            }
        }catch(error){
            console.error(error)
            testResult.value.httpMessage = proxy.t('setting.httpFailed')
            testResult.value.httpSuccess = false
        }
        console.log(testResult.value)
    }

    const testWSConnection = async () => {
        testResult.value.wsMessage = proxy.t('setting.testing')
        try{
            const ws = new WebSocket(`${systemConfig.value.wsAddress}/websocket/S?tokenuser=ws_test`)
            ws.onmessage = e => {
                if(e.data === 'ws_test'){
                    testResult.value.wsMessage = proxy.t('setting.wsSuccess')
                    testResult.value.wsSuccess = true
                }
            }
        }catch(error){
            console.error(error)
            testResult.value.wsMessage = proxy.t('setting.wsFailed')
            testResult.value.wsSuccess = false
        }
        console.log(testResult.value)
    }

    const emit = defineEmits(['close'])

    const saveSetting = () => {
        settingStore.saveSetting({ ...systemConfig.value })
        proxy.message.success(proxy.t('setting.saved'))
    }

    const changeAdvancedNet = () => {
        settingStore.toggleAdvancedNet()
    }

    onMounted(() => {
        systemConfig.value = { ...systemConfig.value, ...settingStore.loadSetting() }
    })
</script>

<style lang="scss" scoped>
.container{
    width: 100%;
    .setting-type-title{
        font-size: 16px;
        font-weight: bold;
        margin: 10px 0;
    }
    .setting-item{
        display: flex;
        margin: 5px;
        gap: 10px;
        .label{
            min-width: 80px;
            height: 60px;
            line-height: 60px;
        }
        .content{
            flex: 1;
            margin: auto 0;
        }
    }
    .panel-bottom{
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
    }
}
.test-btn{
    width: 100px;
}
.test-result{
    font-size: 14px;
}
.save-btn{
    width: 70%;
}
.end{
    display: flex;
    justify-content: flex-end;
    padding-right: 5px;
}
</style>