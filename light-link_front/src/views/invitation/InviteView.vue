<template>
<div class="container">
    <div class="panel" v-if="server">
        <div class="panel-header">
            <div class="server-icon">
                <img v-if="server.serverImage" :src="server.serverImage" alt="">
                <span v-else>{{ server.serverName?.charAt(0)?.toUpperCase() || 'S' }}</span>
            </div>
            <div class="invite-text">{{ proxy.t('invite.invitedToJoin').replace('{master}', server.serverMaster) }}</div>
        </div>
        <div class="server-name">{{ server.serverName }}</div>
        <div class="server-id">{{ server.serverId }}</div>
        <div class="server-info" v-if="server.serverInfo">{{ server.serverInfo }}</div>
        <div class="server-meta">
            <span class="meta-item">{{ proxy.t('invite.capacityLimit').replace('{count}', server.maxNumber) }}</span>
        </div>
        <div class="panel-footer">
            <button class="btn-join" @click="joinRoom" v-if="loginStore.loginUser">{{ proxy.t('invite.join') }}</button>
            <button class="btn-join" @click="goLogin" v-else>{{ proxy.t('invite.loginFirst') }}</button>
        </div>
    </div>
    <div class="panel" v-else-if="loading">
        <div class="invite-text">{{ proxy.t('common.loading') }}</div>
    </div>
    <div class="panel" v-else>
        <div class="invite-text">{{ proxy.t('room.notFound') }}</div>
        <div class="panel-footer">
            <button class="btn-join" @click="router.push('/server')">{{ proxy.t('invite.backHome') }}</button>
        </div>
    </div>
</div>
</template>

<script setup>
    import { ref, getCurrentInstance } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { useLoginStore } from '@/store/LoginStore'
    const {proxy} = getCurrentInstance()

    const route = useRoute()
    const router = useRouter()
    const loginStore = useLoginStore()

    const server = ref({})

    const loadServer = async () => {
        const result = await proxy.request.get({
            url: '/server/loadServerByLink',
            params: {
                link: route.params.ink
            }
        })
        if (!result) {
            return;
        }
        server.value = result.data
    }
    loadServer()

    const joinRoom = async () => {
        const result = await proxy.request.get({
            url: '/server/joinServerByLink',
            params: {
                link: route.params.ink
            }
        })
        if (!result) {
            return;
        }
        router.push(`/online/${server.value.serverId}`)
    }

    const goLogin = () => {
        router.push('/person')
    }
</script>

<style scoped lang="scss">
.container {
    margin: 0;
    padding: 0;
    height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;

    .panel {
        width: 400px;
        background-color: var(--s-color-surface-container, #fff);
        border-radius: 12px;
        padding: 32px;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;
        border: 1px solid var(--s-color-outline-variant, #e0e0e0);
    }

    .panel-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
    }

    .server-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        background-color: var(--s-color-primary-container, #d0e4ff);
        color: var(--s-color-on-primary-container, #001e3c);
        font-size: 22px;
        font-weight: 600;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;

        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }

    .invite-text {
        font-size: 14px;
        color: var(--s-color-on-surface-variant, #666);
    }

    .server-name {
        font-size: 22px;
        font-weight: 600;
        color: var(--s-color-on-surface, #111);
    }

    .server-id {
        font-size: 12px;
        color: var(--s-color-on-surface-variant, #999);
    }

    .server-info {
        font-size: 14px;
        color: var(--s-color-on-surface-variant, #666);
        text-align: center;
    }

    .server-meta {
        display: flex;
        gap: 8px;

        .meta-item {
            font-size: 12px;
            color: var(--s-color-on-surface-variant, #666);
            background-color: var(--s-color-surface, #f5f5f5);
            padding: 4px 12px;
            border-radius: 6px;
        }
    }

    .panel-footer {
        margin-top: 16px;
    }

    .btn-join {
        padding: 10px 48px;
        border: none;
        border-radius: 8px;
        background-color: var(--s-color-primary, #1976d2);
        color: var(--s-color-on-primary, #fff);
        font-size: 15px;
        cursor: pointer;

        &:hover {
            opacity: 0.85;
        }
    }
}
</style>