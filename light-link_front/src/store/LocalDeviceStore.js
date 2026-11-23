import {defineStore} from 'pinia'

export const useLocalDeviceStore = defineStore('localDeviceStore', {
    state: () => {
        return {
            audioDeviceList: [],
            curDevice: -1,
            audioStream: null,
            isMicOn: false,
            volumeAnimId: 0,
            audioContext: null,
            audioAnalyser: null,
            volume: 0
        }
    },
    actions: {
        setAudioDeviceList(list){
            this.audioDeviceList = list,
            this.curDevice = list.length > 0 ? 0 : null
        },
        setCurDevice(index){
            this.curDevice = index
        },
        setAudioStream(stream){
            this.audioStream = stream
        },
        watchMicro(){
            this.audioContext = new window.AudioContext()
            const mediaStreamSource = this.audioContext.createMediaStreamSource(this.audioStream)
            this.audioAnalyser = this.audioContext.createAnalyser()
            mediaStreamSource.connect(this.audioAnalyser)
            this.audioAnalyser.fftSize = 1024
            const bufferLength = this.audioAnalyser.frequencyBinCount
            const dataArray = new Uint8Array(bufferLength)
            const set = () => {
                this.audioAnalyser.getByteFrequencyData(dataArray)
                let value = 0
                for(let i = 0; i < dataArray.length; i++){
                    value += dataArray[i]
                }
                value = (value / bufferLength) * 2
                this.volume = Math.round(value > 100 ? 100 : value)
                this.volumeAnimId = requestAnimationFrame(set)
            }
            set()
        },
        selectAudioDevice(index){
            if(index >= 0 && index < this.audioDeviceList.length){
                
            }
        }
    }
})