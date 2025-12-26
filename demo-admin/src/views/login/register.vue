<template>
  <div class="container">
    <div class="login">
      <LoginBanner v-if="isPc" />
      <div class="login_box">
        <div class="login_title">创建账户</div>
<!--        <div class="login_subtitle">注册您的账户以开始使用系统</div>-->
        <RegisterForm />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import LoginBanner from "@/views/login/components/login-banner.vue";
import RegisterForm from "@/views/login/components/register-form.vue";
import { useDevicesSize } from "@/hooks/useDevicesSize";
const { isPc } = useDevicesSize();
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  height: 100vh;
  overflow: hidden;
  .login {
    position: absolute;
    top: 50%;
    left: 50%;
    display: flex;
    align-items: center;
    max-width: 1000px;
    height: 600px;
    box-shadow: 0 0 8px 1px var(--color-fill-2);
    transform: translate(-50%, -50%);
    .login_box {
      position: relative;
      box-sizing: border-box;
      width: 350px;
      height: 100%;
      padding: 40px 30px 30px;
      .login_title {
        margin-bottom: 8px;
        font-size: 28px;
        font-weight: 600;
        color: var(--color-text-1);
      }
      .login_subtitle {
        margin-bottom: 32px;
        font-size: 14px;
        color: var(--color-text-3);
        line-height: 1.5;
      }
    }
  }
}
</style>
