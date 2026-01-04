<script lang="ts" setup>
import { ref } from "vue";
import { useTokenStore } from "@/store/token";
import { useUserStore } from "@/store/user";

const tokenStore = useTokenStore();
const userStore = useUserStore();
const uToastRef = ref()

definePage({
  style: {
    navigationBarTitleText: '登录',
  },
})

// 登录表单数据
const loginForm = ref({
  username: 'admin',
  password: '123456',
});

// 是否显示密码
const showPassword = ref(false);

// 登录中状态
const loading = ref(false);

// 社交登录列表
const socialLogins = [
  {
    icon: "weixin-fill",
    name: "微信",
    color: "#07C160",
  },
  {
    icon: "github-circle-fill",
    name: "GitHub",
    color: "#24292E",
  },
  {
    icon: "qq-fill",
    name: "QQ",
    color: "#12B7F5",
  },
  {
    icon: "weibo-circle-fill",
    name: "微博",
    color: "#E6162D",
  },
];

// 切换密码显示
const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

// 账号密码登录
const handlePasswordLogin = async () => {
  if (!loginForm.value.username) {
    uToastRef.value?.show({
      title: '请输入用户名',
      type: 'error'
    })
    return;
  }
  if (!loginForm.value.password) {
    uToastRef.value?.show({
      title: '请输入密码',
      type: 'error',
    })
    return;
  }

  loading.value = true;
  try {
    // 调用登录接口
    await tokenStore.login({
      username: loginForm.value.username,
      password: loginForm.value.password,
    });
    uni.showToast({
      icon: 'none',
      title: '登录成功',
    });
    // 如果是注册界面返回个人信息界面
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2];
    console.log('2222',prevPage)
    if (prevPage && prevPage.route.includes('pages-fg/login/register')) {
      await uni.switchTab({
        url: '/pages/me/me'
      });
    } else {
      uni.navigateBack();
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false;
  }
};

// 社交登录
const handleSocialLogin = (item: any) => {
  uni.showModal({
    title: "提示",
    content: `${item.name} 登录功能开发中`,
    showCancelButton: false,
    confirmText: "我知道了",
  });
};

// 忘记密码
const handleForgotPassword = () => {
  uni.showModal({
    title: "提示",
    content: "忘记密码功能开发中",
    showCancelButton: false,
    confirmText: "我知道了",
  });
};

// 注册账号
const handleRegister = () => {
  uni.navigateTo({
    url: "/pages-fg/login/register",
  });
};
</script>

<template>
  <u-toast ref="uToastRef" />
  <view class="login-container">
    <!-- 顶部 Logo 区域 -->
    <view class="logo-section">
<!--      <view class="brand-logo">FxAdmin</view>-->
      <image class="brand-logo" src="/static/logo.png" mode="aspectFit" />

    </view>

    <!-- 账号密码登录 -->
    <view class="login-section">
      <view class="form-group">
        <u-input
          v-model="loginForm.username"
          placeholder="请输入用户名"
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
        />
        <u-input
          v-model="loginForm.password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="请输入密码"
          border="surround"
          prefix-icon="lock"
          :suffix-icon="showPassword ? 'eye' : 'eye-slash'"
          :suffix-icon-style="{ color: '#94A3B8' }"
          @suffix-icon-click="togglePassword"
          :custom-style="{
            backgroundColor: '#F8FAFC',
            height: '48px',
            borderRadius: '12px',
            marginTop: '16px',
          }"
          :placeholder-style="{ color: '#94A3B8' }"
          :icon-style="{ color: '#64748B' }"
        />
      </view>

      <!-- 登录按钮 -->
      <view class="button-group">
        <u-button
          type="primary"
          :loading="loading"
          :loading-text="'登录中...'"
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
          @click="handlePasswordLogin"
        >
          登录
        </u-button>
        <view class="forgot-link" @click="handleForgotPassword">
          忘记密码？
        </view>
      </view>
    </view>

    <!-- 第三方登录 -->
    <view class="social-section">
      <u-divider
        text="其他登录方式"
        :hair-line="false"
        :dashed="false"
        :custom-style="{ color: '#E2E8F0', margin: '24px 0' }"
      />
      <view class="social-grid">
        <view
          v-for="(item, index) in socialLogins"
          :key="index"
          class="social-item"
          @click="handleSocialLogin(item)"
        >
          <view class="social-icon">
          <u-icon :name="item.icon" :color="item.color"></u-icon>
          </view>
          <view class="social-name">{{ item.name }}</view>
        </view>
      </view>
    </view>

    <!-- 注册提示 -->
    <view class="register-tip">
      <text class="tip-text">还没有账号？</text>
      <text class="tip-link" @click="handleRegister">立即注册</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.login-container {
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
}

.brand-logo {
  width: 100px;
  height: 100px;
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

.login-section {
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

.button-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.forgot-link {
  text-align: center;
  color: #64748b;
  font-size: 14px;
  font-weight: 400;
}

.forgot-link:active {
  color: #3b82f6;
}

.social-section {
  background: #ffffff;
  margin: 0 24px 24px;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.social-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.social-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 12px;
  transition: all 0.25s ease;
}

.social-item:active {
  transform: scale(0.96);
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.social-icon text {
  font-size: 24px;
  color: #ffffff;
}

.social-name {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.register-tip {
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
  }

  .brand-logo {
    width: 80px;
    height: 80px;
    font-size: 32px;
  }

  .login-section {
    margin: 0 16px 16px;
    padding: 32px 20px;
  }

  .social-section {
    margin: 0 16px 16px;
    padding: 20px;
  }

  .social-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .social-item {
    padding: 14px 12px;
    gap: 10px;
  }

  .social-icon {
    width: 44px;
    height: 44px;
  }

  .social-icon text {
    font-size: 22px;
  }
}
</style>
