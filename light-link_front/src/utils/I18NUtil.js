import zhCN from '@/i18n/zh-CN'
import enUS from '@/i18n/en-US'
import VueCookies from 'vue-cookies'
import { useSettingStore } from '@/store/SettingStore'

const DEFAULT_LANGUAGE = 'zh-CN'
const messages = {
    'zh-CN': zhCN,
    'en-US': enUS
}

// 内部方法：优先系统设置，其次持久化的 cookie，最后默认中文
function getLanguage() {
    try {
        const language = useSettingStore().setting?.language
        if (messages[language]) return language
    } catch (e) {
        // 没有 active pinia 时忽略
    }
    try {
        const setting = VueCookies.get('setting')
        if (setting && typeof setting === 'object' && messages[setting.language]) {
            return setting.language
        }
    } catch (e) {
        // cookie 解析失败时忽略
    }
    return DEFAULT_LANGUAGE
}

/**
 * 根据系统设置的语言获取关键字
 * @param {string} key 如 'common.confirm'
 * @returns {string}
 */
export function t(key) {
    const language = getLanguage()
    return messages[language][key] ?? messages[DEFAULT_LANGUAGE][key] ?? key
}

export default { t }
