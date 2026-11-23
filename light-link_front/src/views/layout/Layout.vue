<template>
<div class="container">
    <div class="view">
        <router-view></router-view>
    </div>
    <div class="tab-bar">
        <TabBar></TabBar>
    </div>
    <div class="control-panel" v-if="onlineStore.isInRoom" :class="{ 'collapsed': panelCollapsed, 'top-mode': onlineStore.chatInputFocused }">
        <div class="panel-header" @click="panelCollapsed = !panelCollapsed">
            <span>{{ proxy.t('controlPanel.title') }}</span>
            <svg viewBox="0 0 24 24" width="18" height="18" :style="{ transform: panelCollapsed ? 'rotate(180deg)' : '' }">
                <path fill="currentColor" d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6z"/>
            </svg>
        </div>
        <div class="panel-body" v-if="!panelCollapsed">
            <div class="panel-item">
                <el-select v-model="localDeviceStore.curDevice" :placeholder="proxy.t('controlPanel.micNotFound')">
                    <el-option 
                        v-for="(device, index) in localDeviceStore.audioDeviceList"
                        :key="device.deviceId" 
                        @click="changeMicDevice(index)"
                        :label="device.label"
                        :value="index">
                        {{ device.label }}
                    </el-option>
                </el-select>
            </div>
            <div class="mic-status">
                <span class="status-label">{{ proxy.t('controlPanel.status') }}</span>
                <span class="status-value" :class="{ 'active': localDeviceStore.isMicOn }">
                    {{ localDeviceStore.isMicOn ? proxy.t('controlPanel.micOn') : proxy.t('controlPanel.micMuted') }}
                </span>
            </div>
            <div class="volume-bar">
                <span class="status-label">{{ proxy.t('controlPanel.volume') }}</span>
                <div class="volume-track">
                    <div class="volume-fill" :style="{ width: localDeviceStore.volume + '%' }"></div>
                </div>
                <span class="volume-value">{{ localDeviceStore.volume }}</span>
            </div>
            <div class="panel-actions">
                <button class="panel-btn" :class="{ 'active': localDeviceStore.isMicOn }" @click="Device.toggleMic()">
                    {{ localDeviceStore.isMicOn ? proxy.t('controlPanel.micOffAction') : proxy.t('controlPanel.micOnAction') }}
                </button>
                <button class="panel-btn danger" @click="leaveRoom">{{ proxy.t('controlPanel.leaveRoom') }}</button>
            </div>
        </div>
    </div>
</div>
</template>

<script setup>
    import { ref, watch, getCurrentInstance } from 'vue';
    import { useRouter } from 'vue-router';
    import TabBar from './TabBar.vue'
    import { useOnlineStore } from '../../store/OnlineStore'
    import { useLocalDeviceStore } from '../../store/LocalDeviceStore.js';
    import Device from '../../device/useDevice.js'
    const { proxy } = getCurrentInstance()
    const onlineStore = useOnlineStore()
    const router = useRouter()
    const panelCollapsed = ref(false)
    const localDeviceStore = useLocalDeviceStore()

    const changeMicDevice = (index) => {
        Device.openMicro(localDeviceStore.audioDeviceList[index].deviceId)
    }

    function leaveRoom() {
        if (!confirm(proxy.t('controlPanel.leaveConfirm'))) return
        onlineStore.closeWebSocket()
        router.push('/server')
    }
</script>

<style scoped lang="scss">
.container{
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    height: 100vh;
    width: 100%;
    .view{
        padding: 12px;
        margin: 0;
        width: 100%;
        box-sizing: border-box;
        flex: 1;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }
    .tab-bar{
        margin: 0;
        padding: 0;
        width: 100%;
        height: 65px;
        background-color: var(--s-color-primary-container);
        overflow: hidden;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
}

.control-panel {
    position: fixed;
    bottom: 130px;
    right: 240px;
    width: 240px;
    background-color: var(--s-color-surface-container-high);
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    overflow: hidden;
    z-index: 100;

    &.collapsed .panel-header {
        border-bottom: none;
    }
}

.panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    cursor: pointer;
    background-color: var(--s-color-surface-container);
    border-bottom: 1px solid var(--s-color-outline-variant);
    font-size: 13px;
    font-weight: 600;
    color: var(--s-color-on-surface);
    user-select: none;

    svg { transition: transform 0.2s; }
}

.panel-body {
    padding: 12px 14px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    .panel-item{
        display: flex;
        justify-content: center;
        flex-direction: column;
        align-items: start;
        width: 100%;
    }
}

.mic-status {
    display: flex;
    align-items: center;
    gap: 8px;
}

.status-label {
    font-size: 12px;
    color: var(--s-color-on-surface-variant);
}

.status-value {
    font-size: 12px;
    font-weight: 500;
    color: var(--s-color-on-surface-variant);

    &.active { color: #4caf50; }
}

.volume-bar {
    display: flex;
    align-items: center;
    gap: 8px;
}

.volume-track {
    flex: 1;
    height: 4px;
    background-color: var(--s-color-outline-variant);
    border-radius: 2px;
    overflow: hidden;

    .volume-fill {
        height: 100%;
        background-color: var(--s-color-primary);
        border-radius: 2px;
        transition: width 0.1s;
    }
}

.volume-value {
    font-size: 11px;
    color: var(--s-color-on-surface-variant);
    min-width: 24px;
    text-align: right;
}

.panel-actions {
    display: flex;
    gap: 8px;
}

.panel-btn {
    flex: 1;
    padding: 6px 0;
    border-radius: 8px;
    border: 1px solid var(--s-color-outline);
    background-color: var(--s-color-surface);
    color: var(--s-color-on-surface);
    font-size: 12px;
    cursor: pointer;

    &.active {
        background-color: var(--s-color-primary);
        color: var(--s-color-on-primary);
        border-color: transparent;
    }

    &.danger {
        color: #e53935;
        border-color: #e53935;

        &:hover {
            background-color: #e53935;
            color: white;
        }
    }

    &:hover { opacity: 0.85; }
}

@media (max-width: 768px) {
    .control-panel {
        right: 10px;
        bottom: 130px;
        width: 200px;
        transition: bottom 0.3s, top 0.3s;
    }

    .control-panel.top-mode {
        bottom: auto;
        top: 100px;
    }
}
</style>