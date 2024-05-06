<template>
  <div id="teamPage">
    <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch"/>
    <van-button size="small" type="primary"  @click="doJoinTeam">创建队伍</van-button>
    <team-card-list :teamList :loading="loading"/>
    <van-empty v-if="teamList?.length < 1" description="数据为空" />

  </div>

</template>
<script setup lang="ts">
import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast} from "vant";

const router = useRouter()
const teamList = ref([])
const searchText = ref('')
const loading = ref(true);

onMounted(async ()=>{
  listTeam();
});


const onSearch = (val) => {
  listTeam(val);
}

/**
 * 搜索队伍
 * @param val
 * @returns {Promise<void>}
 */
const listTeam = async (val = '') => {
  loading.value = true;
  const res = await myAxios.get("/team/list/my/create", {
    params: {
      searchText: val,
      pageNum: 1,
    },
  });
  if (res.data?.code === 0) {
    teamList.value = res.data.data;
  } else {
    showFailToast('加载队伍失败，请刷新重试');
  }
  loading.value = false;
}

//跳转到队伍表单
const doJoinTeam = ()=>{
  router.push({
    path:'/team/add'
  })
}


</script>
<style scoped>

</style>
