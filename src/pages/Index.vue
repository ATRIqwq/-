<template>
 <user-card-list :user-list="userList"/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {showFailToast, showSuccessToast} from "vant/lib/vant.es";
import myAxios from "../plugins/myAxios.ts";
import UserCardList from "../components/UserCardList.vue";


const route = useRoute();
const {tags} = route.query;

const userList = ref([]); //用户列表

onMounted(async () => {
  // 为给定 ID 的 user 创建请求
  const userListData = await myAxios.get('/user/recommend', {
    withCredentials: false,
    params: {},
  })
      .then(function (response) {
        console.log('/user/recommend succeed', response);
        showSuccessToast('请求成功');
        return response.data?.data;
      })
      .catch(function (error) {
        console.log('/user/recommend error', error);
        showFailToast('请求失败')
      });
  if (userListData) {
    userListData.forEach(user => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})

</script>

<style scoped>

</style>
