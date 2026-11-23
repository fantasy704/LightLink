import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: "/",
        name: "layout",
        component: () => import('@/views/layout/Layout.vue'),
        redirect: "/server",
        children: [
            {
                path: "/server",
                name: "server",
                component: () => import('@/views/server/Server.vue'),
            },
            {
                path: "/online/:serverId",
                name: "online",
                component: () => import('@/views/online/Online.vue')
            },
            {
                path: "/person",
                name: "person",
                component: () => import('@/views/person/Person.vue'),
            }

        ]
    },
    {
        path: "/invitation/:ink",
        name: "invitation",
        component: () => import('@/views/invitation/InviteView.vue')
    },
    {
        path: "/404",
        name: "not-found",
        component: () => import('@/views/error/NotFound.vue')
    },
    {
        path: "/:pathMatch(.*)*",
        redirect: "/404"
    }
]

export default createRouter({
    history: createWebHistory(),
    routes
})