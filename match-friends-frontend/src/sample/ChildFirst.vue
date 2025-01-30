<template>
    <h2>Child First</h2>
    <h3>{{ props.testProps }}</h3>
    <van-button type="primary" @click="emitter.emit('test-emitter-sample',testData)">测试不同组件通信</van-button>
</template>
  
  <script setup lang="ts">
   import emitter from '../config/emit';
   import { defineProps, ref } from 'vue';
   const props = defineProps<{ testProps: string }>();
   const testData = ref('哈哈哈 我成功了!!!! 多多');
  </script>
  
  <style scoped></style>