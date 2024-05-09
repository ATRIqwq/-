<template>
  <div>

    <van-row justify="center" style="padding-top: 60px" >
      <van-image
          round
          width="10rem"
          height="10rem"
          :src="imgUrl" class="userImage"
      />
    </van-row>
    <van-row justify="center" style="font-size: 20px; font-weight: bold; padding-top: 30px; padding-bottom: 30px">"心语——寻找知音之友"</van-row>

  </div>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <!-- 通过 pattern 进行正则校验 -->
      <van-field
          label="昵称"
          v-model="username"
          name="username"
          placeholder="请输入昵称"
          :rules="[{ required:true, message: '请输入昵称' }]"
      />

      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="请输入账号"
          :rules="[{ validator: validateUserAccount, message: '长度在4~16之间，允许字母数字下划线' }]"
      />
      <van-field
          v-model="userPassword"
          :type="showPassword ? 'text' : 'password'"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ validator,message:'长度在8~18之间，允许字母数字下划线' }]"
          :right-icon="showPassword ? zhengyan : biyan"
          @click-right-icon="showPassword = !showPassword "/>

      <van-field
          label="确认密码"
          v-model="checkPassword"
          name="checkPassword"
          :type="showPassword ? 'text' : 'password'"
          placeholder="请确认密码"
          :rules="[{ validator, message: '以字母开头，长度在8~18之间，允许字母数字下划线' }]"
          :right-icon="showPassword ? zhengyan : biyan"
          @click-right-icon="showPassword = !showPassword " />

      <van-field
          label="星球编号"
          v-model="planetCode"
          name="planetCode"
          placeholder="请输入星球编号"
          :rules="[{ required:true, message: '请输入星球编号' }]"
      />

    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        注册账号
      </van-button>
    </div>
    <van-row  >
      <van-col offset="8"  span="14" >
        <van-cell class="loginUnderline" to="/user/login" value="已有，账号点击登录" />
      </van-col>
    </van-row>
  </van-form>
</template>

<script setup>

import myAxios from "../../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import zhengyan from "../../../public/眼睛.png";
import biyan from "../../../public/闭眼睛.png";

const router = useRouter();
const route = useRoute();

const username = ref('');
const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');
const planetCode = ref('');
const imgUrl = ref('https://kano-img-bed.oss-cn-shanghai.aliyuncs.com/image-20240425130316821.png');
const showPassword = ref(false);

/**
 * 登录校验规则
 * @param val
 * @returns {string}
 */
const validateUserAccount = (val) => {
  // const pattern = /[a-zA-Z]\w{3,15}$/;
  const pattern = /^\w{3,15}$/;
  if (!val) {
    return '请输入账号';
  } else if (!pattern.test(val)) {
    return '长度在4~16之间，只能包含字母、数字和下划线';
  }
};
const validator = (val) =>{
  // const pattern = /^[a-zA-Z]\w{7,17}$/;
  const pattern = /^\w{6,17}$/;
  if (!val) {
    return '请输入密码';
  } else if (!pattern.test(val)) {
    return '长度在8~18之间，只能包含字母、数字和下划线';
  }
}

const onSubmit =async () =>{
  const res = await myAxios.post('/user/register',{
    username:username.value,
    userAccount:userAccount.value,
    userPassword:userPassword.value,
    checkPassword:userPassword.value,
    planetCode:planetCode.value,
  })
  if(res.data){
    showSuccessToast("注册成功")
    await router.push({
      path: '/user/login'
    })
  }
}

</script>

<style scoped>
@import "../../css/image.css";
@import "../../css/loginUnderline.css";
</style>
