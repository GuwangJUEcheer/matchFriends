<template>
  <van-cell title="修改信息" is-link url="/user/update" />
  <van-cell title="我创建的队伍" is-link url="/user/team/create" />
  <van-cell title="我加入的队伍" is-link to="/user/team/join"/>
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