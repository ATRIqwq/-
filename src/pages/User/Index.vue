<template>
  <van-notice-bar
      color="#1989fa"
      background="#ecf9ff"
      left-icon="volume-o"
      style="margin-bottom: 10px"
      text="富强、民主、文明、和谐；自由、平等、公正、法治；爱国、敬业、诚信、友善。"
  />
  <van-search v-model="searchText" placeholder="搜索用户" @search="onSearch"/>
  <van-cell center title="匹配模式">
    <template #right-icon>
      <van-switch v-model="isMatchMode" />
    </template>
  </van-cell>
  <user-card-list :user-list="userList" :loading="loading"/>
  <van-empty v-if="!userList || userList.length <1" description="数据为空" />
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {showFailToast, showSuccessToast} from "vant/lib/vant.es";
import myAxios from "../../plugins/myAxios.ts";
import UserCardList from "../../components/UserCardList.vue";
import {userType} from "../../module/user";
import {watchEffect} from "vue";

const route = useRoute();
const {tags} = route.query;
const isMatchMode = ref<boolean>(false);
const userList = ref([]); //用户列表
const loading = ref(true);
const searchText = ref('');


const onSearch = (val) => {
  listUser(val);
}

/**
 * 搜索用户
 * @param val
 * @returns {Promise<void>}
 */
const listUser = async (val = '') => {
  loading.value = true;
  const res = await myAxios.get("/user/search", {
    params: {
      searchText: val,
      pageNum: 1,
    },
  });
  if (res.data?.code === 0) {
    res.data.data.forEach(user=>{
      if(user.tags){
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = res.data.data;
  }
  loading.value = false;
}



const loadData =async ()=>{
  let userListData;
  loading.value = true;
  if (isMatchMode.value) {
    const num = 10;
     userListData = await myAxios.get('/user/match', {
      params: {
        num,
      },
    })
        .then(function (response) {
          console.log('/user/match succeed', response);
          // showSuccessToast('请求成功');
          return response.data?.data;
        })
        .catch(function (error) {
          console.log('/user/match error', error);
          showFailToast('请求失败')
        });

  } else {
     userListData = await myAxios.get('/user/recommend', {
      withCredentials: true,
      params: {
        pageSize:8,
        pageNum:1
      },
    })
        .then(function (response) {
          console.log('/user/recommend succeed', response);
          // showSuccessToast('请求成功');
          return response.data?.data;
        })
        .catch(function (error) {
          console.log('/user/recommend error', error);
          showFailToast('请求失败')
        });
  }
  if (userListData) {
    userListData.forEach((user:userType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
  loading.value = false;
}

watchEffect(() => {
  loadData();
})



</script>

<style scoped>

</style>
