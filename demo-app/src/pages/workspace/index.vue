<script lang="ts" setup>
definePage({
  style: {
    // "custom" 表示开启自定义导航栏，默认 "default"
    navigationStyle: "custom",
    navigationBarTitleText: "工作台",
  },
})

// 快捷功能列表
const quickActions = [
  {
    icon: "icon-file",
    title: "文件管理",
    path: "/pages/file/index",
    color: "#007AFF",
  },
  {
    icon: "icon-camera",
    title: "拍照上传",
    path: "/pages/camera/index",
    color: "#00D26A",
  },
  {
    icon: "icon-scan",
    title: "扫一扫",
    path: "/pages/scan/index",
    color: "#FFB800",
  },
  {
    icon: "icon-calendar",
    title: "日程安排",
    path: "/pages/schedule/index",
    color: "#6F42C1",
  },
  {
    icon: "icon-msg",
    title: "消息通知",
    path: "/pages/message/index",
    color: "#FA2A2D",
  },
  {
    icon: "icon-setting",
    title: "系统设置",
    path: "/pages/settings/index",
    color: "#999999",
  },
]

// 点击快捷功能
const handleQuickAction = (item: any) => {
  uni.navigateTo({
    url: item.path,
    fail: () => {
      uni.showToast({
        title: "页面开发中",
        icon: "none",
      })
    }
  })
}
</script>

<template>
  <view class="workspace-container">
    <!-- 统计卡片 -->
    <view class="stats-card">
      <view class="stats-item">
        <view class="stats-icon" style="background: #007AFF">
          <text class="icon-file"></text>
        </view>
        <view class="stats-info">
          <text class="stats-value">128</text>
          <text class="stats-label">文件总数</text>
        </view>
      </view>
      <view class="stats-item">
        <view class="stats-icon" style="background: #00D26A">
          <text class="icon-upload"></text>
        </view>
        <view class="stats-info">
          <text class="stats-value">56</text>
          <text class="stats-label">本周上传</text>
        </view>
      </view>
      <view class="stats-item">
        <view class="stats-icon" style="background: #FFB800">
          <text class="icon-download"></text>
        </view>
        <view class="stats-info">
          <text class="stats-value">34</text>
          <text class="stats-label">本周下载</text>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="section-title">快捷功能</view>
      <view class="action-grid">
        <view 
          v-for="(item, index) in quickActions" 
          :key="index"
          class="action-item"
          :style="{ borderColor: item.color }"
          @click="handleQuickAction(item)"
        >
          <view class="action-icon" :style="{ background: item.color }">
            <text :class="item.icon"></text>
          </view>
          <text class="action-title">{{ item.title }}</text>
        </view>
      </view>
    </view>


    <!-- 最近文件 -->
    <view class="recent-files">
      <view class="section-title">最近文件</view>
      <view class="file-list">
        <view class="file-item">
          <view class="file-icon icon-file-image"></view>
          <view class="file-info">
            <text class="file-name">产品文档.pdf</text>
            <text class="file-time">2024-01-15 14:30</text>
          </view>
          <view class="file-size">2.3MB</view>
        </view>
        <view class="file-item">
          <view class="file-icon icon-file-document"></view>
          <view class="file-info">
            <text class="file-name">会议记录.docx</text>
            <text class="file-time">2024-01-14 10:20</text>
          </view>
          <view class="file-size">1.5MB</view>
        </view>
        <view class="file-item">
          <view class="file-icon icon-file-video"></view>
          <view class="file-info">
            <text class="file-name">演示视频.mp4</text>
            <text class="file-time">2024-01-13 16:45</text>
          </view>
          <view class="file-size">45.8MB</view>
        </view>
      </view>
      <view class="more-btn">
        <text>查看更多</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.workspace-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: calc(var(--status-bar-height) + 12px);
  padding-bottom: var(--tab-bar-height);
}

.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: calc(var(--status-bar-height) + 44px);
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #e5e5e5;
  z-index: 100;
}

.navbar-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}

.stats-card {
  display: flex;
  justify-content: space-between;
  background: #ffffff;
  margin: 12px;
  padding: 20px 16px;
  border-radius: 12px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stats-icon text {
  font-size: 24px;
  color: #ffffff;
}

.stats-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stats-value {
  font-size: 28px;
  font-weight: 600;
  color: #333333;
}

.stats-label {
  font-size: 12px;
  color: #999999;
}

.quick-actions {
  background: #ffffff;
  margin: 12px;
  padding: 20px;
  border-radius: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 12px;
  border-radius: 12px;
  border: 2px solid #e5e5e5;
  transition: all 0.3s;
}

.action-item:active {
  background: #f0f0f0;
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.action-icon text {
  font-size: 28px;
  color: #ffffff;
}

.action-title {
  font-size: 14px;
  color: #333333;
}

.recent-files {
  background: #ffffff;
  margin: 12px;
  padding: 20px;
  border-radius: 12px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f8f8f8;
  border-radius: 8px;
}

.file-icon {
  font-size: 40px;
  margin-right: 12px;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-name {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

.file-time {
  font-size: 12px;
  color: #999999;
}

.file-size {
  font-size: 12px;
  color: #666666;
  margin-left: auto;
}

.more-btn {
  margin-top: 16px;
  padding: 12px;
  text-align: center;
  color: #007AFF;
  font-size: 14px;
}
</style>
