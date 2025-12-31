<script lang="ts" setup>
definePage({
  type: "home",
  style: {
    navigationStyle: "custom",
    navigationBarTitleText: "首页",
  },
})

// 项目特色
const features = [
  {
    icon: "icon-file",
    title: "文件管理",
    desc: "安全高效的文件存储与共享",
  },
  {
    icon: "icon-setting",
    title: "系统设置",
    desc: "灵活可配的权限管理系统",
  },
  {
    icon: "icon-msg",
    title: "消息通知",
    desc: "实时推送的智能消息服务",
  },
  {
    icon: "icon-scan",
    title: "扫一扫",
    desc: "便捷快速的扫码识别功能",
  },
]

// 技术栈
const techStack = [
  { name: "UniApp", color: "#03A9F4" },
  { name: "Vue3", color: "#42B883" },
  { name: "TypeScript", color: "#3178C6" },
  { name: "Vite", color: "#646CFF" },
  { name: "Pinia", color: "#FFD700" },
]

// 系统信息
const sysInfo = [
  { label: "系统版本", value: "v1.0.0" },
  { label: "技术支持", value: "FX 团队" },
  { label: "联系方式", value: "400-123-4567" },
]

// 快捷入口
const quickLinks = [
  { title: "工作台", path: "/pages/workspace/index", color: "#2f61d2" },
  { title: "个人中心", path: "/pages/me/me", color: "#134cbd" },
]

// 点击快捷入口
const handleQuickLink = (item: any) => {
  uni.switchTab({
    url: item.path,
  })
}
</script>

<template>
  <view class="home-container">
    <!-- 顶部卡片 -->
    <view class="top-card">
      <image src="/static/logo.svg" class="brand-logo" mode="aspectFill" />
      <view class="brand-info">
        <view class="brand-title">FX 文件管理系统</view>
        <view class="brand-subtitle">安全 · 高效 · 智能的文件管理平台</view>
      </view>
    </view>

    <!-- 项目介绍 -->
    <view class="intro-section">
      <view class="section-title">
        <text class="title-icon">✨</text>
        <text class="title-text">项目特色</text>
      </view>
      <view class="features-grid">
        <view v-for="(item, index) in features" :key="index" class="feature-card">
          <view class="feature-icon" :class="item.icon"></view>
          <view class="feature-content">
            <view class="feature-title">{{ item.title }}</view>
            <view class="feature-desc">{{ item.desc }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 技术栈 -->
    <view class="tech-section">
      <view class="section-title">
        <text class="title-icon">⚡</text>
        <text class="title-text">技术栈</text>
      </view>
      <view class="tech-list">
        <view v-for="(item, index) in techStack" :key="index" class="tech-item" :style="{ background: item.color }">
          <text class="tech-name">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="links-section">
      <view class="section-title">
        <text class="title-icon">🚀</text>
        <text class="title-text">快捷入口</text>
      </view>
      <view class="links-grid">
        <view 
          v-for="(item, index) in quickLinks" 
          :key="index"
          class="link-card"
          :style="{ background: item.color }"
          @click="handleQuickLink(item)"
        >
          <text class="link-title">{{ item.title }}</text>
          <text class="link-arrow">→</text>
        </view>
      </view>
    </view>

    <!-- 系统信息 -->
    <view class="info-section">
      <view class="section-title">
        <text class="title-icon">ℹ️</text>
        <text class="title-text">系统信息</text>
      </view>
      <view class="info-list">
        <view v-for="(item, index) in sysInfo" :key="index" class="info-item">
          <text class="info-label">{{ item.label }}：</text>
          <text class="info-value">{{ item.value }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  padding-top: var(--status-bar-height);
  padding-bottom: var(--tab-bar-height);
}

.top-card {
  margin: 12px;
  padding: 32px 24px;
  background: linear-gradient(135deg, #2f61d2 0%, #134cbd 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(47, 97, 210, 0.3);
}

.brand-logo {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
}

.brand-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.brand-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 1px;
}

.brand-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
}

.intro-section {
  margin: 0 12px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20px;
}

.title-icon {
  font-size: 22px;
}

.title-text {
  flex: 1;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: #2f61d2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #ffffff;
}

.feature-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feature-title {
  font-size: 17px;
  font-weight: 600;
  color: #333333;
}

.feature-desc {
  font-size: 14px;
  color: #666666;
  line-height: 1.5;
}

.tech-section {
  margin: 12px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tech-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.tech-item {
  padding: 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tech-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.links-section {
  margin: 12px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  justify-content: space-between;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.link-card:active {
  transform: scale(0.95);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.link-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}

.link-arrow {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: bold;
}

.info-section {
  margin: 12px;
  padding: 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-label {
  font-size: 15px;
  color: #666666;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
}
</style>
