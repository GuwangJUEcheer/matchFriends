<!--  -->
<template>
  <user-card-list :userList="userList" />
  <van-empty v-if="userList.length === 0" description="暂无数据" />
</template>

<script setup lang="ts">

import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import type { UserType } from '../models/user.d.ts';
import { showSuccessToast, showFailToast } from 'vant';
import UserCardList from './UserCardList.vue';
import myaxios from '../plugins/my-axios';

const route = useRoute();

onMounted(async () => {
  const usersList: UserType[] = await myaxios.get('/user/recommend', {
    params: {
      pageNumber: 8,
      pageSize: 10
    }
  }).then(function (response) {
    showSuccessToast({ message: '请求成功' });
    return response?.data?.records;
  })
    .catch(function (error) {
      console.error('/user/search/tags error', error);
      showFailToast({ message: '请求失败' });
      return [];
    });
  if (usersList) {
    usersList.forEach(user => {
      if (user.tags) {
        try{
          user.tags = JSON.parse(user.tags as string);
        }catch(e){
          user.tags = [];
        }
      }
    });
    userList.value = usersList;
  }
});


const { tags } = route.query;

//用户列表
const userList = ref([] as UserType[]);
</script>

<style scoped></style>