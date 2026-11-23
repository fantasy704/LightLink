<template>
<div class="container">
    <div class="join-room" v-if="!onlineStore.isConnected">
        <div class="join-card" v-if="serverId !== 'S'">
            <div class="join-title">{{ proxy.t('room.title').replace('{id}', serverId) }}</div>
            <button class="join-btn" @click="joinRoom">{{ proxy.t('room.join') }}</button>
        </div>
        <div class="join-card" v-else>
            <div>{{ proxy.t('room.notFound') }}</div>
        </div>
    </div>
    <div class="room" v-else>
        <div class="room-header">
            <span class="room-name">{{ proxy.t('room.title').replace('{id}', serverId) }}</span>
            <span>{{ proxy.t('room.inviteLink') }}</span>
            <div class="invite-link-box">
                <span class="invite-link-text">{{ inviteLink }}</span>
                <button class="copy-btn" @click="copyLink">{{ proxy.t('common.copy') }}</button>
            </div>
            <span class="online-badge">{{ proxy.t('room.onlineCount').replace('{count}', onlineStore.onlineUsers.length) }}</span>
            <button class="toggle-users-btn" @click="showUserList = !showUserList">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="currentColor" d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg>
            </button>
        </div>
        <div class="mobile-users" v-if="showUserList">
            <div class="mobile-user" v-for="user in onlineStore.serverUsers" :key="user.serverUser">
                <div @contextmenu.prevent="selectedUser = user;$refs.popupRef.show({ x: $event.clientX, y: $event.clientY })" 
                    class="user-avatar" :class="{ 'speaking': user.isSpeaking }">
                    {{ user.memberName.charAt(0) }}
                </div>
            </div>
        </div>
        <div class="room-body">
        <div class="main-area">
            <div class="message-list" ref="messageListRef">
                <div
                    class="message-item"
                    v-for="(msg, index) in onlineStore.messages"
                    :key="index"
                    :class="{
                        'is-system': msg.type === 'user_joined' || msg.type === 'user_left',
                        'is-chat': msg.type === 'chat' || msg.type === 'text_message',
                        'is-self': msg.fromUserId === loginStore.loginUser?.uid
                    }">
                    <template v-if="msg.type === 'user_joined' || msg.type === 'user_left'">
                        <span class="sys-text">{{ msg.message }}</span>
                    </template>
                    <template v-else-if="msg.type === 'text_message'">
                        <div class="chat-header">
                            <span class="chat-name">{{ msg.name }}:</span>
                            <span class="chat-time">{{ formatTime(msg.timestamp) }}</span>
                            <div class="chat-body">{{ msg.message }}</div>
                        </div>
                        
                    </template>
                    <template v-else>
                        <span class="sys-text">{{ msg.message || JSON.stringify(msg) }}</span>
                    </template>
                </div>
            </div>
            <div class="chat-input-bar">
                <input
                    type="text"
                    class="chat-input"
                    v-model="chatInput"
                    :placeholder="proxy.t('room.messagePlaceholder')"
                    @keyup.enter="sendChatMessage"
                    @focus="onlineStore.chatInputFocused = true"
                    @blur="onlineStore.chatInputFocused = false"
                />
                <button class="chat-send-btn" @click="sendChatMessage">
                    <svg viewBox="0 0 24 24" width="18" height="18"><path fill="currentColor" d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>
                </button>
            </div>
        </div>
        <div class="sidebar">
            <div class="sidebar-header">{{ proxy.t('room.onlineUsers') }}</div>
            <div class="user-list">
                <div
                    v-for="user in onlineStore.serverUsers"
                    :key="user.serverUser"
                    :class="{ 'self': user.serverUser === loginStore.loginUser.uid }">
                    <div
                        :class="['user-item', user.isDisable ? 'disable' : '']" 
                        @contextmenu.prevent="selectedUser = user;$refs.popupRef.show({ x: $event.clientX, y: $event.clientY })">
                        <div class="user-avatar" :class="{ 'speaking': user.isSpeaking }">{{ user.memberName.charAt(0) }}</div>
                        <div class="user-info">
                            <span class="user-id">{{ user.memberName }}</span>
                        </div>
                        <div class="speaking-indicator" v-if="user.isSpeaking">
                            <span class="speaking-dot"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    </div>
