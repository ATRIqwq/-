//@ts-ignore
import Index from "../pages/Index.vue";
import UserPage from "../pages/UserPage.vue";
import TeamPage from "../pages/TeamPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPages from "../pages/SearchResultPages.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";



const routes = [
    { path: '/', component: Index },
    { path: '/team', component: TeamPage },
    { path: '/user', component: UserPage },
    { path: '/search', component: SearchPage },
    { path: '/user/edit', component: UserEditPage },
    { path: '/user/list', component: SearchResultPages },
    { path: '/user/login', component: UserLoginPage },
]

export default routes;
