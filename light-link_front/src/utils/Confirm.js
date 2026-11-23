import { ElMessageBox } from 'element-plus'
import { t } from './I18NUtil'

const confirm = ({ message, okfun, showCancelBtn = true, okText = t('common.confirm') }) => {
    ElMessageBox.confirm(message, t('common.tip'), {
        "close-on-click-modal": false,
        confirmButtonText: okText,
        cancelButtonText: t('common.cancel'),
        showCancelButton: showCancelBtn,
        type: 'info',
    }).then(async () => {
        if (okfun) {
            okfun();
        }
    }).catch(() => {
    });
};
export default confirm;