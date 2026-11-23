import { ref, onUnmounted } from 'vue'
import { useLocalDeviceStore } from '../store/LocalDeviceStore'

const localDeviceStore = useLocalDeviceStore()

const notDeviceId = ['communications', 'default', ""]
/**
 * 初始化设备
 */
const initDevice = async () => {
    try{
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        localDeviceStore.setAudioStream(stream)
        localDeviceStore.isMicOn = true
        localDeviceStore.watchMicro()

        const eDevices = await navigator.mediaDevices.enumerateDevices()
        const audioDevices = eDevices.filter(device => device.kind === 'audioinput' && !!device.deviceId && !notDeviceId.includes(device.deviceId))

        localDeviceStore.setAudioDeviceList(audioDevices)
        if(localDeviceStore.curDevice !== -1){
            await openMicro(localDeviceStore.audioDeviceList[localDeviceStore.curDevice].deviceId)
        }else{
            await openMicro(-1)
        }
    }catch(e){
        console.error(e)
    }
}

/**
 * 打开指定麦克风设备
 * @param deviceId 设备ID
 */
const openMicro = async (deviceId) => {
    closeMicro()
    try{
        const audio = deviceId !== -1 ? {
            deviceId: {
                exact: deviceId
            }
        } : true
        const audioStream = await navigator.mediaDevices.getUserMedia({audio})
        localDeviceStore.setAudioStream(audioStream)
        localDeviceStore.isMicOn = true
        localDeviceStore.watchMicro()
    }catch(e){
        console.error(e)
    }
}

/**
 * 关闭麦克风
 */
const closeMicro = () => {
    if(localDeviceStore.audioStream){
        localDeviceStore.audioStream.getTracks().forEach(track => track.stop())
        localDeviceStore.audioStream = null
    }
    localDeviceStore.isMicOn = false
    if(localDeviceStore.volumeAnimId){
        cancelAnimationFrame(localDeviceStore.volumeAnimId)
        localDeviceStore.volumeAnimId = 0
    }
    if(localDeviceStore.audioContext){
        localDeviceStore.audioContext.close()
        localDeviceStore.audioContext = null
        localDeviceStore.audioAnalyser = null
    }
}

const toggleMic = () =>{
    if(!localDeviceStore.audioStream) return
    const newState = !localDeviceStore.isMicOn
    localDeviceStore.audioStream.getAudioTracks().forEach(track => {
        track.enabled = newState
    })
    localDeviceStore.isMicOn = newState
}

export default {
    initDevice,
    openMicro,
    closeMicro,
    toggleMic
}