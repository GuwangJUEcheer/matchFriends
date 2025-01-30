<template>
    <h2>ChildSecond</h2>
    <van-button type="primary" @click="testClick">测试点击</van-button>
    <van-cell-group inset>
      <van-field v-model="computer" label="文本" placeholder="请输入用户名" />
    </van-cell-group>
</template>
  
  <script setup lang="ts">
  import emitter from '../config/emit';
  import { defineEmits, ref } from 'vue'
  const emit = defineEmits(['test-emit'])
  const computer =ref('');
  function testClick(){
     emit('test-emit', '这是第二个组件传值给父组件')
   }
   emitter.on('test-emitter-sample', (data:string) => {
      computer.value = data;
      console.log('第二个组件接收到第一个组件传过来的数据', data)
    })  
  </script>
  
  <style scoped></style>