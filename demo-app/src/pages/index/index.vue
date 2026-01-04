<script lang="ts" setup>
definePage({
  type: "home",
  style: {
    navigationStyle: "custom",
    navigationBarTitleText: "首页",
  },
});

// 项目核心功能
const features = [
  {
    icon: "icon-file",
    title: "文件管理",
    desc: "安全高效的文件存储与共享",
    color: "#3B82F6",
  },
  {
    icon: "icon-calendar",
    title: "日程管理",
    desc: "个人日程安排与提醒",
    color: "#F97316",
  },
  {
    icon: "icon-star",
    title: "收藏管理",
    desc: "管理常用收藏内容",
    color: "#10B981",
  },
  {
    icon: "icon-msg",
    title: "消息通知",
    desc: "实时接收系统重要消息",
    color: "#8B5CF6",
  },
];

// 快捷入口
const quickLinks = [
  {
    title: "工作台",
    path: "/pages/workspace/index",
    color: "linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)",
  },
  {
    title: "系统菜单",
    path: "/pages/menu/index",
    color: "linear-gradient(135deg, #F97316 0%, #EA580C 100%)",
  },
  {
    title: "个人中心",
    path: "/pages/me/me",
    color: "linear-gradient(135deg, #10B981 0%, #059669 100%)",
  },
  {
    title: "关于系统",
    path: "/pages/about/index",
    color: "linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)",
  },
];

// 点击快捷入口
const handleQuickLink = (item: any) => {
  uni.switchTab({
    url: item.path,
  });
};
</script>

<template>
  <view class="home-container">
    <!-- 顶部卡片 -->
    <view class="top-card">
      <image src="/static/logo.svg" class="brand-logo" mode="aspectFill" />
      <view class="brand-info">
        <view class="brand-title">FxAdmin</view>
        <view class="brand-subtitle">移动端文件管理系统</view>
        <view class="brand-desc">基于 UniApp + Vue 3 构建</view>
      </view>
    </view>

    <!-- 项目介绍 -->
    <view class="intro-section">
      <view class="section-header">
        <text class="section-title">项目介绍</text>
        <text class="section-subtitle">一站式移动端解决方案</text>
      </view>
      <view class="intro-content">
        <view class="intro-text">
          <text class="text-label">FxAdmin 移动端</text>
          <text class="text-content">
            是 FxAdmin
            全栈框架的移动端应用，提供文件管理、日程安排、消息通知等核心功能，支持与后端管理系统无缝集成，实现移动办公高效化。</text
          >
        </view>
        <view class="intro-highlights">
          <view class="highlight-item">
            <text class="highlight-dot">•</text>
            <text class="highlight-text">对接后端 Spring Boot API</text>
          </view>
          <view class="highlight-item">
            <text class="highlight-dot">•</text>
            <text class="highlight-text">支持文件上传下载管理</text>
          </view>
          <view class="highlight-item">
            <text class="highlight-dot">•</text>
            <text class="highlight-text">实时消息推送通知</text>
          </view>
          <view class="highlight-item">
            <text class="highlight-dot">•</text>
            <text class="highlight-text">个人日程与收藏管理</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 核心功能 -->
    <view class="features-section">
      <view class="section-header">
        <text class="section-title">核心功能</text>
        <text class="section-subtitle">移动端核心功能模块</text>
      </view>
      <view class="features-grid">
        <view
          v-for="(item, index) in features"
          :key="index"
          class="feature-card"
        >
          <view class="feature-icon-wrapper">
            <view
              class="feature-icon"
              :style="{ background: item.color }"
              :class="item.icon"
            ></view>
          </view>
          <view class="feature-content">
            <view class="feature-title">{{ item.title }}</view>
            <view class="feature-desc">{{ item.desc }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="links-section">
      <view class="section-header">
        <text class="section-title">快捷入口</text>
        <text class="section-subtitle">快速访问常用功能</text>
      </view>
      <view class="links-grid">
        <view
          v-for="(item, index) in quickLinks"
          :key="index"
          class="link-card"
          :style="{ background: item.color }"
          @click="handleQuickLink(item)"
        >
          <view class="link-icon">
            <text class="link-icon-text">{{ item.title.charAt(0) }}</text>
          </view>
          <view class="link-content">
            <text class="link-title">{{ item.title }}</text>
            <text class="link-arrow">→</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 版本信息 -->
    <view class="version-section">
      <view class="version-card">
        <view class="version-content">
          <view class="version-text">
            <text class="version-label">当前版本</text>
            <text class="version-number">v1.0.0</text>
          </view>
          <view class="version-info">
            <text class="version-desc">支持管理后台 · 文件 · 日程</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: #f8fafc;
  padding-top: var(--status-bar-height);
  padding-bottom: calc(var(--tab-bar-height) + 40px);
}

.top-card {
  margin: 16px;
  padding: 40px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow:
    0 8px 32px rgba(37, 99, 235, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1);
}

.brand-logo {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  padding: 12px;
  flex-shrink: 0;
}

.brand-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
}

.brand-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

.intro-section {
  margin: 0 16px 24px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  display: block;
  margin-bottom: 4px;
}

.section-subtitle {
  font-size: 14px;
  color: #64748b;
}

.intro-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.intro-text {
  line-height: 1.8;
}

.text-label {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.text-content {
  font-size: 14px;
  color: #64748b;
}

.intro-highlights {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.highlight-dot {
  font-size: 16px;
  color: #3b82f6;
  font-weight: bold;
}

.highlight-text {
  font-size: 13px;
  color: #475569;
}

.features-section {
  margin: 0 16px 24px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.25s ease;
}

.feature-card:active {
  transform: scale(0.98);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.feature-icon-wrapper {
  flex-shrink: 0;
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #ffffff;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.1);
}

.feature-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.feature-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

.links-section {
  margin: 0 16px 24px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.link-card {
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.25s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.link-card:active {
  transform: scale(0.96);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.link-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.link-icon-text {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
}

.link-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.link-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}

.link-arrow {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: bold;
}

.version-section {
  margin: 0 16px 24px;
}

.version-card {
  padding: 24px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: 16px;
  border: 1px solid #334155;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.version-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.version-label {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
}

.version-number {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 1px;
}

.version-info {
  text-align: right;
}

.version-desc {
  font-size: 14px;
  color: #94a3b8;
}

@media (max-width: 480px) {
  .top-card {
    padding: 32px 20px;
    margin: 12px;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-subtitle {
    font-size: 16px;
  }

  .intro-section,
  .features-section,
  .links-section {
    margin: 0 12px 20px;
    padding: 20px;
  }

  .intro-highlights {
    grid-template-columns: 1fr;
  }

  .features-grid,
  .links-grid {
    gap: 12px;
  }

  .feature-card {
    padding: 14px;
  }

  .feature-icon {
    width: 44px;
    height: 44px;
  }
}
</style>
