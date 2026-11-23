import { Snackbar } from 'sober'
export default {
    info(msg){
        Snackbar.builder({
            text: msg,
            type: 'info'
        })
    },
    success(msg){
        Snackbar.builder({
            text: msg,
            type: 'success'
        })
    },
    warning(msg){
        Snackbar.builder({
            text: msg,
            type: 'warning'
        })
    },
    error(msg){
        Snackbar.builder({
            text: msg,
            type: 'error'
        })
    }
}