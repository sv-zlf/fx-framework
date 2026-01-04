<script lang="ts" setup>
import { ref } from "vue";
import { LOGIN_PAGE } from "@/router/config";
import {register} from "@/api/login";

definePage({
  style: {
    navigationBarTitleText: '注册',
  },
})

// 表单数据
const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  phone: "",
  code: "",
});

// 是否显示密码
const showPassword = ref(false);
const showConfirmPassword = ref(false);

// 发送验证码状态
const sendCodeLoading = ref(false);
const codeSent = ref(false);
const countdown = ref(0);
let countdownInterval: number;

// 注册中状态
const loading = ref(false);

// 切换密码显示
const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const toggleConfirmPassword = () => {
  showConfirmPassword.value = !showConfirmPassword.value;
};

// 验证表单
const validateForm = () => {
  if (!registerForm.value.username) {
    uni.showToast({title:"请输入用户名", icon:"none"});
    return false;
  }
  if (registerForm.value.username.length < 3) {
    uni.showToast({title:"用户名至少3个字符", icon:"none"});
    return false;
  }
  if (!registerForm.value.password) {
    uni.showToast({title:"请输入密码", icon:"none"});
    return false;
  }
  if (registerForm.value.password.length < 6) {
    uni.showToast({title:"密码至少6个字符", icon:"none"});
    return false;
  }
  if (!registerForm.value.confirmPassword) {
    uni.showToast({title:"请确认密码", icon:"none"});
    return false;
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    uni.showToast({title:"两次密码不一致"});
    return false;
  }
  if (!registerForm.value.phone) {
    uni.showToast({title:"请输入手机号", icon:"none"});
    return false;
  }
  if (!/^1[3-9]\d{9}$/.test(registerForm.value.phone)) {
    uni.showToast({title:"手机号格式不正确", icon:"none"});
    return false;
  }
  return true;
};

// 发送验证码
// const handleSendCode = async () => {
//   if (!registerForm.value.phone) {
//     uni.showToast("请输入手机号");
//     return;
//   }
//   if (!/^1[3-9]\d{9}$/.test(registerForm.value.phone)) {
//     uni.showToast("手机号格式不正确");
//     return;
//   }
//
//   sendCodeLoading.value = true;
//   try {
//     // TODO: 调用发送验证码接口
//     await new Promise((resolve) => setTimeout(resolve, 1000));
//
//     codeSent.value = true;
//     countdown.value = 60;
//     countdownInterval = setInterval(() => {
//       countdown.value--;
//       if (countdown.value <= 0) {
//         clearInterval(countdownInterval);
//         codeSent.value = false;
//       }
//     }, 1000);
//
//     uni.showToast({
//       type: "success",
//       message: "验证码已发送",
//       icon: true,
//     });
//   } catch (error) {
//     uni.showToast({
//       type: "error",
//       message: "发送失败，请重试",
//     });
//   } finally {
//     sendCodeLoading.value = false;
//   }
// };

// 注册
const handleRegister = async () => {
  if (!validateForm()) {
    return;
  }

  loading.value = true;
  try {
    // 调用注册接口
    await register(registerForm.value);
    uni.showToast({
      icon: 'none',
      title: '注册成功',
    });

    // 注册成功后跳转
    uni.navigateTo({
        url: LOGIN_PAGE,
    });
  } catch (error) {

  } finally {
    loading.value = false;
  }
};

// 返回登录
const handleGoToLogin = () => {
  uni.navigateTo({
    url: LOGIN_PAGE,
  });
};
</script>

<template>
  <view class="register-container">
    <!-- 顶部 Logo 区域 -->
    <view class="logo-section">
<!--      <view class="brand-logo">FxAdmin</view>-->
      <image class="brand-logo" src="/static/logo.png" mode="aspectFit" />
      <view class="brand-subtitle">创建新账号</view>
    </view>

    <!-- 注册表单 -->
    <view class="register-section">
      <view class="form-group">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <u-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            border="surround"
            prefix-icon="account"
            clearable
            :custom-style="{
              backgroundColor: '#F8FAFC',
              height: '48px',
              borderRadius: '12px',
            }"
            :placeholder-style="{ color: '#94A3B8' }"
            :icon-style="{ color: '#64748B' }"
            @blur="() => (registerForm.username = registerForm.username.trim())"
          />
        </view>

        <view class="form-item">
          <text class="form-label">手机号</text>
          <u-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            type="number"
            maxlength="11"
            border="surround"
            prefix-icon="phone"
            clearable
            :custom-style="{
              backgroundColor: '#F8FAFC',
              height: '48px',
              borderRadius: '12px',
            }"
            :placeholder-style="{ color: '#94A3B8' }"
            :icon-style="{ color: '#64748B' }"
            @blur="() => (registerForm.phone = registerForm.phone.trim())"
          />
        </view>
      </view>

      <view class="form-group">
