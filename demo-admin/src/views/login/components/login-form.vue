<template>
  <div>
    <div class="login_form_box">
      <a-form :rules="rules" :model="form" layout="vertical" @submit="onSubmit">
        <a-form-item field="username" :hide-asterisk="true">
          <a-input v-model="form.username" allow-clear placeholder="请输入用户名">
            <template #prefix>
              <icon-user />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item field="password" :hide-asterisk="true">
          <a-input-password v-model="form.password" allow-clear placeholder="请输入密码">
            <template #prefix>
              <icon-lock />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item field="remember">
          <div class="remember">
            <a-checkbox v-model="form.remember">记住我</a-checkbox>
            <div class="forgot-password">忘记密码</div>
          </div>
        </a-form-item>
        <a-form-item>
          <a-button long type="primary" html-type="submit">登录</a-button>
        </a-form-item>
      </a-form>
    </div>
    <div class="register" @click="goToRegister">立即注册</div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useUserInfoStore } from "@/store/modules/user-info";
import { login } from "@/api/system/user/index";
import { useRouteConfigStore } from "@/store/modules/route-config";
import { useSystemStore } from "@/store/modules/system";
let userStores = useUserInfoStore();
const routeStore = useRouteConfigStore();
const router = useRouter();
const form = ref({
  username: "admin",
  password: "123456",
  verifyCode: null,
  remember: false
});
const rules = ref({
  username: [
    {
      required: true,
      message: "请输入用户名"
    }
  ],
  password: [
    {
      required: true,
      message: "请输入密码"
    }
  ]
});

// 提交表单
const onSubmit = async ({ errors }: any) => {
  if (errors) return;
  onLogin();
};

// 登录
const onLogin = async () => {
  // 登录
  let res = await login(form.value);
  // 保存token
  await userStores.setToken(res.data);
  // 加载用户信息
  await userStores.setAccount();
  // 加载路由信息
  await routeStore.initSetRouter();

  arcoMessage("success", "登录成功");
  // 跳转到首页
  router.replace("/home");
};

// 跳转到注册
const goToRegister = () => {
  router.push("/register");
};
</script>

<style lang="scss" scoped>
.login_form_box {
  margin-top: 28px;
  .remember {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    .forgot-password {
      color: var(--color-primary);
      cursor: pointer;
    }
  }
}
.register {
  font-size: 14px;
  color: var(--color-text-3);
  text-align: center;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: var(--color-primary);
  }
}
</style>