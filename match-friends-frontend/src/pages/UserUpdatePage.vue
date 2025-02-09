<template>
    <div v-if="user">
    <van-cell title="昵称" is-link to="/user/edit" :value="user.username"
        @click="toEdit('username', '昵称', user.username)" />
    <van-cell title="头像" is-link to="/user/edit" @click="toEdit('avatarUrl', '头像', user.avatarUrl)">
        <img :src="user.avatarUrl" alt="头像" />
    </van-cell>
    <van-cell title="性别" is-link to="/user/edit" :value="user.gender" @click="toEdit('gender', '性别', user.gender)" />
    <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="toEdit('phone', '电话', user.phone)" />
    <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="toEdit('email', '邮箱', user.email)" />
    <van-cell title="星球编号" :value="user.planetCode" @click="toEdit('planetCode', '星球编号', user.planetCode)" />
    <van-cell title="注册时间" :value="user.createtime.toDateString"
        @click="toEdit('createTime', '注册时间', user.createtime.toISOString())" />
</div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { UserType } from '../models/user.js';
import { onMounted, ref } from 'vue';
import getCurrentUser  from '../services/user.js';
const router = useRouter();

const user = ref<UserType>();

onMounted(
    async () =>{
    user.value = await getCurrentUser();
     if(!user.value){
         router.push('/user/login');
     }
}
);
const toEdit = (editKey: string, editName: string, currentValue: any) => {
    router.push({ path: '/user/edit', query: { editKey, editName, currentValue } });
}
</script>
<style scoped></style>