</div>
<s-popup ref="popupRef" @click.stop>
    <div style="min-width: 120px;display: flex;flex-direction: column;align-items: center;justify-content: center;">
        <div style="width: 100%;height: 32px;line-height: 31px; display:block;text-align: center;">
            {{ selectedUser?.memberName || proxy.t('common.none') }}
        </div>
        <s-popup-menu-item 
            style="width: 100%;height: 32px;line-height: 31px; color: red;display:block;text-align: center;"
            @click="removeMember">{{ proxy.t('room.remove') }}
        </s-popup-menu-item>
    </div>
</s-popup>
</template>

<script setup>
    import { ref, onMounted, getCurrentInstance, watch, nextTick, computed } from 'vue';
    import { useRoute, useRouter } from 'vue-router'
    import { useOnlineStore } from '../../store/OnlineStore'
    import { useLoginStore } from '../../store/LoginStore'
    import { useLocalDeviceStore } from '../../store/LocalDeviceStore';
    import Device from '../../device/useDevice';

    //================================================================ test district
    const localDeviceStore = useLocalDeviceStore()
    // console.log(Device.initDevice())

    // setTimeout(() => {
    //     // Device.closeMicro()
    //     Device.openMicro(localDeviceStore.deviceList[1].deviceId)
    //     setTimeout(() => {
    //         Device.closeMicro()
    //     }, 3000)
    // }, 3000)
    

    //================================================================

    const onlineStore = useOnlineStore()
    
    const loginStore = useLoginStore()
    const {proxy} = getCurrentInstance()

    const popupRef = ref(null)

    const router = useRouter()
    const route = useRoute()
    const serverId = route.params.serverId
    const showUserList = ref(false)
    const chatInput = ref('')
    const inviteLink = ref('')
    const messageListRef = ref(null)

    const selectedUser = ref(null)

    watch(() => onlineStore.messages.length, () => {
        nextTick(() => {
            if (messageListRef.value) {
                messageListRef.value.scrollTop = messageListRef.value.scrollHeight
            }
        })
    })

    async function joinRoom() {
        try {
            await Device.initDevice()
            await loadServerAllMember()
        } catch(e) {
            console.error('设备初始化失败:', e)
        }
        onlineStore.connectWebSocket(serverId)
    }

    // 移除成员
    const removeMember = async () => {
        if(!selectedUser.value){
            return
        }
        if(onlineStore.onlineUsers.find(user => user.uid === selectedUser.value.serverUser)){
            proxy.message.error(proxy.t('room.removeOnlineDenied'))
            return;
        }
        const result = await proxy.request.get({
            url: '/server/removeUserFromServer',
            params: {
                serverId,
                memberId: selectedUser.value.serverUser
            }
        })
        if(!result){
            return
        }
        proxy.message.success(proxy.t('room.removeSuccess'))
        onlineStore.loadServerAllMember()
        onelineStore.computeDisableMembers()
    }

    const loadInviteLink = async () => {
        if (!serverId || serverId === 'S') {
            return
        }
        const result = await proxy.request.get({
            url: '/server/loadInviteLink',
            params: {
                serverId: serverId
            }
        })
        if (!result) {
            router.push('/404')
            return;
        }
        inviteLink.value = `https://frp-use.com:64540/invitation/${result.data}`
    }
    loadInviteLink()

    //加载全部成员
    const loadServerAllMember = async () => {
        const result = await proxy.request.get({
            url: '/server/loadServerMember',
            params: {
                serverId
            }
        })
        if(!result){
            return;
        }
        onlineStore.serverUsers = result.data
        onlineStore.computeDisableMembers()
    }

    function sendChatMessage() {
        const text = chatInput.value.trim()
        if (!text) return
        onlineStore.sendTextMessage(text)
        chatInput.value = ''
    }

    function copyLink() {
        const text = inviteLink.value
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(text).then(() => {
                proxy.message.success(proxy.t('room.linkCopied'))
            })
        } else {
            const textarea = document.createElement('textarea')
            textarea.value = text
            textarea.style.position = 'fixed'
            textarea.style.left = '-9999px'
            document.body.appendChild(textarea)
            textarea.focus()
            textarea.select()
            document.execCommand('copy')
            document.body.removeChild(textarea)
            proxy.message.success(proxy.t('room.linkCopied'))
        }
    }

    function formatTime(timestamp) {
        if (!timestamp) return ''
        const date = new Date(timestamp)
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }

