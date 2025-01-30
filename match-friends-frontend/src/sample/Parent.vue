<template>
    <h1>父组件</h1>
    <ChildFirst testProps="父组件传递给子组件的属性"/>
    <ChildSecond @test-emit="handleChildSecondEmit" />
</template>
  
<script setup lang="ts">
   import ChildFirst from './ChildFirst.vue';
   import ChildSecond from './ChildSecond.vue';
   const handleChildSecondEmit = (msg: string) => {
      console.log('父组件接收到子组件的消息：' + msg);
   };
</script>
  
<style scoped></style>