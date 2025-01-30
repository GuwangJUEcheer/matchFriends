import { createApp } from 'vue';
import App from './App.vue';
import *  as VueRouter from 'vue-router';
import routes from './config/route';
import 'es6-promise/auto';

const router = VueRouter.createRouter({
    history: VueRouter.createWebHistory(),
    routes,
})
const app = createApp(App)
app.use(router)
app.mount('#app')