</script>

<style scoped lang="scss">
.container {
    flex: 1;
    min-height: 0;
    width: 100%;
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.join-room {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}

.join-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    padding: 40px 60px;
    background-color: var(--s-color-surface-container);
    border: 1px solid var(--s-color-outline-variant);
    border-radius: 8px;
}

.join-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--s-color-on-surface);
}

.join-btn {
    padding: 10px 40px;
    border: none;
    border-radius: 6px;
    background-color: var(--s-color-primary);
    color: var(--s-color-on-primary);
    font-size: 15px;
    cursor: pointer;

    &:hover {
        opacity: 0.85;
    }
}

.room {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-height: 0;
    width: 100%;
    overflow: hidden;
}

.room-header {
    display: flex;
    align-items: center;
    padding: 10px 14px;
    background-color: var(--s-color-surface-container);
    border-bottom: 1px solid var(--s-color-outline-variant);
    gap: 8px;
    flex-shrink: 0;

    .room-name {
        font-size: 15px;
        font-weight: 600;
        color: var(--s-color-on-surface);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 1;
    }

    .invite-link-box {
        display: flex;
        align-items: center;
        background-color: var(--s-color-surface);
        border: 1px solid var(--s-color-outline-variant);
        border-radius: 6px;
        overflow: hidden;
        max-width: 260px;

        .invite-link-text {
            padding: 4px 10px;
            font-size: 12px;
            color: var(--s-color-on-surface-variant);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            flex: 1;
        }

        .copy-btn {
            padding: 4px 12px;
            font-size: 12px;
            border: none;
            background-color: var(--s-color-primary);
            color: var(--s-color-on-primary);
            cursor: pointer;
            white-space: nowrap;
            flex-shrink: 0;

            &:hover {
                opacity: 0.85;
            }
        }
    }

    .invite-link-box {
        display: flex;
        align-items: center;
        background-color: var(--s-color-surface);
        border: 1px solid var(--s-color-outline-variant);
        border-radius: 6px;
        overflow: hidden;
        max-width: 260px;

        .invite-link-text {
            padding: 4px 10px;
            font-size: 12px;
            color: var(--s-color-on-surface-variant);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            flex: 1;
        }

        .copy-btn {
            padding: 4px 12px;
            font-size: 12px;
            border: none;
            border: 1px solid var(--s-color-outline-variant);
            background-color: var(--s-color-primary);
            color: var(--s-color-on-primary);
            cursor: pointer;
            white-space: nowrap;
            flex-shrink: 0;

            &:hover {
                opacity: 0.85;
            }
        }
    }

    .online-badge {
        font-size: 11px;
        padding: 2px 8px;
        border-radius: 10px;
        background-color: var(--s-color-primary-container);
        color: var(--s-color-on-primary-container);
        flex-shrink: 0;
    }

    .toggle-users-btn {
        display: none;
        width: 32px;
        height: 32px;
        border: none;
        border-radius: 50%;
        background-color: var(--s-color-surface-container-high);
        color: var(--s-color-on-surface);
        cursor: pointer;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }
}

