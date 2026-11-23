<template>
<div class="container">
    <div class="header">
        <span class="title">{{ proxy.t(currentFilter.label) }}</span>
    </div>
    <div class="serverList" v-if="serverList.length > 0">
        <div class="serverCard" v-for="item in serverList" :key="item.serverId" @click.left="enterServer(item)">
            <div @contextmenu.prevent="selectedServer = item; $refs.popup.show({ x: $event.clientX, y: $event.clientY })">
                <div class="cardTop">
                    <div class="serverIcon">
                        <img v-if="item.serverImage" :src="item.serverImage" alt="">
                        <span v-else>{{ item.serverName?.charAt(0)?.toUpperCase() || 'S' }}</span>
                    </div>
                    <div class="serverMainInfo">
                        <div class="serverName">{{ item.serverName }}</div>
                        <div class="serverDesc">{{ item.serverInfo || proxy.t('server.noDescription') }}</div>
                    </div>
                </div>
                <div class="cardBottom">
                    <span class="chip">{{ item.serverId }}</span>
                    <span class="chip">{{ proxy.t('server.memberCount').replace('{count}', item.maxNumber) }}</span>
                    <span class="chip chipPrimary" v-if="item.serverMaster === loginStore.loginUser?.uid">{{ proxy.t('server.mine') }}</span>
                </div>
            </div>
        </div>
    </div>
    <div class="empty" v-else>
        <span>{{ proxy.t('server.empty') }}</span>
    </div>
    <div class="fab" v-if="loginStore.loginUser !== null" @click="menuOpen = !menuOpen">
        <svg class="fabIcon" v-if="!menuOpen" viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/></svg>
        <svg class="fabIcon" v-else viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>
    </div>
    <div class="fabMenu" v-if="menuOpen">
        <div class="menuItem" v-for="filter in filters" :key="filter.key" @click="setFilter(filter)" :class="{ active: currentFilter.key === filter.key }">
            <svg class="menuIcon" viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" :d="filter.iconPath"/></svg>
            <span class="menuText">{{ proxy.t(filter.label) }}</span>
        </div>
        <div class="menuDivider"></div>
        <div class="menuItem create" @click="addServer">
            <svg class="menuIcon" viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>
            <span class="menuText">{{ proxy.t('server.create') }}</span>
        </div>
    </div>
    <div class="overlay" v-if="menuOpen" @click="menuOpen = false"></div>
    <s-popup ref="popup" @click.stop>
        <div style="min-height: 40px; min-width: 128px">
            <s-popup-menu-item class="popup-item" @click="getList();closePopup()">{{ proxy.t('common.refresh') }}</s-popup-menu-item>
            <s-popup-menu-item class="popup-item" @click="editServerNumber(selectedServer);closePopup()">{{ proxy.t('common.edit') }}</s-popup-menu-item>
            <s-popup-menu-item class="popup-item" @click="deleteServer(selectedServer);closePopup()"><span style="color: red">{{ proxy.t('common.delete') }}</span></s-popup-menu-item>
        </div>
    </s-popup>
</div>
<CreateServer v-if="createConfig.show" @load="getList" :show="createConfig.show" @close="createConfig.show = false"></CreateServer>
<Dialog
    :show="editConfig.show"
    @close="editConfig.show = false"
    width="200px">
    <template #title>
        <span>{{ proxy.t('server.editCapacity') }}</span>
    </template>
    <template #content>
        <div style="display: flex; flex-direction: column; align-items: center; gap: 10px">
            <el-input-number v-model="editConfig.num" :min="2" :max="50" @change="handleChange" />
            <s-button style="width: 100px" type="primary" @click="confirmEdit();closePopup()">{{ proxy.t('common.confirm') }}</s-button>
        </div>
    </template>
</Dialog>
</template>

