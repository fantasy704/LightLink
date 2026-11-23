<template>
<div>
    <s-navigation class="container">
        <s-navigation-item v-for="(item, index) in tabs" @click="navigatoTo(item.index)" :selected="tabStore.value === item.index" :key="index">
            <s-icon :src="item.icon" slot="icon" size="16"></s-icon>
            <div slot="text">{{proxy.t(item.name)}}</div>
        </s-navigation-item>
    </s-navigation>
</div>
</template>

<script setup>
    import { useRouter, useRoute } from 'vue-router';
    import { onMounted, watch } from 'vue';
    import { useTabStore } from '../../store/TabStore';
    import { useOnlineStore } from '../../store/OnlineStore';
    import { getCurrentInstance } from 'vue';
    const { proxy } = getCurrentInstance()
    const tabStore = useTabStore();
    const router = useRouter();
    const route = useRoute();
    const onlineStore = useOnlineStore();

    function syncTabFromRoute() {
        const path = route.path;
        if (path.startsWith('/online')) {
            tabStore.toggleTab(1);
        } else if (path.startsWith('/person')) {
            tabStore.toggleTab(2);
        } else {
            tabStore.toggleTab(0);
        }
    }

    onMounted(syncTabFromRoute);
    watch(() => route.path, syncTabFromRoute);

    const tabs = [
        {
            index: 0,
            icon: '/icons/server.svg',
            name: 'tabbar.server',
            path: '/server'
        },
        {
            index: 1,
            icon: '/icons/online.svg',
            name: 'tabbar.room',
            path: '/online'
        },
        {
            index: 2,
            icon: '/icons/person.svg',
            name: 'tabbar.profile',
            path: '/person'
        }
    ]

    const navigatoTo = index => {
        tabStore.toggleTab(index);
        if(index == 1){
            router.push(tabs[index].path + '/' + onlineStore.serverId)
        }else{
            router.push(tabs[index].path)
        }
    }
</script>

<style scoped lang="scss">
.container{
    height: 65px;
}
</style>