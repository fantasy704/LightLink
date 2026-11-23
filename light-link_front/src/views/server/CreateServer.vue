<template>
    <Dialog :show="show" @close="emit('close')" width="400px">
        <template #title>{{ proxy.t('createServer.title') }}</template>
        <template #content>
            <div class="panel">
                <div class="createFormBox">
                    <div class="row">
                        {{ proxy.t('createServer.name') }}
                        <s-text-field class="input" :label="proxy.t('createServer.nameLabel')" v-model="formData.serverName" maxLength="50" type="multiline"></s-text-field>
                    </div>
                    <div class="row">
                        {{ proxy.t('createServer.capacity') }}
                        <s-text-field class="input" :label="proxy.t('createServer.capacityLabel')" v-model="formData.maxNumber" maxLength="50" type="number"></s-text-field>
                    </div>
                    <div class="row">
                        {{ proxy.t('createServer.info') }}
                        <s-text-field class="input" style="height: 200px" name="password" :label="proxy.t('createServer.infoLabel')" type="multiline" maxLength="50" v-model="formData.serverInfo"></s-text-field>
                    </div>
                    <s-button class="btn" @click="createServer"> {{ proxy.t('createServer.submit') }} </s-button>
                </div>
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
    const props = defineProps({
        show:{
            type: Boolean,
            default: false
        }
    })
    const emit = defineEmits(['close', 'load']);
    const formData = ref({
        serverName: '',
        serverInfo: '',
        maxNumber: 0
    })

    const createServer = async () => {
        let result = await proxy.request.get({
            url: '/server/createServer',
            params: formData.value
        })
        if(!result){
            proxy.message.error(proxy.t('createServer.failed'))
            return;
        }
        proxy.message.success(proxy.t('createServer.success'))
        emit('load')
        emit('close')
    }
</script>

<style scoped lang="scss">
.createFormBox{
    min-width: 300px;
    max-width: 600px;
    min-height: 190px;
    max-height: 500px;
    display: flex;
    flex-direction: column;
    .row{
        display: flex;
        align-items: center;
        padding: 0;
        margin: 0;
        .input{
            min-height: 40px;
            margin: 10px 0px;
        }
    }
}
</style>