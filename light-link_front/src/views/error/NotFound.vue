<template>
<div class="not-found">
    <div class="illustration">
        <svg viewBox="0 0 200 120" width="200" height="120">
            <circle class="ring" cx="100" cy="60" r="45" fill="none" stroke-width="2" stroke-dasharray="6 4" opacity="0.6"/>
            <text class="main-text" x="100" y="52" text-anchor="middle" font-size="36" font-weight="700">404</text>
            <text class="sub-text" x="100" y="74" text-anchor="middle" font-size="10">Page Not Found</text>
            <circle class="dot" cx="60" cy="30" r="4" opacity="0.5"/>
            <circle class="dot" cx="145" cy="85" r="3" opacity="0.4"/>
            <circle class="dot" cx="155" cy="35" r="2.5" opacity="0.3"/>
        </svg>
    </div>
    <div class="title">{{ proxy.t('notFound.title') }}</div>
    <div class="description">{{ proxy.t('notFound.description') }}</div>
</div>
</template>

<script setup>
    import { getCurrentInstance } from 'vue'
    const { proxy } = getCurrentInstance()
</script>

<style scoped lang="scss">
$primary: var(--s-color-primary);
$on-primary: var(--s-color-on-primary);
$primary-container: var(--s-color-primary-container);
$on-primary-container: var(--s-color-on-primary-container);
$surface: var(--s-color-surface);
$surface-container: var(--s-color-surface-container);
$on-surface: var(--s-color-on-surface);
$on-surface-variant: var(--s-color-on-surface-variant);
$outline: var(--s-color-outline);

.not-found {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 24px;
    background: $surface;
    box-sizing: border-box;

    .illustration {
        margin-bottom: 24px;
        animation: float 3s ease-in-out infinite;

        .ring {
            stroke: $primary-container;
        }
        .main-text {
            fill: $on-surface;
        }
        .sub-text {
            fill: $on-surface-variant;
        }
        .dot {
            fill: $primary-container;
        }
    }

    .title {
        font-size: 24px;
        font-weight: 500;
        color: $on-surface;
        margin-bottom: 8px;
    }

    .description {
        font-size: 14px;
        color: $on-surface-variant;
        margin-bottom: 32px;
        text-align: center;
        line-height: 1.5;
    }

    .action-btn {
        --s-button-container-color: #{$primary-container};
        --s-button-content-color: #{$on-primary-container};
    }
}

@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-8px); }
}

@media (max-width: 480px) {
    .not-found {
        .illustration svg {
            width: 160px;
            height: 96px;
        }
        .title {
            font-size: 20px;
        }
    }
}
</style>