.mobile-users {
    display: none;
    padding: 8px 14px;
    gap: 12px;
    overflow-x: auto;
    background-color: var(--s-color-surface-container);
    border-bottom: 1px solid var(--s-color-outline-variant);
    flex-shrink: 0;
}

.mobile-user {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;

    .mobile-user-name {
        font-size: 10px;
        color: var(--s-color-on-surface-variant);
        max-width: 48px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.room-body {
    flex: 1;
    display: flex;
    min-height: 0;
    overflow: hidden;
}

.main-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
    min-height: 0;
    overflow: hidden;
}

.message-list {
    flex: 1;
    overflow-y: auto;
    padding: 12px 14px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.chat-input-bar {
    display: flex;
    gap: 6px;
    padding: 6px 10px;
    border-top: 1px solid #d0d0d0;
    flex-shrink: 0;
    background-color: #fff;
}

.chat-input {
    flex: 1;
    padding: 4px 6px;
    border: 1px solid #c0c0c0;
    background-color: #fff;
    color: #333;
    font-size: 13px;
    outline: none;

    &:focus {
        border-color: #1e88e5;
    }

    &::placeholder {
        color: #aaa;
    }
}

.chat-send-btn {
    padding: 4px 14px;
    border: 1px solid #a0a0a0;
    background-color: #f0f0f0;
    color: #333;
    cursor: pointer;
    font-size: 13px;
    flex-shrink: 0;

    &:hover {
        background-color: #e0e0e0;
    }

    &:active {
        background-color: #d0d0d0;
    }
}

.message-item {
    &.is-system {
        text-align: center;
        padding: 4px 0;
    }

    &.is-chat {
        padding: 2px 0;
    }

    &.is-self .chat-name {
        color: #1e88e5;
    }
}

.chat-header {
    display: flex;
    align-items: baseline;
    gap: 6px;
}

.chat-name {
    font-size: 13px;
    font-weight: 500;
    color: #43a047;
    flex-shrink: 0;
}

.chat-time {
    font-size: 11px;
    color: #999;
}

.chat-body {
    font-size: 14px;
    color: var(--s-color-on-surface);
    padding: 1px 0 1px 0;
    word-break: break-all;
    line-height: 1.5;
}

.sys-text {
    font-size: 12px;
    color: #999;
}

.sidebar {
    width: 220px;
    display: flex;
    flex-direction: column;
    background-color: var(--s-color-surface-container);
    border-left: 1px solid var(--s-color-outline-variant);
    flex-shrink: 0;
}

.sidebar-header {
    padding: 10px 14px;
    font-size: 13px;
    font-weight: 600;
    color: var(--s-color-on-surface);
    border-bottom: 1px solid var(--s-color-outline-variant);
}

.user-list {
    flex: 1;
    overflow-y: auto;
    padding: 6px 0;
}

.user-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 14px;

    &.self .user-name {
        color: var(--s-color-primary);
    }
    &:hover{
        background-color: var(--s-color-surface-variant);
    }
}

.disable{
    opacity: 0.5;
}

.user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background-color: var(--s-color-primary-container);
    color: var(--s-color-on-primary-container);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 600;
    flex-shrink: 0;

    &.speaking {
        box-shadow: 0 0 0 2px #4caf50;
    }
}

.user-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;

    .user-name {
        font-size: 13px;
        font-weight: 500;
        color: var(--s-color-on-surface);
    }

    .user-id {
        font-size: 10px;
        color: var(--s-color-on-surface-variant);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.speaking-indicator {
    .speaking-dot {
        display: block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: #4caf50;
        animation: pulse 1s infinite;
    }
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.4; }
}

@media (max-width: 768px) {
    .room-header .toggle-users-btn {
        display: flex;
    }

    .mobile-users {
        display: flex;
    }

    .sidebar {
        display: none;
    }
}
</style>