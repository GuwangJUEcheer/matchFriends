import Index from '../pages/index.vue';
import Team from '../pages/Team.vue';
//@ts-ignore
import TeamAddPage from '../pages/TeamAddPage.vue';
//@ts-ignore
import TeamUpdatePage from '../pages/TeamUpdatePage.vue';
import UserUpdatePage from '../pages/UserUpdatePage.vue';
//@ts-ignore
import SearchPage from '../pages/SearchPage.vue';
import SearchResultPage from '../pages/SearchResultPage.vue';
import EditUserPage from '../pages/EditUserPage.vue';
import UserLoginPage from '../pages/UserLoginPage.vue';
import UserCreateTeamPage from '../pages/UserCreateTeamPage.vue';
import UserJoinTeamPage from '../pages/UserJoinTeamPage.vue';
import Parent from '../sample/Parent.vue';
import UserPage from '../pages/UserPage.vue';
import { createRouter, createWebHistory } from 'vue-router';
import getCurrentUser  from '../services/user.ts';
const routes = [
    { path: '/', title:"首页", component: Index },
    { path: '/team',title:"找队伍", component: Team },
    { path: '/team/add',title:"加入队伍", component: TeamAddPage },
    { path: '/team/update',title:"修改队伍", component: TeamUpdatePage},
    { path: '/search',title:"检索", component: SearchPage },
    { path: '/user', title:"用户",component: UserPage },
    { path: '/user/update', title:"更新用户信息",component: UserUpdatePage },
    { path: '/user/edit', title:"编辑用户信息",component: EditUserPage},
    { path: '/user/list', title:"用户列表",component: SearchResultPage},
    { path: '/user/team/create', title:"创建队伍",component: UserCreateTeamPage},
    { path: '/user/team/join', title:"加入队伍",component: UserJoinTeamPage},
    { path: '/user/login', title:"登录页",component: UserLoginPage},
    { path: '/test',title:"测试页", component: Parent},
  ]

const router = createRouter({
  history: createWebHistory(),
  routes
});

const user = getCurrentUser();

router.beforeEach((to, from, next) => {
  // 允许访问登录页，防止无限重定向
  if (to.path === '/user/login' || from.path === '/user/login') {
    return next();
  }

  // **强制登录：如果用户未登录，则跳转到登录页**
  if (!user) {  // user 为空（null 或 undefined）
    return next('/user/login');
  }

  next(); // 继续跳转
});

  export default routes;