<!--        <view class="form-item">-->
<!--          <text class="form-label">验证码</text>-->
<!--          <view class="code-input-wrapper">-->
<!--            <u-input-->
<!--              v-model="registerForm.code"-->
<!--              placeholder="请输入验证码"-->
<!--              border="surround"-->
<!--              prefix-icon="key"-->
<!--              maxlength="6"-->
<!--              :custom-style="{-->
<!--                backgroundColor: '#F8FAFC',-->
<!--                height: '48px',-->
<!--                borderRadius: '12px',-->
<!--              }"-->
<!--              :placeholder-style="{ color: '#94A3B8' }"-->
<!--              :icon-style="{ color: '#64748B' }"-->
<!--              @blur="() => (registerForm.code = registerForm.code.trim())"-->
<!--            />-->
<!--            <u-button-->
<!--              :loading="sendCodeLoading"-->
<!--              :disabled="codeSent"-->
<!--              :text="codeSent ? `${countdown}s` : '发送验证码'"-->
<!--              type="primary"-->
<!--              shape="circle"-->
<!--              :custom-style="{-->
<!--                height: '48px',-->
<!--                minWidth: '120px',-->
<!--                fontSize: '14px',-->
<!--              }"-->
<!--              @click="handleSendCode"-->
<!--            >-->
<!--            </u-button>-->
<!--          </view>-->
<!--        </view>-->
      </view>

      <view class="form-group">
        <view class="form-item">
          <text class="form-label">密码</text>
          <u-input
            v-model="registerForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码（6-20个字符）"
            border="surround"
            prefix-icon="lock"
            :suffix-icon="showPassword ? 'eye' : 'eye-slash'"
            :suffix-icon-style="{ color: '#94A3B8' }"
            @suffix-icon-click="togglePassword"
            :custom-style="{
              backgroundColor: '#F8FAFC',
              height: '48px',
              borderRadius: '12px',
            }"
            :placeholder-style="{ color: '#94A3B8' }"
            :icon-style="{ color: '#64748B' }"
            @blur="() => (registerForm.password = registerForm.password.trim())"
          />
        </view>

        <view class="form-item">
          <text class="form-label">确认密码</text>
          <u-input
            v-model="registerForm.confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            placeholder="请再次输入密码"
            border="surround"
            prefix-icon="lock"
            :suffix-icon="showConfirmPassword ? 'eye' : 'eye-slash'"
            :suffix-icon-style="{ color: '#94A3B8' }"
            @suffix-icon-click="toggleConfirmPassword"
            :custom-style="{
              backgroundColor: '#F8FAFC',
              height: '48px',
              borderRadius: '12px',
              marginTop: '16px',
            }"
            :placeholder-style="{ color: '#94A3B8' }"
            :icon-style="{ color: '#64748B' }"
            @blur="
              () =>
                (registerForm.confirmPassword =
                  registerForm.confirmPassword.trim())
            "
          />
        </view>
      </view>

      <!-- 注册按钮 -->
      <view class="button-group">
        <u-button
          type="primary"
          :loading="loading"
          :loading-text="'注册中...'"
          shape="circle"
          :custom-style="{
            width: '100%',
            height: '48px',
            fontSize: '16px',
            fontWeight: '600',
            background: 'linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)',
            border: 'none',
            color: '#fff',
          }"
          @click="handleRegister"
        >
          注册
        </u-button>
      </view>
    </view>

    <!-- 登录提示 -->
    <view class="login-tip">
      <text class="tip-text">已有账号？</text>
      <text class="tip-link" @click="handleGoToLogin">立即登录</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  background: #ffffff;
  padding-top: var(--status-bar-height);
  padding-bottom: 40px;
  display: flex;
  flex-direction: column;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px 40px;
  flex-direction: column;
  gap: 12px;
}

.brand-logo {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -1px;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.2);
  flex-shrink: 0;
}

.brand-subtitle {
  font-size: 16px;
  color: #64748b;
  font-weight: 400;
}

.register-section {
  background: #f8fafc;
  margin: 0 24px 24px;
  border-radius: 20px;
  padding: 40px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  flex: 1;
}

.form-group {
  margin-bottom: 32px;
}

.form-group:last-of-type {
  margin-bottom: 0;
}

.form-item {
  margin-bottom: 16px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  display: block;
  margin-bottom: 8px;
}

.code-input-wrapper {
  display: flex;
  gap: 12px;
}

.code-input-wrapper u-input {
  flex: 1;
}

.code-input-wrapper u-button {
  flex-shrink: 0;
}

.button-group {
  margin-top: 32px;
}

.login-tip {
  text-align: center;
  padding: 0 24px 24px;
}

.tip-text {
  font-size: 14px;
  color: #64748b;
  margin-right: 4px;
}

.tip-link {
  font-size: 14px;
  color: #3b82f6;
  font-weight: 500;
}

.tip-link:active {
  opacity: 0.8;
}

@media (max-width: 480px) {
  .logo-section {
    padding: 48px 20px 32px;
    gap: 10px;
  }

  .brand-logo {
    width: 80px;
    height: 80px;
    font-size: 32px;
  }

  .brand-subtitle {
    font-size: 15px;
  }

  .register-section {
    margin: 0 16px 16px;
    padding: 32px 20px;
  }

  .form-group {
    margin-bottom: 24px;
  }

  .form-item {
    margin-bottom: 14px;
  }

  .code-input-wrapper {
    flex-direction: column;
    gap: 12px;
  }

  .code-input-wrapper u-button {
    width: 100%;
  }

  .button-group {
    margin-top: 28px;
  }
}
</style>
