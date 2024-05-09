//@ts-ignore
import Index from "../pages/User/Index.vue";
import UserPage from "../pages/User/UserPage.vue";
import TeamPage from "../pages/TeamPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/User/UserEditPage.vue";
import SearchResultPages from "../pages/SearchResultPages.vue";
import UserLoginPage from "../pages/User/UserLoginPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import UserTeamJoinPage from "../pages/User/UserTeamJoinPage.vue";
import UserTeamCreatePage from "../pages/User/UserTeamCreatePage.vue";
import UserUpdatePage from "../pages/User/UserUpdatePage.vue";
import UserRegisterPage from "../pages/User/UserRegisterPage.vue";
import UserShow from "../pages/User/UserShow.vue";



const routes = [
    { path: '/', component: Index },
    { path: '/team',title:'找队伍', component: TeamPage },
    { path: '/team/add', title:'添加队伍',component: TeamAddPage },
    { path: '/team/update',title:'更新队伍', component: TeamUpdatePage },
    { path: '/search', title:'找伙伴',component: SearchPage },
    { path: '/user',title:'个人信息', component: UserPage },
    { path: '/user/update', title:'更新信息',component: UserUpdatePage },
    { path: '/user/edit',title:'编辑信息', component: UserEditPage },
    { path: '/user/list',title:'用户列表', component: SearchResultPages },
    { path: '/userShow', title: '查看用户',component: UserShow },
    { path: '/user/login',title:'登录', component: UserLoginPage },
    { path: '/user/register',title:'注册', component: UserRegisterPage },
    { path: '/user/team/join',title:'加入队伍', component: UserTeamJoinPage },
    { path: '/user/team/create',title:'创建队伍', component: UserTeamCreatePage },
]

export default routes;
