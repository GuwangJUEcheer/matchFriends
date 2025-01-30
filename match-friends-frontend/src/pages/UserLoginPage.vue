<template>
    <van-form @submit="onSubmit">
  <van-cell-group inset>
    <van-field
      v-model="username"
      name="用户名"
      label="用户名"
      placeholder="用户名"
      :rules="[{ required: true, message: '请填写用户名' }]"
    />
    <!-- 密码输入框 type="password" -->
    <van-field
      v-model="password"
      type="password"
      name="密码"
      label="密码"
      placeholder="密码"
      :rules="[{ required: true, message: '请填写密码' }]"
    />
  </van-cell-group>
  <div style="margin: 16px;">
    <van-button round block type="primary" native-type="submit">
      提交
    </van-button>
  </div>
</van-form>

</template>

<script setup lang="ts">
    import { ref } from 'vue';
    import myaxios from '../plugins/my-axios';
    import { useRouter } from 'vue-router';
    import { showSuccessToast } from 'vant';
    const router = useRouter();
    const username = ref('');
    const password = ref('');
    const onSubmit =async (values) => {
     const res = await myaxios
        .post('/user/login', {
          username: username.value,
          password: password.value,
        });
      if (res.code === 0) {
        showSuccessToast('登录成功');
        // 跳转到首页
        router.replace('/');
    }};
</script>
<style scoped></style>