<script setup>
    import CreateServer from './CreateServer.vue';
    import {ref, getCurrentInstance, onMounted} from 'vue';
    const { proxy } = getCurrentInstance();
    import { useRoute, useRouter } from 'vue-router';
    const route = useRoute();
    const router = useRouter();
    import { useLoginStore } from '@/store/LoginStore';
    const loginStore = useLoginStore();
    import { useOnlineStore } from '@/store/OnlineStore';

    const onlineStore = useOnlineStore();

    const createConfig = ref({
        show: false
    })

    const editConfig = ref({
        show: false,
        num: 0
    })

    const menuOpen = ref(false)

    const filters = [
        { key: 'all', label: 'server.filterAll', iconPath: 'M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z' },
        { key: 'joined', label: 'server.filterJoined', iconPath: 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.64 1.97 1.51 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z' },
        { key: 'created', label: 'server.filterCreated', iconPath: 'M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z' }
    ]
    const currentFilter = ref(filters[0])
    const serverList = ref([])
    const selectedServer = ref(null)

    const getList = async () => {
        let result = null;
        if(currentFilter.value.key === 'all' || currentFilter.value.key === 'created'){
            result = await proxy.request.get({
                url: '/server/loadServerList'
            })
        }else if(currentFilter.value.key === 'joined'){
            result = await proxy.request.get({
                url: '/server/loadSelfServerList'
            })
        }
        if(!result){
            return;
        }
        serverList.value = result.data
        if(currentFilter.value.key === 'created'){
            serverList.value = serverList.value.filter(item => item.serverMaster === loginStore.loginUser.uid)
        }
    }

    const setFilter = (filter) => {
        currentFilter.value = filter
        menuOpen.value = false
        getList()
    }

    const enterServer = async (server) => {
        if(onlineStore.isConnected){
            proxy.message.warning(proxy.t('server.msgLeaveFirst'))
            return;
        }
        let result = await proxy.request.get({
            url: '/server/checkEnterServer',
            params: {
                serverId: server.serverId
            }
        })
        if(!result){
            proxy.message.error(proxy.t('server.msgJoinFailed'))
            return;
        }
        if(result.data){
            router.push(`/online/${server.serverId}`)
            return
        }
        proxy.message.error(proxy.t('server.msgJoinDenied'))
    }

    const deleteServer = async (server) => {
        if(server.serverMaster !== loginStore.loginUser.uid){
            proxy.message.error(proxy.t('server.msgDeleteDenied'))
            return;
        }
        proxy.confirm({
            message: proxy.t('server.msgDeleteConfirm').replace('{name}', server.serverName),
            okfun: async () => {
                let result = await proxy.request.get({
                    url: '/server/deleteServer',
                    params: {
                        serverId: server.serverId
                    }
                })
                if(!result){
                    proxy.message.error(proxy.t('server.msgDeleteFailed'))
                    return;
                }
                proxy.message.success(proxy.t('server.msgDeleteSuccess'))
                getList()
            }
        })
    }

    const editServerNumber = (server) => {
        editConfig.value.num = server.maxNumber
        if(server.serverMaster !== loginStore.loginUser.uid){
            proxy.message.error(proxy.t('server.msgEditDenied'))
            return;
        }
        editConfig.value.show = true
    }

    const addServer = () => {
        menuOpen.value = false
        createConfig.value.show = true
    }

    const popup = ref(null)

    const closePopup = () => {
        popup.value?.close()
    }

    const confirmEdit = async () => {
        if(editConfig.value.num <= 0){
            proxy.message.error(proxy.t('server.msgCapacityInvalid'))
            return;
        }
        let result = await proxy.request.get({
            url: '/server/editServerNumber',
            params: {
                maxNumber: editConfig.value.num,
                serverId: selectedServer.value.serverId
            }
        })
        if(!result){
            proxy.message.error(proxy.t('server.msgEditFailed'))
            return;
        }
        proxy.message.success(proxy.t('server.msgEditSuccess'))
        getList()
    }

    const handleChange = (value) => {
        editConfig.value.num = value
    }

    onMounted(() => {
        getList()
    })
</script>

<style scoped lang="scss">
$primary: var(--s-color-primary);
$on-primary: var(--s-color-on-primary);
$primary-container: var(--s-color-primary-container);
$on-primary-container: var(--s-color-on-primary-container);
$surface: var(--s-color-surface);
$surface-container: var(--s-color-surface-container);
$surface-container-high: var(--s-color-surface-container-high);
$surface-container-lowest: var(--s-color-surface-container-lowest);
$on-surface: var(--s-color-on-surface);
$on-surface-variant: var(--s-color-on-surface-variant);
$outline: var(--s-color-outline);
$outline-variant: var(--s-color-outline-variant);

.popup-item{
    padding-left: 20px;
}

.container{
    height: 100%;
    width: 100%;
    padding: 16px;
    box-sizing: border-box;
    overflow-y: auto;
    .header{
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        .title{
            font-size: 22px;
            font-weight: 500;
            color: $on-surface;
        }
    }
    .serverList{
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 12px;
    }
    .serverCard{
        background: $surface-container;
        border-radius: 12px;
        padding: 16px;
        cursor: pointer;
        transition: background 0.15s;
        &:hover{
            background: $surface-container-high;
        }
        .cardTop{
            display: flex;
            align-items: center;
            margin-bottom: 12px;
            .serverIcon{
                width: 40px;
                height: 40px;
                border-radius: 20px;
                background: $primary-container;
                color: $on-primary-container;
                font-size: 16px;
                font-weight: 500;
                display: flex;
                align-items: center;
                justify-content: center;
                flex-shrink: 0;
                margin-right: 12px;
                overflow: hidden;
                img{
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }
            }
            .serverMainInfo{
                flex: 1;
                min-width: 0;
                .serverName{
                    font-size: 16px;
                    font-weight: 500;
                    color: $on-surface;
                    margin-bottom: 2px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                .serverDesc{
                    font-size: 14px;
                    color: $on-surface-variant;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }
        .cardBottom{
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
            .chip{
                font-size: 12px;
                color: $on-surface-variant;
                background: $surface;
                border: 1px solid $outline-variant;
                padding: 4px 12px;
                border-radius: 8px;
                line-height: 16px;
            }
            .chipPrimary{
                color: $on-primary-container;
                background: $primary-container;
                border-color: transparent;
            }
        }
    }
    .empty{
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 60%;
        color: $on-surface-variant;
        font-size: 14px;
        gap: 16px;
    }
    .fab{
        position: fixed;
        bottom: 90px;
        right: 16px;
        width: 56px;
        height: 56px;
        border-radius: 16px;
        background: $primary-container;
        color: $on-primary-container;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.08);
        z-index: 100;
        transition: box-shadow 0.2s;
        &:hover{
            box-shadow: 0 2px 6px rgba(0,0,0,0.16), 0 2px 4px rgba(0,0,0,0.1);
        }
    }
    .overlay{
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 90;
    }
    .fabMenu{
        position: fixed;
        bottom: 156px;
        right: 16px;
        background: $surface-container;
        border-radius: 12px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 4px 12px rgba(0,0,0,0.08);
        padding: 8px 0;
        z-index: 100;
        min-width: 200px;
        animation: menuIn 0.2s cubic-bezier(0.2, 0, 0, 1);
        @keyframes menuIn {
            from { opacity: 0; transform: scale(0.95) translateY(8px); }
            to { opacity: 1; transform: scale(1) translateY(0); }
        }
        .menuItem{
            display: flex;
            align-items: center;
            padding: 12px 16px;
            cursor: pointer;
            border-radius: 8px;
            margin: 0 8px;
            transition: background 0.15s;
            &:hover{
                background: rgba($primary, 0.08);
            }
            &.active{
                background: rgba($primary, 0.12);
                .menuText{
                    color: $primary;
                    font-weight: 500;
                }
                .menuIcon{
                    color: $primary;
                }
            }
            .menuIcon{
                color: $on-surface-variant;
                margin-right: 12px;
                flex-shrink: 0;
            }
            .menuText{
                font-size: 14px;
                color: $on-surface;
                flex: 1;
            }
        }
        .menuDivider{
            height: 1px;
            background: $outline-variant;
            margin: 4px 16px;
        }
        .create{
            .menuIcon{
                color: $primary;
            }
            .menuText{
                color: $primary;
                font-weight: 500;
            }
        }
    }
}
</style>