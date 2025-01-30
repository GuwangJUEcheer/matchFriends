import Index from '../pages/index.vue';
import Team from '../pages/Team.vue';
import TeamAddPage from '../pages/TeamAddPage.vue';
import User from '../pages/User.vue';
import SearchPage from '../pages/SearchPage.vue';
import SearchResultPage from '../pages/SearchResultPage.vue';
import EditUserPage from '../pages/EditUserPage.vue';
import UserLoginPage from '../pages/UserLoginPage.vue';
import Parent from '../sample/Parent.vue';

const routes = [
    { path: '/', component: Index },
    { path: '/team', component: Team },
    { path: '/team/add', component: TeamAddPage },
    { path: '/user', component: User },
    { path: '/search', component: SearchPage },
    { path: '/user/edit', component: EditUserPage},
    { path: '/user/list', component: SearchResultPage},
    { path: '/user/login', component: UserLoginPage},
    { path: '/test', component: Parent},
  ]

  export default routes;