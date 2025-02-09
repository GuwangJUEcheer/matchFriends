<!--  -->
<template>
  <van-skeleton title avatar :row="3" :loading="props.loadings">
    <van-card num="2" price="2.00" v-for="user in props.userList" :desc="user.profile" :title="user.username"
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
  </van-skeleton>
  </template>
  
  <script setup lang="ts">
  interface UserCardListProps {
    loadings: boolean;
    userList: UserType[];
  }
  const props = withDefaults(defineProps<UserCardListProps>(),{
    loadings: true,
    //@ts-ignore
    userList: [] as UserType[],
  });
  import type { UserType } from '../models/user.d.ts';

  </script>
  
  <style scoped></style>