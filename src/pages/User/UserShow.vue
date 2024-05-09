<template v-if="user">
  <div class="center">
    <!--todo 展示默认头像-->
    <img class="img" :src="user.avatarUrl ?? defaultImage">
  </div>
  <div style="padding-top: 15px"/>

  <van-cell :value="user.username" icon="manager-o">
    <template #title>
      <span class="custom-title">昵称</span>
    </template>
  </van-cell>
  <van-cell :value="user.userAccount" icon="user-circle-o">
    <template #title>
      <span class="custom-title">账号</span>
    </template>
  </van-cell>
  <van-cell :value="genderMap[user.gender]" icon="like-o">
    <template #title>
      <span class="custom-title">性别</span>
    </template>
  </van-cell>
  <van-cell :value="user.phone" icon="comment-o">
    <template #title>
      <span class="custom-title">联系方式</span>
    </template>
  </van-cell>
  <van-cell title="邮箱" @click="showPopup" icon="envelop-o">
    <template #value>
      <div v-if="user.email" class="van-ellipsis">
        {{ user.email }}
      </div>
    </template>
  </van-cell>
  <van-popup v-model:show="show" :style="{ padding: '64px' }">{{ user.email }}</van-popup>

<!--  <van-cell value="点击查看" icon="cluster-o" @click="teams" is-link>-->
<!--    <template #title>-->
<!--      <span class="custom-title">已加队伍</span>-->
<!--    </template>-->
<!--  </van-cell>-->
  <van-cell title="简介" icon="chat-o">
    <template #value>
      <div v-if="user.profile" class="van-multi-ellipsis--l3">
        {{ user?.profile }}
      </div>
      <div v-if="!user.profile" class="van-ellipsis">
        暂无简介
      </div>
    </template>
  </van-cell>




</template>

<script setup lang="ts">
import {onMounted, ref, watchEffect} from "vue";
import {useRoute, useRouter} from "vue-router";
import myAxios from "../../plugins/myAxios.ts";
import {showFailToast} from "vant";
import {genderMap} from "../../module/userMap.ts";

const route = useRoute()
const router = useRouter()
const show = ref(false);
const defaultImage = ref('https://kano-img-bed.oss-cn-shanghai.aliyuncs.com/image-20240425130316821.png');
const user = ref({});
const toUserShow = ref({
  id:route.query.id,
})
const showPopup = () => {
  show.value = true;
};

const loginUser = ref({
  user: {},
  userIds: []
})

onMounted(async () => {
  if(route.query.id == null){
    showFailToast("用户为空");
  }
  const userId = Number(route.query.id);
  const res = await myAxios.get(`/user/${userId}`);
  console.log(res.data);
  if (res?.data.code === 0 && res.data.data){
    user.value = res.data.data;
  }




})




// const addUserApply = ref(false);
// const addUserApplyText = ref();
// const toAddUserApply = async (id: number) => {
//   const status = await myAxios.post("/friends/add", {
//     "receiveId": id,
//     "remark": addUserApplyText.value
//   })
//   if (status) {
//     showSuccessToast("申请成功")
//   }
// }
// const addUser = async () => {
//   //  dialog组件
//   showConfirmDialog({
//     message: '请确认是否添加' + user.value.username + '为好友?',
//   }).then(async () => {
//     const add = await myAxios.post(`/user/addUser/${user.value.id}`, {})
//     if (add) {
//       loginUser.value.userIds.push(user.value.id)
//       showSuccessToast("添加成功")
//     }
//   }).catch(() => {
//     showSuccessToast("修改成功")
//   });
// }






watchEffect(() => {
  console.log('响应式数据发生变化');
  // 在这里执行需要执行的副作用操作
});
</script>

<style scoped>
@import '../../css/userShow.css';
@import "../../css/public.css";

:deep(.van-popup--center) {
  max-width: none;
  border-radius: 5%;
}

</style>
