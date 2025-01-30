<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field v-model="editUser.currentValue as string" :name="editUser.editKey as string" :label="editUser.editName as string" placeholder="用户名"
                :rules="[{ required: true, message: '请填写用户名' }]" />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { useRoute,useRouter } from 'vue-router';
import myaxios from '../plugins/my-axios';
import {onMounted, ref} from 'vue';
import { showFailToast } from 'vant';

import getCurrentUser  from '../services/user'; 

const route = useRoute();
const router = useRouter();

const editUser = ref({
    editKey: route.query.editKey,
    currentValue: route.query.currentValue,
    editName:route.query.editName
}
);
const onSubmit = async () => {
    const currentUser = await getCurrentUser();
    if(!currentUser){
        showFailToast("请先登录");
        router.push('/user/login');
        return;
    }
    const res = await myaxios.post('/user/update', {
      ...currentUser,
     //读取ref类型的变量值
     [editUser.value.editKey as string]: editUser.value.currentValue
    });
    if (res.code === 0 && res.data>0)  {
        router.back();
    }    
}
</script>
<style scoped></style>