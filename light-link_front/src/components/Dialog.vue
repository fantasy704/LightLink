<template>
    <el-dialog
        :width="width"
        :model-value="show"
        @close="close"
        :show-close="true"
        :close-on-click-modal="false"
        class="custom-dialog">
        <template #header>
            <slot name="title"></slot>
        </template>
        <slot name="content"></slot>
    </el-dialog>
</template>

<script setup>
    const props = defineProps({
        show:{
            type: Boolean,
            default: false
        },
        close: {
            type: Function,
            default: () => {}
        },
        width: {
            type: String,
            default: 'auto'
        }
    })
</script>

<style lang="scss" scoped>

</style>

<style lang="scss">

</style>