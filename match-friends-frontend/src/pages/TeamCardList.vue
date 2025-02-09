<template>
  <div>
    <!-- 当 teamList 为空时，显示 van-empty -->
    <van-empty v-if="displayData.length === 0" text="数据为空" />
    <form action="/">
      <van-search v-model="value" show-action placeholder="请输入搜索关键词" @search="onSearch" @cancel="onCancel" />
    </form>

    <!-- 循环渲染队伍卡片 -->
    <van-card v-for="team in displayData" :key="team.id" :desc="team.description" :title="team.name"
      :price="'最大人数: ' + team.maxNum" :thumb="teamsIcon">
      <template #tags>
        <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
          {{ teamsEnum[team.status] }}
        </van-tag>
      </template>

      <template #bottom>
        <div class="cardDiv">
          {{ '队伍人数: ' + team.memberNum + '/' + team.maxNum }}
        </div>
        <div v-if="team.expireTime" class="cardDiv">
          {{ '过期时间: ' + team.expireTime }}
        </div>
        <div class="cardDiv">
          {{ '创建时间: ' + team.createTime }}
        </div>
      </template>
      <template #footer>
        <van-button size="mini" type="default" @click="handleJoinTeam(team.id, team.status)">申请入队</van-button>
        <van-button v-if="team.userId == currentUser.id" type="primary" size="mini"
          @click="doChangeTeam(team.id)">修改队伍信息</van-button>
        <van-button v-if="showDischarge" type="danger" size="mini" @click="dischargeTeam(team.id)">解散队伍</van-button>
        <van-button v-if="showQuit" type="warning" size="mini" @click="quitTeam(team.id)">退出队伍</van-button>
      </template>
    </van-card>
    <van-dialog v-model:show="show" title="入队申请" show-cancel-button>
      <van-field v-model="password" label="密码" placeholder="请输入密码" />
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import type Team from '../models/team';  // 修正导入方式
import teamsEnum from '../models/teamsEnum';
import teamsIcon from '../assets/teamIcon.png';
import myaxios from '../plugins/my-axios';
import { showSuccessToast } from 'vant';
import { ref, defineProps, onMounted, watch } from 'vue';
import getCurrentUser from '../services/user';
import { useRouter } from 'vue-router';
interface TeamCardListProps {
  teamList: Team[];
  showDischarge: boolean;
  showQuit: boolean;
}

const password = ref(''); // 密码输入框
const show = ref(false);

const router = useRouter();

const currentUser = ref();  // 修正变量名
onMounted(async () => {
  const user = await getCurrentUser();
  currentUser.value = user;
});
const props = withDefaults(defineProps<TeamCardListProps>(), {
  teamList: () => [], // 修正默认值
  showDischarge: false, //展示解散队伍按钮
  showQuit: false, //展示退出队伍按钮
});
const value = ref('');
// ✅ 创建 displayData 的副本，避免直接修改 props.teamList
const displayData = ref<Team[]>([]);

// ✅ 监听 props.teamList 变化，自动更新 displayData
watch(() => props.teamList, (newTeamList) => {
  displayData.value = [...newTeamList]; // 复制数组，避免修改源数据
}, { immediate: true });

const handleJoinTeam = async (id, status) => {
  if (status === 1) {
    show.value = true;
  }
  // 处理申请入队逻辑
  await myaxios.post(`/team/join`, {
    teamId: id,
    password: password.value
  }).then(res => {
    showSuccessToast('申请成功');
    return res.data;
  }).catch(err => {
    console.error(err);
  });
  password.value = '';
}

const onSearch = (val) => {
  displayData.value = props.teamList.filter(item => item.name.includes(val));
};
const onCancel = () => {
  value.value = '',
    displayData.value = props.teamList
};

const doChangeTeam = (id) => {
  // 处理修改队伍信息逻辑 
  // 跳转到修改队伍信息页面
  router.push({ path: "/team/update", query: { id: id } });
}

const dischargeTeam = async (id) => {
  // 处理解散队伍逻辑
  myaxios.get(`/team/delete/` + id).then(res => {
    showSuccessToast('解散成功');
    // 刷新页面
    window.location.reload();
  })
}

const quitTeam = async (id) => {
  // 处理退出队伍逻辑
  myaxios.post(`/team/leave`, {
    "teamId": id
  }).then(res => {
    showSuccessToast('退队成功');
    // 刷新页面
    window.location.reload();
  })
}

</script>

<style scoped>
.cardDiv {
  margin-top: 8px
}
</style>
