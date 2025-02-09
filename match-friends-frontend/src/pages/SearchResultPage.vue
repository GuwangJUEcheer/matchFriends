<!--  -->
<template>
  <van-card num="2" price="2.00" v-for="user in userList" :desc="user.profile" :title="user.username"
    :thumb="user.avatarUrl">
    <template #tags>
      <van-tag plain type="primary" v-for="item in user.tags" style="margin-right: 5px;margin-top: 8px;">
        {{ item }}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
    <van-empty v-if="userList.length === 0" text="数据为空" />
  </van-card>

</template>

<script setup lang="ts">

import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import type { UserType } from '../models/user.d.ts';
import { showSuccessToast, showFailToast } from 'vant';
import myaxios from '../plugins/my-axios.js';
import qs from 'qs';

const route = useRoute();

onMounted(
  async() => {
      const usersList:UserType[]= await myaxios.get('/user/search/tags', {
        params: {
          tagNameList: tags,
        },
        //将请求参数序列化
        paramsSerializer: (params) => {
          return qs.stringify(params, { indices: false });
        },
      })
        .then(function (response) {
          showSuccessToast({ message: '请求成功' });
          return response.data;
        })
        .catch(function (error) {
          console.error('/user/search/tags error', error);
          showFailToast({ message: '请求失败' });
          return [];
        });
        if(usersList){
          usersList.forEach(user => {
            if(user.tags){
              user.tags = JSON.parse(user.tags as string);
              console.log(user.tags);
            }
        });
        userList.value = usersList;
      }}
    );

const { tags } = route.query;

//用户列表
const userList = ref([] as UserType[]);

// const mockUser = ref({
//   id: 1,
//   username: 'JohnDoe',
//   avatarUrl: 'https://example.com/avatar.jpg',
//   gender: 1,
//   phone: '1234567890',
//   profile: '这是一段介绍',
//   email: 'johndoe@example.com',
//   isValid: 1,
//   createtime: new Date('2025-01-01T12:00:00'),
//   updatetime: new Date('2025-01-05T12:00:00'),
//   isDelete: 0,
//   roleId: 2,
//   planetCode: 'PLANET123',
//   tags: ['Java', '大连理工大学', '计算机科学与技术']
// } as UserType);
</script>

<style scoped></style>