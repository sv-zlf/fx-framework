<script lang="ts" setup>
import { HOME_PAGE } from "@/utils";

definePage({
  style: {
    navigationBarTitleText: '',
  },
})

const handleGoBack = () => {
  uni.switchTab({
    url: HOME_PAGE,
  });
};

const handleRefresh = () => {
  uni.switchTab({
    url: HOME_PAGE,
  });
};
</script>

<template>
  <view class="not-found-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration bg-decoration-1"></view>
    <view class="bg-decoration bg-decoration-2"></view>
    <view class="bg-decoration bg-decoration-3"></view>
    <view class="bg-decoration bg-decoration-4"></view>

    <!-- 主内容 -->
    <view class="content-wrapper">
      <!-- 品牌 Logo -->
<!--      <view class="brand-logo">FxAdmin</view>-->

      <!-- 插图 -->
      <view class="illustration-wrapper">
        <view class="illustration-circle">
          <view class="illustration-icon">
            <text class="icon-404">404</text>
          </view>
        </view>
      </view>

      <!-- 提示信息 -->
      <view class="message-section">
        <view class="error-title">页面不存在</view>
        <view class="error-desc">
          <text>抱歉，您访问的页面</text>
          <text>无法找到或已移除</text>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-section">
        <view class="action-buttons">
          <u-button
            type="primary"
            shape="circle"
            size="large"
            :custom-style="{
              width: '100%',
              height: '48px',
              fontSize: '16px',
              fontWeight: '600',
              background: 'linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)',
              border: 'none',
              color: '#fff',
            }"
            @click="handleGoBack"
          >
            返回首页
          </u-button>
          <u-button
            shape="circle"
            size="large"
            :custom-style="{
              width: '100%',
              height: '48px',
              fontSize: '16px',
              fontWeight: '600',
              background: '#F8FAFC',
              color: '#3B82F6',
              border: '1px solid #E2E8F0',
            }"
            @click="handleRefresh"
          >
            刷新页面
          </u-button>
        </view>
      </view>

      <!-- 底部提示 -->
      <view class="tip-section">
        <view class="tip-item">
          <text class="tip-icon">🏠</text>
          <text class="tip-text">回到首页</text>
        </view>
        <view class="tip-divider"></view>
        <view class="tip-item">
          <text class="tip-icon">🔄</text>
          <text class="tip-text">尝试刷新</text>
        </view>
        <view class="tip-divider"></view>
        <view class="tip-item">
          <text class="tip-icon">📞</text>
          <text class="tip-text">联系客服</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.not-found-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  padding-top: var(--status-bar-height);
  padding-bottom: 40px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 背景装饰
.bg-decoration {
  position: absolute;
  border-radius: 50%;
  opacity: 0.05;
  animation: float 20s ease-in-out infinite;
}

.bg-decoration-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.bg-decoration-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.bg-decoration-3 {
  width: 250px;
  height: 250px;
  top: 20%;
  right: -80px;
  animation-delay: 10s;
}

.bg-decoration-4 {
  width: 180px;
  height: 180px;
  bottom: 30%;
  left: -60px;
  animation-delay: 15s;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, -20px);
  }
}

// 内容区域
.content-wrapper {
  background: #ffffff;
  border-radius: 24px;
  padding: 48px 32px;
  max-width: 400px;
  width: 90%;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 10;
}

.brand-logo {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 20px;
  margin: 0 auto 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -1px;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.25);
}

.illustration-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 32px;
}

.illustration-circle {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.5);
}

.illustration-icon {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: 2px;
}

.icon-404 {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

// 提示信息
.message-section {
  text-align: center;
  margin-bottom: 32px;
}

.error-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.error-desc {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

// 操作按钮
.action-section {
  margin-bottom: 32px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

// 底部提示
.tip-section {
  display: flex;
  align-items: center;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

.tip-icon {
  font-size: 16px;
}

.tip-text {
  font-weight: 500;
}

.tip-divider {
  width: 1px;
  height: 16px;
  background: #e2e8f0;
}

@media (max-width: 480px) {
  .not-found-container {
    padding-bottom: 32px;
  }

  .content-wrapper {
    padding: 32px 24px;
    max-width: 360px;
  }

  .brand-logo {
    width: 80px;
    height: 80px;
    font-size: 28px;
    margin: 0 auto 24px;
  }

  .illustration-circle {
    width: 100px;
    height: 100px;
  }

  .illustration-icon {
    font-size: 28px;
  }

  .error-title {
    font-size: 20px;
  }

  .error-desc {
    font-size: 13px;
  }

  .tip-section {
    flex-wrap: wrap;
    justify-content: center;
    gap: 16px;
  }

  .tip-divider {
    display: none;
  }

  .tip-item {
    width: 100%;
    justify-content: center;
    padding: 8px 0;
  }
}
</style>
