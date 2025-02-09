<!--  -->
<template>
     <van-tabs v-model:active="active"  type="card" @change="handleTabChange">
          <van-tab title="公开" name = "public"></van-tab>
          <van-tab title="私密" name = "private"></van-tab>
     </van-tabs>

     <TeamCardList :teamList="teamList" :showDischarge="false" :showQuit="false" />
     <van-button type="primary" @click="doCreateTeam" class="add-button" icon="plus"></van-button>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import TeamCardList from './TeamCardList.vue';
import { onMounted } from 'vue';
import type Team from '../models/team.d.ts';
import myaxios from '../plugins/my-axios.ts';
const router = useRouter();
const doCreateTeam = () => {

     router.push("/team/add");
}
const originTeamList = ref<Team[]>([]);
const teamList = ref<Team[]>([]);
onMounted(async () => {
     const teamResponse: Team[] = await myaxios.post('/team/list',{status:0}).then(function (response) {
          return response.data;
     })
          .catch(function (error) {
               return [];
          });
     originTeamList.value = teamResponse;
     teamList.value=[...originTeamList.value].filter(team => team.status === 0);
}
);
const active = ref('public');

const handleTabChange =async () => {
   let teamListBake = [...originTeamList.value];
   if(active.value === 'public'){
        teamListBake = teamListBake.filter(team => team.status === 0);
   }else{
       teamListBake = teamListBake.filter(team => team.status !== 0);
   }
   teamList.value = teamListBake;
}
</script>

<style scoped>
.add-button {
     position: fixed;
     bottom: 60px;
     width: 50px;
     right: 12px;
     height: 50px;
     border-radius: 50%;
}
</style>