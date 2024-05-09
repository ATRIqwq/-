<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<script setup>
import {ref} from "vue";
import {useRoute,useRouter} from "vue-router"
import {getCurrentUser} from "../../service/userService.ts";
import myAxios from "../../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";


const route = useRoute();
const router = useRouter();


//接收user页面传递的参数  'email','电话',user.email
const editUser = ref({
  editKey: route.query.editKey,
  currentValue: route.query.currentValue,
  editName: route.query.editName,
})

const onSubmit =async () => {
  //把editKey currentValue editName提交到后台
  const currentUser = await getCurrentUser();
  console.log(currentUser)
  if (!currentUser){
    showFailToast("用户未登录");
  }

  const res = await  myAxios.post('/user/update',{
    'id': currentUser.id,
    [editUser.value.editKey]: editUser.value.currentValue
  })
  console.log(res,'更新请求')
  if (res.data.code === 0 && res.data.data >0){
    showSuccessToast('修改成功');
    router.back();
  } else {
    showFailToast('修改错误');
  }

}

</script>

<style scoped>

</style>
