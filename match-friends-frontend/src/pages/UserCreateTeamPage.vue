<!--  -->
<template>
     <van-button type="primary" @click="doCreateTeam">我创建的队伍</van-button>
     <TeamCardList :teamList="teamList" :showDischarge="true" :showQuit="false"/>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import TeamCardList from './TeamCardList.vue';
import { onMounted } from 'vue';
import type Team from '../models/team';
import myaxios from '../plugins/my-axios';
const router = useRouter();
const doCreateTeam = () => {
     router.push("/team/add");
}
const teamList = ref<Team[]>([]);
onMounted(async () => {
     const teamResponse: Team[] = await myaxios.get('/team/hasCreated').then(function (response) {
          return response.data;
     })
          .catch(function (error) {
               return [];
          });
     teamList.value = teamResponse;
}
);
</script>

<style scoped></style>