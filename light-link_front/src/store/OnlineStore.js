import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import VueCookie from 'vue-cookies'
import request from '@/utils/request.js'
import { useLocalDeviceStore } from './LocalDeviceStore'
import {useLoginStore} from '@/store/LoginStore.js'
import Device from '../device/useDevice'
import { useSettingStore } from './SettingStore'

const settingStore = useSettingStore()
const localDeviceStore = useLocalDeviceStore()

const iceServers = [{urls: 'stun:stun.l.google.com:19302'}]

export const useOnlineStore = defineStore('onlineStore', {
    state: () => {
        return {
            serverId: 'S',
            ws: null,
            isConnected: false,
            messages: [],
            heartbeatTimer: null,
            serverUsers: [],
            onlineUsers: [],
            chatInputFocused: false,
            logList: [

            ],
            fromUserId: null,
            peerConnections: {},
            remoteStreams: {},
            audioGain: 25
        }
    },
    getters: {
        isInRoom: (state) => state.isConnected && state.serverId !== 'S'
    },
    actions: {
        /**
         * 设置加入房间的serverId
         * @param serverId 
         */
        setOnlineServerId(serverId){
            this.serverId = serverId
        },
        /**
         * 建立RTC peerConnection并返回
         * @param {*} remoteUid 
         * @param {*} localStream 
         * @returns RTCPeerConnection
         */
        createPeerConnection(remoteUid, localStream) {
            if(this.peerConnections[remoteUid]){
                this.peerConnections[remoteUid].close()
            }
            const pc = new RTCPeerConnection({iceServers})
            if(localStream){
                localStream.getTracks().forEach(track => {
                    pc.addTrack(track, localStream)
                })
            }
            pc.ontrack = e => {
                this.remoteStreams[remoteUid] = e.streams[0]
                this.playRemoteAudio(remoteUid, e.streams[0])
            }
            pc.onicecandidate = e => {
                if(e.candidate){
                    this.sendWebSocketMessage({
                        type: 'ice_candidate',
                        serverId: this.serverId,
                        fromUserId: this.fromUserId,
                        toUserId: remoteUid,
                        data: e.candidate.toJSON()
                    })
                }
            }
            this.peerConnections[remoteUid] = pc
            return pc
        },
        /**
         * 创建RTC offer并发送给服务器
         * @param remoteUid 
         * @param localStream 
         */
        async createOffer(remoteUid, localStream) {
            const pc = this.createPeerConnection(remoteUid, localStream)
            const offer = await pc.createOffer()
            await pc.setLocalDescription(offer)
            this.sendWebSocketMessage({
                type: 'offer',
                serverId: this.serverId,
                fromUserId: this.fromUserId,
                toUserId: remoteUid,
                data: pc.localDescription
            })
        },
        /**
         * 处理RTC offer
         * @param message 
         * @param localStream 
         */
        async handleOffer(message, localStream) {
            const remoteUid = message.fromUserId
            const pc = this.createPeerConnection(remoteUid, localStream)
            await pc.setRemoteDescription(new RTCSessionDescription(message.data))
            const answer = await pc.createAnswer()
            await pc.setLocalDescription(answer)
            this.sendWebSocketMessage({
                type: 'answer',
                serverId: this.serverId,
                fromUserId: this.fromUserId,
                toUserId: remoteUid,
                data: pc.localDescription
            })
        },
        /**
         * 处理RTC answer
         * @param message 
         */
        async handleAnswer(message){
            const pc = this.peerConnections[message.fromUserId]
            if(pc){
                await pc.setRemoteDescription(new RTCSessionDescription(message.data))
            }
        },
        /**
         * 处理RTC candidate
         * @param message 
         */
        async handleIceCandidate(message){
            const pc = this.peerConnections[message.fromUserId]
            if(pc){
                await pc.addIceCandidate(new RTCIceCandidate(message.data))
            }
        },
        /**
         * 播放远程语音
         * @param {*} uid 
         * @param {*} stream 
         */
        playRemoteAudio(uid, stream){
            let audio = document.getElementById(`remote-audio-${uid}`)
            if(!audio){
                audio = document.createElement('audio')
                audio.id = `remote-audio-${uid}`
                audio.autoplay = true
                document.body.appendChild(audio)
            }
            audio.srcObject = stream
        },
        /**
         * 关闭指定成员的peerConnection
         * @param uid 
         */
        closePeerConnection(uid){
            const pc = this.peerConnections[uid]
            if(pc){
                pc.close()
                delete this.peerConnections[uid]
            }
            delete this.remoteStreams[uid]
            const audio = document.getElementById(`remote-audio-${uid}`)
            if(audio){
                audio.remove()
            }
        },
        /**
         * 关闭所有PeerConnection
         */
        closeAllPeerConnections(){
            Object.keys(this.peerConnections).forEach(uid => this.closePeerConnection(uid))
        },
        /**
         * 建立ws连接
         * @param serverId 
         * @returns 
         */
        connectWebSocket(serverId){
            const setting = VueCookie.get('setting')
            const loginStore = useLoginStore()
            this.fromUserId = loginStore.loginUser.uid
            if(this.ws && this.ws.readyState === WebSocket.OPEN){
                return
            }
            this.serverId = serverId
            const token = VueCookie.get('tokenuser')
            const wsAddress = settingStore.setting.usingAdvancedNetwork
                ? setting.wsAddress
                : `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/api`
            const url = `${wsAddress}/websocket/${serverId}?tokenuser=${token}`
            this.ws = new WebSocket(url)
            this.ws.onopen = () => {
                this.isConnected = true
                this.startHeartbeat()
            }
            this.ws.onmessage = (e) => {
                const data = JSON.parse(e.data)
                this.logList.push(data)
                if(data.type === 'pong'){
                    return
                }else if(data.type === 'user_joined'){
                    this.onlineUsers = data.onlineUserIds
                    if(data.fromUserId === this.fromUserId){
                        data.onlineUserIds.forEach(user => {
                            if(user.uid !== this.fromUserId && !this.peerConnections[user.uid]){
                                this.createOffer(user.uid, localDeviceStore.audioStream)
                            }
                        })
                    }
                    this.loadServerAllMember()
                    this.computeDisableMembers()
                }else if(data.type === 'user_left'){
                    this.onlineUsers = data.onlineUserIds
                    this.closePeerConnection(data.fromUserId)
                    this.loadServerAllMember()
                    this.computeDisableMembers()
                }else if(data.type === 'text_message'){
                    const user = this.onlineUsers.find(user => user.uid === data.fromUserId)
                    if(user) data.name = user.name
                    this.loadServerAllMember()
                    this.computeDisableMembers()
                }else if(data.type === 'offer' || data.type === 'answer' || data.type === 'ice_candidate'){
                    if(data.toUserId === this.fromUserId){
                        if(data.type === 'offer'){
                            this.handleOffer(data, localDeviceStore.audioStream)
                        }else if(data.type === 'answer'){
                            this.handleAnswer(data)
                        }else if(data.type === 'ice_candidate'){
                            this.handleIceCandidate(data)
                        }
                    }
                    return;
                }
                this.messages.push(data)
            }
            this.ws.onclose = () => {
                this.isConnected = false
                this.stopHeartbeat()
                setTimeout(() => {
                    if (!this.isConnected) this.connectWebSocket(this.serverId)
                }, 3000)
                console.log('连接关闭')
            }
            this.ws.onerror = error => {
                console.error('WebSocket error:', error)
            }
        },
        /**
         * 向服务器发送信息（上层接口）
         * @param text 
         */
        sendTextMessage(text){
            this.sendWebSocketMessage({
                type: 'text_message',
                serverId: this.serverId,
                fromUserId: this.fromUserId,
                toUserId: this.fromUserId,
                message: text
            })
        },
        /**
         * 向服务器发送信息
         * @param data 
         */
        sendWebSocketMessage(data) {
            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.ws.send(JSON.stringify(data))
            }
        },
        /**
         * 关闭ws连接
         */
        closeWebSocket() {
            this.stopHeartbeat()
            this.closeAllPeerConnections()
            Device.closeMicro()
            if (this.ws) {
                this.ws.onclose = null
                this.ws.close()
                this.ws = null
                this.isConnected = false
            }
            this.serverId = 'S'
            this.messages = []
            this.onlineUsers = []
            localDeviceStore.isMicOn = false
        },
        /**
         * 开始检测心跳
         */
        startHeartbeat() {
            this.heartbeatTimer = setInterval(() => {
                this.ws.send(JSON.stringify({
                    type: 'ping',
                    serverId: this.serverId,
                    fromUserId: this.fromUserId,
                    toUserId: this.fromUserId
                }))
            }, 5000)
        },
        /**
         * 回收心跳检测
         */
        stopHeartbeat() {
            if (this.heartbeatTimer) {
                clearInterval(this.heartbeatTimer)
                this.heartbeatTimer = null
            }
        },
        /**
         * 计算离线成员
         */
        computeDisableMembers(){
            this.serverUsers.forEach(server => {
                if(!this.onlineUsers.find(online => online.uid === server.serverUser)){
                    server.isDisable = 1
                }else{
                    server.isDisable = 0
                }
            })
            this.serverUsers = [...this.serverUsers].sort((a, b) => {
                return a.isDisable - b.isDisable
            })
        },
        /**
         * 加载房间内所有成员
         */
        async loadServerAllMember(){
            const result = await request.get({
                url: '/server/loadServerMember',
                params: {
                    serverId: this.serverId
                }
            })
            if(!result){
                return;
            }
            this.serverUsers = result.data
            this.computeDisableMembers()
        }
    }
})