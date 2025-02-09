<!--  -->
<template>
  <div style="display: block; align-items: center;">
    <van-button type="primary" @click="doMatchUsers">
      {{ mode === 'default' ? '所有用户' : '匹配用户' }}
    </van-button>
    <van-switch v-model="checked" :style="{ float: 'right', marginTop: '10px' }" />
  </div>
  <user-card-list :userList="userList" :loadings="loadings"/>
  <van-empty v-if="userList.length === 0" description="暂无数据" />
</template>

<script setup lang="ts">

import { ref, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import type { UserType } from '../models/user.d.ts';
import { showSuccessToast, showFailToast } from 'vant';
import UserCardList from './UserCardList.vue';
import myaxios from '../plugins/my-axios.js';

const route = useRoute();

const loadings = ref(true);
type ModelType = 'default' | 'match';
const mode = ref<ModelType>('default');

const loadData = async () => {
  let userListData = [];

  if (checked.value) {
    const num = 10;
    userListData = await myaxios.get('/user/match', {
      params: {
        num :10,
      }
    })
      .then(function (response) {
        return response.data;
      })
      .catch(function (error) {
        console.error('/user/match error', error);
      });
  } else {
    userListData = await myaxios.get('/user/recommend', {
      params: {
        pageSize: 8,
        pageNumber: 1,
      }
    }).then(function (response) { 
      return response.data.records; }).catch(function (error) {
      console.error('/user/match error', error);
    });
  }

  if (userListData) {
    userListData.forEach(user => {
      if (user.tags) {
        try {
          user.tags = JSON.parse(user.tags as string);
        } catch (e) {
          user.tags = [];
        }
      }
    });
    userList.value = userListData;
    loadings.value = false;
  }
};

const { tags } = route.query;

const checked = ref(false);

//用户列表
const userList = ref([] as UserType[]);

const doMatchUsers = () => {
  myaxios.get('/team/match', {
    params: {
      tags: tags as string
    }
  }).then(function (response) {
    showSuccessToast({ message: '请求成功' });
    console.log(response);
  })
    .catch(function (error) {
      console.error('/user/match error', error);
      showFailToast({ message: '请求失败' });
    });
};

//监听数据变化
watchEffect(() => {
  loadData();
});

</script>

<style scoped></style>