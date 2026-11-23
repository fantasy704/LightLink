<template>
    <div class="using_info">
        <div class="info_title">{{ proxy.t('introduce.title') }}</div>
        <div class="step_list">
            <div class="step_item">
                <div class="step_number">1</div>
                <div class="step_content">
                    <div class="step_label">{{ proxy.t('introduce.step1Title') }}</div>
                    <div class="step_desc">{{ proxy.t('introduce.step1Desc') }}</div>
                </div>
            </div>
            <div class="step_line"></div>
            <div class="step_item">
                <div class="step_number">2</div>
                <div class="step_content">
                    <div class="step_label">{{ proxy.t('introduce.step2Title') }}</div>
                    <div class="step_desc">{{ proxy.t('introduce.step2Desc') }}</div>
                </div>
            </div>
            <div class="step_line"></div>
            <div class="step_item">
                <div class="step_number">3</div>
                <div class="step_content">
                    <div class="step_label">{{ proxy.t('introduce.step3Title') }}</div>
                    <div class="step_desc">{{ proxy.t('introduce.step3Desc') }}</div>
                </div>
            </div>
            <div class="step_line"></div>
            <div class="step_item">
                <div class="step_number">4</div>
                <div class="step_content">
                    <div class="step_label">{{ proxy.t('introduce.step4Title') }}</div>
                    <div class="step_desc">{{ proxy.t('introduce.step4Desc') }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { getCurrentInstance } from 'vue'
    const { proxy } = getCurrentInstance()
</script>

<style lang="scss">
.using_info{
    width: 90%;
    max-width: 500px;
    padding: 24px;
    background: var(--s-color-surface-container, #fff);
    border: 1px solid var(--s-color-outline-variant, #e0e0e0);
    border-radius: 16px;
    margin-bottom: 24px;
    .info_title{
        font-size: 22px;
        font-weight: 500;
        color: var(--s-color-on-surface, #111);
        text-align: center;
        margin-bottom: 20px;
        letter-spacing: 0.15px;
    }
    .step_list{
        display: flex;
        flex-direction: column;
        gap: 0;
    }
    .step_item{
        display: flex;
        align-items: flex-start;
        gap: 16px;
        padding: 12px 0;
    }
    .step_number{
        flex-shrink: 0;
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: var(--s-color-primary-container, #d0e4ff);
        color: var(--s-color-on-primary-container, #001e3c);
        font-size: 14px;
        font-weight: 500;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .step_content{
        flex: 1;
        padding-top: 6px;
    }
    .step_label{
        font-size: 16px;
        font-weight: 500;
        color: var(--s-color-on-surface, #111);
        margin-bottom: 2px;
        letter-spacing: 0.1px;
    }
    .step_desc{
        font-size: 14px;
        color: var(--s-color-on-surface-variant, #666);
        line-height: 1.43;
        letter-spacing: 0.25px;
    }
    .step_line{
        width: 1px;
        height: 8px;
        background: var(--s-color-outline-variant, #e0e0e0);
        margin-left: 17.5px;
    }
}
</style>