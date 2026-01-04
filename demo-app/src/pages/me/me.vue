<script lang="ts" setup>
import { storeToRefs } from "pinia"
import { LOGIN_PAGE } from "@/router/config"
import { useUserStore } from "@/store"
import { useTokenStore } from "@/store/token"

definePage({
  style: {
    navigationBarTitleText: "我的",
    navigationStyle: "custom",
  },
})

const userStore = useUserStore()
const tokenStore = useTokenStore()
const { userInfo } = storeToRefs(userStore)

// 功能菜单列表
const menuList = [
  {
    icon: "icon-file-text",
    title: "我的文件",
    path: "/pages/file/index",
  },
  {
    icon: "icon-clock",
    title: "操作记录",
    path: "/pages/record/index",
  },
  {
    icon: "icon-star",
    title: "我的收藏",
    path: "/pages/favorite/index",
  },
  {
    icon: "icon-share",
    title: "分享中心",
    path: "/pages/share/index",
  },
]

const settingsList = [
  {
    icon: "icon-setting",
    title: "系统设置",
    path: "/pages/settings/index",
  },
  {
    icon: "icon-info-circle",
    title: "关于我们",
    path: "/pages/about/index",
  },
  {
    icon: "icon-chat",
    title: "意见反馈",
    path: "/pages/feedback/index",
  },
  {
    icon: "icon-phone",
    title: "联系客服",
    path: "/pages/contact/index",
  },
]

// 微信小程序登录
async function handleLogin() {
  // #ifdef MP-WEIXIN
  // await tokenStore.login()
  // #endif
  // #ifndef MP-WEIXIN
  uni.navigateTo({
    url: `${LOGIN_PAGE}`,
  })
  // #endif
}

// 退出登录
async function handleLogout() {
  uni.showModal({
    title: "提示",
    content: "确定要退出登录吗？",
    success: async (res) => {
      if (res.confirm) {
        await tokenStore.logout()
        uni.showToast({
          title: "退出登录成功",
          icon: "success",
        })
      }
    },
  })
}

// 跳转页面
function navigateTo(path: string) {
  uni.navigateTo({
    url: path,
    fail: () => {
      uni.showToast({
        title: "页面开发中",
        icon: "none",
      })
    }
  })
}

// 编辑资料
function handleEditProfile() {
  if (!tokenStore.hasLogin) {
    handleLogin()
    return
  }
  navigateTo("/pages/profile/edit")
}
</script>

<template>
  <view class="me-container">


    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-avatar" @click="handleEditProfile">
        <image :src="userInfo.avatar" class="avatar-img" mode="aspectFill" />
      </view>
      <view class="user-info" @click="handleEditProfile">
        <view class="user-name">
          {{ tokenStore.hasLogin ? (userInfo.nickName || userInfo.userName || "用户") : "未登录" }}
        </view>
        <view class="user-desc" v-if="tokenStore.hasLogin">
          {{ userInfo.userName || "" }}
        </view>
        <view class="user-desc" v-else>
          登录后体验更多功能
        </view>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-row" v-if="tokenStore.hasLogin">
      <view class="stats-item">
        <view class="stats-value">128</view>
        <view class="stats-label">文件</view>
      </view>
      <view class="stats-divider"></view>
      <view class="stats-item">
        <view class="stats-value">56</view>
        <view class="stats-label">收藏</view>
      </view>
      <view class="stats-divider"></view>
      <view class="stats-item">
        <view class="stats-value">23</view>
        <view class="stats-label">分享</view>
      </view>
      <view class="stats-divider"></view>
      <view class="stats-item">
        <view class="stats-value">89</view>
        <view class="stats-label">访问</view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-title">常用功能</view>
      <view class="menu-grid">
        <view
          v-for="(item, index) in menuList"
          :key="index"
          class="menu-item"
          @click="navigateTo(item.path)"
        >
          <view class="menu-icon">
            <text :class="item.icon"></text>
          </view>
          <text class="menu-label">{{ item.title }}</text>
        </view>
      </view>
    </view>

    <!-- 设置菜单 -->
    <view class="settings-section">
      <view class="settings-list">
        <view
          v-for="(item, index) in settingsList"
          :key="index"
          class="settings-item"
          @click="navigateTo(item.path)"
        >
          <view class="settings-icon">
            <text :class="item.icon"></text>
          </view>
          <text class="settings-label">{{ item.title }}</text>
          <view class="settings-arrow icon-arrow-right"></view>
        </view>
      </view>
    </view>

    <!-- 登录/退出按钮 -->
    <view class="action-section">
      <view
        class="action-btn login-btn"
        v-if="!tokenStore.hasLogin"
        @click="handleLogin"
      >
        登录
      </view>
      <view
        class="action-btn logout-btn"
        v-else
        @click="handleLogout"
      >
        退出登录
      </view>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text class="version-text">版本 1.0.0</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.me-container {
  min-height: 100vh;
  background: #f5f5f5;

  padding-bottom: var(--tab-bar-height);
}


.navbar-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.user-card {
  margin: calc(var(--status-bar-height) + 12px) 12px 12px;
  background: linear-gradient(135deg, #2f61d2 0%, #134cbd 100%);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-avatar {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 3px solid rgba(255,255,255, 0.3);
  background: #ffffff;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name {
  font-size: 22px;
  font-weight: 600;
  color: #ffffff;
}

.user-desc {
  font-size: 14px;
  color: rgba(255,255,255, 0.8);
}

.stats-row {
  display: flex;
  justify-content: space-around;
  background: #ffffff;
  margin: 12px;
  padding: 24px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stats-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stats-value {
  font-size: 28px;
  font-weight: 600;
  color: #333333;
}

.stats-label {
  font-size: 14px;
  color: #999999;
}

.stats-divider {
  width: 1px;
  height: 40px;
  background: #e5e5e5;
}

.menu-section {
  background: #ffffff;
  margin: 12px;
  padding: 20px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 0;
}

.menu-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #2f61d2;
}

.menu-label {
  font-size: 14px;
  color: #333333;
}

.settings-section {
  background: #ffffff;
  margin: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.settings-list {
  display: flex;
  flex-direction: column;
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  gap: 12px;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #2f61d2;
  background: #f8f8f8;
}

.settings-label {
  flex: 1;
  font-size: 15px;
  color: #333333;
}

.settings-arrow {
  font-size: 16px;
  color: #cccccc;
}

.action-section {
  margin: 12px;
  padding: 0 12px;
}

.action-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
}

.login-btn {
  background: linear-gradient(135deg, #2f61d2 0%, #134cbd 100%);
  color: #ffffff;
}

.logout-btn {
  background: #ffffff;
  color: #ff4d4f;
  border: 1px solid #ff4d4f;
}

.action-btn:active {
  opacity: 0.8;
  transform: scale(0.98);
}

.version-info {
  margin-top: 24px;
  padding-bottom: 24px;
  text-align: center;
}

.version-text {
  font-size: 12px;
  color: #999999;
}
</style>
