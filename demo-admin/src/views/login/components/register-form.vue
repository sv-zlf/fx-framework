<template>
  <div>
    <div class="register_form_box">
      <a-form :rules="rules" :model="form" layout="vertical" @submit="onSubmit">
        <a-form-item field="userName" :hide-asterisk="true">
          <a-input v-model="form.userName" allow-clear placeholder="请输入用户名">
            <template #prefix>
              <icon-user />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item field="nickName" :hide-asterisk="true">
          <a-input v-model="form.nickName" allow-clear placeholder="请输入昵称">
            <template #prefix>
              <icon-idcard />
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
        <a-form-item field="confirmPassword" :hide-asterisk="true">
          <a-input-password v-model="form.confirmPassword" allow-clear placeholder="请确认密码">
            <template #prefix>
              <icon-lock />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item field="email" :hide-asterisk="true">
          <a-input v-model="form.email" allow-clear placeholder="请输入邮箱（选填）">
            <template #prefix>
              <icon-email />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item field="phone" :hide-asterisk="true">
          <a-input v-model="form.phone" allow-clear placeholder="请输入手机号（选填）">
            <template #prefix>
              <icon-mobile />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button long type="primary" html-type="submit" :loading="loading">注册</a-button>
        </a-form-item>
      </a-form>
    </div>
    <div class="login-link" @click="onLogin">已有账号？立即登录</div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { register } from "@/api/system/user/index";

const router = useRouter();
const loading = ref(false);

const form = ref({
  userName: "",
  nickName: "",
  password: "",
  confirmPassword: "",
  email: "",
  phone: ""
});

const rules = ref({
  userName: [
    {
      required: true,
      message: "请输入用户名"
    },
    {
      minLength: 3,
      maxLength: 20,
      message: "用户名长度应在3-20位之间"
    }
  ],
  nickName: [
    {
      required: true,
      message: "请输入昵称"
    },
    {
      minLength: 2,
      maxLength: 20,
      message: "昵称长度应在2-20位之间"
    }
  ],
  password: [
    {
      required: true,
      message: "请输入密码"
    },
    {
      minLength: 6,
      maxLength: 20,
      message: "密码长度应在6-20位之间"
    }
  ],
  confirmPassword: [
    {
      required: true,
      message: "请确认密码"
    },
    {
      validator: (value: string, cb: any) => {
        if (value !== form.value.password) {
          cb("两次输入的密码不一致");
        } else {
          cb();
        }
      }
    }
  ],
  email: [
    {
      type: "email",
      message: "请输入正确的邮箱格式"
    }
  ],
  phone: [
    {
      match: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号"
    }
  ]
});

// 注册提交
const onSubmit = async ({ errors }: any) => {
  if (errors) return;
  await onRegister();
};

// 注册
const onRegister = async () => {
  try {
    loading.value = true;
    const { confirmPassword, ...registerData } = form.value;
    await register(registerData);
    arcoMessage("success", "注册成功，请登录");
    router.push("/login");
  } catch (error: any) {
  } finally {
    loading.value = false;
  }
};

// 跳转到登录页
const onLogin = () => {
  router.push("/login");
}
</script>

<style lang="scss" scoped>
.register_form_box {
  margin-top: 28px;

}
.login-link {
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
