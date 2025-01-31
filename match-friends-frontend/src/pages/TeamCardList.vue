<template>
  <div>
    <!-- 当 teamList 为空时，显示 van-empty -->
    <van-empty v-if="props.teamList.length === 0" text="数据为空" />

    <!-- 循环渲染队伍卡片 -->
    <van-card v-for="team in props.teamList" :key="team.id" :desc="team.description" :title="team.name"
      :price="'最大人数: ' + team.maxNum" :thumb="teamsIcon">
      <template #tags>
        <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
          {{ teamsEnum[team.status] }}
        </van-tag>
      </template>

      <template #bottom>
        <div class="cardDiv">
          {{ '最大人数: ' + team.maxNum }}
        </div>
        <div v-if="team.expireTime" class="cardDiv">
          {{ '过期时间: ' + team.expireTime }}
        </div>
        <div class="cardDiv">
          {{ '创建时间: ' + team.createTime }}
        </div>
      </template>
      <template #footer>
        <van-button size="mini" @click="handleJoinTeam(team.id)">申请入队</van-button>
      </template>
    </van-card>
  </div>
</template>

<script setup lang="ts">
import type { Team } from '../models/team';  // 修正导入方式
import teamsEnum from '../models/teamsEnum';
import teamsIcon from '../assets/teamIcon.png';
import myaxios from '../plugins/my-axios';
import { showSuccessToast } from 'vant';
interface TeamCardListProps {
  teamList: Team[];
}

const props = withDefaults(defineProps<TeamCardListProps>(), {
  teamList: () => [], // 修正默认值
});

const handleJoinTeam = async (id) => {
  alert(id);
  // 处理申请入队逻辑
 const result = await myaxios.post(`/team/join`, {
      teamId: id,
      password:""
  }).then(res => {  
    showSuccessToast('申请成功');
    return res.data;
  }).catch(err => {
    console.error(err);
  });
}
</script>

<style scoped>
.cardDiv {
  margin-top: 8px
}
</style>
