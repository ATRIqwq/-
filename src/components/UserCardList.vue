<template>
  <van-skeleton :loading="props.loading" v-for="user in props.userList">
    <template #template>
      <div :style="{ display: 'flex', width: '100%' }">
        <van-skeleton-image/>
        <div :style="{ flex: 1, marginLeft: '16px' }">
          <van-skeleton-paragraph row-width="60%"/>
          <van-skeleton-paragraph/>
          <van-skeleton-paragraph/>
          <van-skeleton-paragraph/>
        </div>
      </div>
    </template>

    <van-card
        :desc="user.profile"
        :title="`${user.username} (${user.planetCode})`"
        @click="toUserShow(user.id)"
    >
      <template #thumb >
        <img width="100%" height="100%" id="images" :src="user.avatarUrl ?? defaultImage" >
      </template>

      <template #tags>
        <van-tag plain type="danger" v-for="tag in user.tags" style="margin-right: 8px; margin-top: 8px">
          {{ tag }}
        </van-tag>
      </template>

<!--      <template #bottom>-->
<!--        <div>-->
<!--          {{ '邮箱: ' + user.email }}-->
<!--        </div>-->
<!--        <div>-->
<!--          {{ '电话: ' + user.phone }}-->
<!--        </div>-->
<!--      </template>-->

<!--      <template #footer>-->
<!--        <van-icon name="phone-circle-o" size="30" />-->
<!--      </template>-->
    </van-card>
  </van-skeleton>
</template>


<script setup lang="ts">


import {userType} from "../module/user";
import {useRouter} from 'vue-router';
import {ref} from "vue";

const router = useRouter();
const defaultImage = ref('https://kano-img-bed.oss-cn-shanghai.aliyuncs.com/image-20240425130316821.png');


interface UserCardListProps{
  loading: boolean;
  userList: userType[];
}
// 给父组件设置默认值，保证数据不为空
const props= withDefaults(defineProps<UserCardListProps>(),{
  //@ts-ignore
  userList: [] as UserType[],
  loading:true,
});

//跳转到用户信息页
const toUserShow = (id:number) =>{
  router.push({
    path:'/userShow',
    query:{
      id,
    }
  })

}


</script>
<style scoped>
/* 标签颜色*/
.van-tag--danger.van-tag--plain {
  //color: #002fff;
}
</style>
