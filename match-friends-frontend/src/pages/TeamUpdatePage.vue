<!--  -->
<template>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="teamData.name" name="队伍名称" label="队伍名称" placeholder="队伍名称"
          :rules="[{ required: true, message: '请填写队伍名称' }]" />
  
        <van-field v-model="teamData.description" type="textarea" rows="4" autosize name="队伍描述" label="队伍描述"
          placeholder="请填写队伍描述" :rules="[{ required: false, message: '请填写队伍描述' }]" />
  
        <van-field v-model="result" is-link readonly name="picker" label="选择器" placeholder="选择队伍类型"
          @click="showPicker = true" />
  
        <van-field v-model="teamData.password" v-if="teamData.status === 2" type="password" name="密码" label="密码"
          placeholder="请输入密码" :rules="[{ required: true, message: '请填写密码' }]" />
  
        <van-popup v-model:show="showPicker" destroy-on-close position="bottom">
          <van-picker :columns="columns" :model-value="pickerValue" @confirm="onConfirm" @cancel="showPicker = false" />
        </van-popup>
        <van-field v-model="resultDate" is-link readonly name="datePicker" label="时间选择" placeholder="选择队伍过期时间"
          @click="showPickerDate = true" />
        <van-popup v-model:show="showPickerDate" destroy-on-close position="bottom">
          <van-date-picker :model-value="pickerValueDate" @confirm="onConfirmDate" @cancel="showPickerDate = false"
            :min-date="new Date()" />
        </van-popup>
  
        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="teamData.maxNum" />
          </template>
        </van-field>
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </template>
  
  <script setup lang="ts">
  import type Team from '../models/team.d.ts';
  import { useRouter,useRoute } from 'vue-router';
  import { onBeforeMount, onMounted, ref } from 'vue';
  import myaxios from '../plugins/my-axios.js';
  
  
  const router = useRouter();
  const route = useRoute();
  
  onBeforeMount(() => { 
        if(!route.query.id){
            router.push("/team");
        }
  })

 onMounted(async() => {
    const teamId = route.query.id;
    await myaxios.get('/team/get/'+ teamId).then(function (response) {
      teamData.value = response.data;
      result.value = columns[response.data.status].text;
      resultDate.value = response.data.expireTime.split('T')[0];
    })
     .catch(function (error) {
        console.error('/team/get error', error);
      });
  });
  
  //防止初始值被重置
  const teamData = ref<Team>({} as Team) 
  
  const result:any = ref([]);
  const pickerValue:any = ref([]);
  const showPicker = ref(false);
  const columns = [
    { text: '公开', value: 0 },
    { text: '私密', value: 1 },
    { text: '加密', value: 2 },
  ];
  
  const onConfirm = ({ selectedValues, selectedOptions }) => {
    result.value = selectedOptions[0]?.text;
    teamData.value.status = selectedOptions[0]?.value;
    pickerValue.value = selectedValues;
    showPicker.value = false;
  };
  
  const resultDate:any = ref([]);
  const showPickerDate = ref(false);
  const pickerValueDate = ref([]);
  const onConfirmDate = ({ selectedValues }) => {
    resultDate.value = selectedValues.join('-');
    teamData.value.expireTime = new Date(selectedValues.join('-'));
    pickerValue.value = selectedValues;
    showPickerDate.value = false;
  };
  
  
  const onSubmit = async () => {
    alert("nnn");
    const teamId = await myaxios.post('/team/update',{
      ...teamData.value,
    }).then(function (response) {
      return response.data;
    })
      .catch(function (error) {
        console.error('/user/search/tags error', error);
        result.value = "";
        resultDate.value = null;
      });
  
      if(teamId>0){
        router.push("/team");
      }
  }
  
  </script>
  
  <style scoped></style>