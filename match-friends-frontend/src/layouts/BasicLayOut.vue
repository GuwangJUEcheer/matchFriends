<!--  -->
<template>
    <van-nav-bar :title="title" left-text="返回" left-arrow
        @click-left="onClickLeft" @click-right="onClickRight">
        <template #right>
            <van-icon name="search" size="18" />
        </template>
    </van-nav-bar>

    <div id="content">
       <router-view />
    </div>

    <van-tabbar  route>
        <van-tabbar-item icon="home-o" to="/" name="index">主页</van-tabbar-item>
        <van-tabbar-item icon="search" to="/team" name="team">队伍</van-tabbar-item>
        <van-tabbar-item icon="friends-o" to="/user" name="user">个人</van-tabbar-item>
    </van-tabbar>
</template>

<script setup>
import { ref } from 'vue';
import { showLoadingToast, showSuccessToast, showFailToast } from 'vant';
import { useRouter,RouterView } from 'vue-router';
import routes from '../config/route';
const router = useRouter();
const DEFAULT_TITLE = '伙伴匹配系统';
const title = ref(DEFAULT_TITLE);
const onClickLeft = () => {
    //回到上一页
    router.back();
};
const onClickRight = () => {
    showSuccessToast({
        message: '点击了右侧按钮',
        duration: 1000,
    });
    router.push('/search');
};
//默认值是index 和主页的name属性保持一致
const active = ref("index");

router.beforeEach((to, from, next) => {
    const path = to.path;
    const route = routes.find(item => item.path === path);
    if (route && route.title) {
        title.value = route.title;
    }else{
        title.value = DEFAULT_TITLE;
    }

    next();
});
</script>

<style scoped>
#content{
    padding-bottom: 50px;
}
</style>