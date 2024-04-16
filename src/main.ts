import { createApp } from 'vue'
import 'vant/lib/index.css';
import {Button, Icon, NavBar, Tabbar, TabbarItem} from 'vant';
import App from './App.vue'

const app = createApp(App);
app.mount('#app');
app.use(Button);
app.use(NavBar);
app.use(Icon);
app.use(Tabbar);
app.use(TabbarItem);
