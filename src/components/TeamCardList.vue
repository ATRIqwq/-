<template>
  <div>
    <van-skeleton  :loading="props.loading" v-for="team in props.teamList" >
      <template #template>
        <div :style="{ display: 'flex', width: '100%' }">
          <van-skeleton-image />
          <div :style="{ flex: 1, marginLeft: '16px' }">
            <van-skeleton-paragraph row-width="60%" />
            <van-skeleton-paragraph />
            <van-skeleton-paragraph />
            <van-skeleton-paragraph />
          </div>
        </div>
      </template>

      <van-card
          :thumb="url"
          :desc="team.description"
          :title="`${team.name}`"
      >
        <template #tags>
          <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
            {{
              teamStatusEnum[team.status]
            }}
          </van-tag>
        </template>
        <template #bottom>
          <div>
            {{ `最大人数: ${team.hasJoinNum}/${team.maxNum}` }}
          </div>
          <div v-if="team.expireTime">
            {{ '过期时间: ' + team.expireTime }}
          </div>
          <div>
            {{ '创建时间: ' + team.createTime }}
          </div>
        </template>
        <template #footer>
          <van-button v-if="team.userId !== currentUser?.id && !team.hasJoin" size="small" type="primary"
                      plain @click="preJoinTeam(team)">加入队伍</van-button>

          <van-button v-if="team.userId === currentUser?.id" size="small" type="success"
                      plain @click="doUpdateTeam(team.id)">修改队伍</van-button>

          <van-button v-if="team.userId !== currentUser?.id && team.hasJoin" size="small" type="warning"
                      plain @click="doQuitTeam(team.id)">退出队伍</van-button>

          <van-button v-if="team.userId === currentUser?.id" size="small" type="danger"
                      plain @click="doDeleteTeam(team.id)">解散队伍</van-button>
        </template>
      </van-card>
    </van-skeleton>
  </div>

  <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
    <van-field v-model="password" placeholder="请输入密码"/>
  </van-dialog>

</template>

<script setup lang="ts">
import {TeamType} from "../module/team";
import {teamStatusEnum} from "../constants/team.ts";
import myAxios from "../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../service/userService.ts";

const router = useRouter();
const url = "https://kano-img-bed.oss-cn-shanghai.aliyuncs.com/20240429101152.png"
const currentUser = ref();
const showPasswordDialog = ref(false);
const password = ref('');
const joinTeamId = ref(0);

interface TeamCardListProps {
  loading: boolean;
  teamList: TeamType[];
}

onMounted(async ()=>{
  currentUser.value = await getCurrentUser();
})



const props = withDefaults(defineProps<TeamCardListProps>(), {
  // @ts-ignore
  teamList: [] as TeamType[],
  loading:true,
});


/**
 * 加入队伍
 */
const doJoinTeam = async () => {
  if (!joinTeamId.value){
    return;
  }

  const res = await myAxios.post('/team/join', {
    teamId: joinTeamId.value,
    password: password.value
  });
  if (res.data?.code === 0) {
    showSuccessToast('加入成功');
  } else {
    showFailToast('加入失败' + (res.data.description ? `，${res.data.description}` : ''));
    doJoinCancel();
  }
}

/**
 * 退出队伍
 */
const doQuitTeam = async (id:number) => {
  const res = await myAxios.post('/team/quit', {
    teamId: id,
  });
  if (res.data?.code === 0) {
    showSuccessToast('操作成功');
  } else {
    showFailToast('操作失败' + (res.data.description ? `，${res.data.description}` : ''));
  }
}

/**
 * 解散队伍
 */
const doDeleteTeam = async (id:number) => {
  const res = await myAxios.post('/team/delete', {
    teamId: id,
  });
  if (res.data?.code === 0) {
    showSuccessToast('操作成功');
  } else {
    showFailToast('操作失败' + (res.data.description ? `，${res.data.description}` : ''));
  }
}

/**
 * 跳转至更新队伍页
 * @param id
 */
const doUpdateTeam = (id: number) => {
  router.push({
    path: '/team/update',
    query: {
      id,
    }
  })
}

/**
 * 判断是不是加密房间，是的话显示密码框
 * @param team
 */
const preJoinTeam = (team: TeamType) => {
  joinTeamId.value = team.id;
  if (team.status === 0) {
    doJoinTeam();
  } else {
    showPasswordDialog.value = true;
  }
}

const doJoinCancel = () => {
  joinTeamId.value = 0;
  password.value = '';
}

</script>

<style scoped>
#teamCardList :deep(.van-image__img) {
  height: 128px;
  object-fit: unset;
}
</style>
