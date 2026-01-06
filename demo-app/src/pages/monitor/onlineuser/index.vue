<template>
  <scroll-view
    class="online-users-container"
    scroll-y
    refresher-enabled
    :refresher-triggered="refreshing"
    @refresherrefresh="onRefresh"
    @scrolltolower="loadMore"
    lower-threshold="100"
  >
    <!-- 错误提示 -->
    <view v-if="error" class="error-banner">
      <text>{{ error }}</text>
    </view>
    <!-- 搜索和筛选 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          v-model="loginName"
          class="search-input"
          placeholder="搜索账户名"
          placeholder-class="search-placeholder"
          @input="fetchUsers()"
        />
        <text v-if="loginName" class="clear-icon" @click="clearSearch">✕</text>
      </view>
    </view>
    <!-- 用户列表 -->
    <view v-if="!loading || filteredUsers.length > 0" class="users-list">
      <view
        v-for="(user, index) in users"
        :key="user.sessionId"
        v-memo="[user.sessionId, user.sessionStatus]"
        class="user-card"

        @click="handleUserClick(user)"
      >
        <view class="user-avatar-wrapper">
          <view
            :class="['user-avatar-placeholder', { online: user.sessionStatus === 1, offline: user.sessionStatus !== 1 }]">
            <text class="avatar-text">{{ user.loginName.charAt(0).toUpperCase() }}</text>
          </view>
          <view
            :class="['status-indicator', { online: user.sessionStatus === 1, offline: user.sessionStatus !== 1 }]">
            <view v-if="user.sessionStatus === 1" class="pulse-ring"></view>
          </view>
        </view>
        <view class="user-info">
          <view class="user-name-row">
            <text class="user-name">{{ user.loginName }}</text>
            <text :class="['status-badge', { 'online-badge': user.sessionStatus === 1, 'offline-badge': user.sessionStatus !== 1 }]">
              {{ user.sessionStatus === 1 ? '在线' : '离线' }}
            </text>
          </view>
          <text class="user-email">{{ user.host }} - {{ user.loginLocation }}</text>
          <view class="user-meta">
            <text class="user-role">{{ user.browser }}</text>
            <text class="user-department">{{ user.os }}</text>
          </view>
          <text class="last-active">最后访问: {{ user.lastAccessTime }}</text>
        </view>
        <view class="user-arrow" @click.stop="onForceLogout(user)">
          <text class="arrow-icon">›</text>
        </view>
      </view>
    </view>
    <!-- 空状态 -->
    <view v-if="!loading && !loadingMore && filteredUsers.length === 0" class="empty-state">
      <text class="empty-icon">👥</text>
      <text class="empty-title">暂无在线用户</text>
      <text class="empty-desc">下拉刷新获取最新数据</text>
    </view>
    <!-- 加载状态 -->
    <view v-if="loading && filteredUsers.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>
    <!-- 加载更多 -->
    <view v-if="loadingMore" class="loading-more">
      <text class="loading-more-text">加载更多...</text>
    </view>
    <!-- 没有更多 -->
    <view v-if="!hasMore && filteredUsers.length > 0" class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </scroll-view>
  <!-- 详情弹窗 -->
  <u-popup
    v-model="showDetailModal"
    mode="center"
    :round="50"
    :title="'会话详情'"
    :closeOnClickOverlay="true"
    @close="closeDetailModal"
    width="85%"
    :safe-area-inset-bottom="true"
  >
    <view class="u-modal-content">
<!--        <view class="detail-avatar-wrapper">-->
<!--          <view :class="['detail-avatar', { online: selectedUser.sessionStatus === 1, offline: selectedUser.sessionStatus !== 1 }]">-->
<!--            <text class="detail-avatar-text">{{ selectedUser.loginName.charAt(0).toUpperCase() }}</text>-->
<!--          </view>-->
<!--        </view>-->
<!--        <view class="detail-user-name">{{ selectedUser.loginName }}</view>-->
<!--        <view class="detail-status">-->
<!--          <text :class="['detail-status-badge', { online: selectedUser.sessionStatus === 1, offline: selectedUser.sessionStatus !== 1 }]">-->
<!--            {{ selectedUser.sessionStatus === 1 ? '在线' : '离线' }}-->
<!--          </text>-->
<!--        </view>-->
<!--      </view>-->
      <view v-if="selectedUser" class="detail-items">
        <view class="detail-item">
          <text class="detail-label">会话ID</text>
          <text class="detail-value">{{ selectedUser.sessionId }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">IP地址</text>
          <text class="detail-value">{{ selectedUser.host }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">登录地点</text>
          <text class="detail-value">{{ selectedUser.loginLocation }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">浏览器</text>
          <text class="detail-value">{{ selectedUser.browser }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">操作系统</text>
          <text class="detail-value">{{ selectedUser.os }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">登录时间</text>
          <text class="detail-value">{{ selectedUser.loginTime }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">最后访问</text>
          <text class="detail-value">{{ selectedUser.lastAccessTime }}</text>
        </view>
      </view>
    </view>
  </u-popup>
</template>
<script lang="ts" setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { getSessionList, forceLogout } from '@/api/session'
import type { SessionInfo } from '@/api/session'
definePage({
  style: {
    navigationBarTitleText: '在线用户',
  },
  onPullDownRefresh: () => {
    onRefresh()
  },
})
// 状态管理
const loading = ref(false)
const showDetailModal = ref(false)
const selectedUser = ref<SessionInfo | null>(null)
const forcingLogout = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const error = ref<string | null>(null)
const users = ref<SessionInfo[]>([])
const loginName = ref('')
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const totalRecords = ref(0)


// 过滤后的用户列表（只显示在线用户）
const filteredUsers = computed(() => {
  return users.value;
})
// 监听users变化，调试用
watch(users, (newVal) => {
  console.log('users changed, length:', newVal.length, 'page:', page.value)
  console.log('hasMore:', hasMore.value, 'loadingMore:', loadingMore.value)
}, { deep: true })
// 获取用户列表
const fetchUsers = async (loadMore = false) => {
  if (loadMore) {
    loadingMore.value = true
    page.value += 1
  } else {
    loading.value = true
    page.value = 1
  }
  error.value = null
  try {
    const params = {
      pageIndex: page.value,
      pageSize: pageSize,
      loginName:loginName.value,
    }
    const res = await getSessionList(params)
    if (loadMore) {
      users.value = [...users.value, ...res.records]
    } else {
      users.value = res.records
    }
    totalRecords.value = res.total
    hasMore.value = (page.value * pageSize) < res.total
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}
// 下拉刷新
const onRefresh = async () => {
  refreshing.value = true
  await fetchUsers()
  setTimeout(() => {
    refreshing.value = false
    uni.stopPullDownRefresh()
  }, 500)
}
// 上拉加载更多
const loadMore = async () => {
  if (!hasMore.value || loadingMore.value) {
    return
  }
  page.value++
  await fetchUsers(true)
}
// 清除搜索
const clearSearch = () => {
  loginName.value = ''
  fetchUsers()
}

// 打开详情弹窗
const openDetailModal = (user: SessionInfo) => {
  selectedUser.value = user
  showDetailModal.value = true
}
// 关闭详情弹窗
const closeDetailModal = () => {
  showDetailModal.value = false
  selectedUser.value = null
}
// 在弹窗中强制退出
const onForceLogout = async (user:SessionInfo) => {
  console.log('onForceLogout', user)
  if (!user) return
  uni.showModal({
    title: '确认操作',
    content: `确定强制退出用户 ${user.loginName} 吗？`,
    success: async (res: any) => {
      if (res.confirm) {
        forcingLogout.value = true
        try {
          await forceLogout(user.sessionId)
          uni.showToast({
            title: '强制退出成功',
            icon: 'success',
          })
          fetchUsers()
        }  finally {
          forcingLogout.value = false
        }
      }
    },
  })
}
// 点击用户卡片，打开详情弹窗
const handleUserClick = (user: SessionInfo) => {
  console.log('clicked user:', user)
  openDetailModal(user)
}

// 页面加载
onMounted(() => {
  fetchUsers()
})
// 监听下拉刷新
onPullDownRefresh(() => {
  onRefresh()
})
</script>
<style lang="scss" scoped>
.online-users-container {
  height: 100vh;
  background: #f5f5f5;
  padding-top: calc(var(--status-bar-height) + 12px);
  padding-bottom: calc(var(--tab-bar-height) + 20px);
  box-sizing: border-box;
}
.error-banner {
  background: #ffe6e6;
  color: #d32f2f;
  padding: 12px 20px;
  font-size: 14px;
  text-align: center;
}
.search-bar {
  background: #ffffff;
  padding: 12px 16px;
}
.search-input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 12px;
}
.search-icon {
  font-size: 18px;
  margin-right: 8px;
}
.search-input {
  flex: 1;
  font-size: 14px;
  color: #333333;
}
.search-placeholder {
  color: #999999;
}
.clear-icon {
  font-size: 18px;
  color: #999999;
  margin-left: 8px;
  padding: 4px;
}


.users-list {
  padding: 0 16px;
  margin: 12px 0;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}
.user-card:active {
  transform: scale(0.98);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.user-card-offline {
  opacity: 0.7;
}
.user-avatar-wrapper {
  position: relative;
  width: 56px;
  height: 56px;
  flex-shrink: 0;
}
.user-avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 28px;
  background: #007AFF;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-text {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}
.user-avatar-placeholder.online {
  background: #22C55E;
}
.user-avatar-placeholder.offline {
  background: #94A3B8;
}
.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 28px;
  background: #f0f0f0;
}
.status-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 16px;
  height: 16px;
  border-radius: 8px;
  border: 2px solid #ffffff;
}
.status-indicator.online {
  background: #22C55E;
}
.status-indicator.offline {
  background: #94A3B8;
}
.pulse-ring {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  border: 2px solid #22C55E;
  animation: pulse 1.5s ease-out infinite;
}
@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}
.user-info {
  flex: 1;
  min-width: 0;
}
.user-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
}
.online-badge {
  padding: 2px 8px;
  background: #22C55E;
  color: #ffffff;
  font-size: 12px;
  border-radius: 8px;
}

.offline-badge {
  padding: 2px 8px;
  background: #94A3B8;
  color: #ffffff;
  font-size: 12px;
  border-radius: 8px;
}
.user-email {
  font-size: 13px;
  color: #666666;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.user-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.user-role,
.user-department {
  font-size: 12px;
  color: #999999;
  padding: 2px 6px;
  background: #f5f5f5;
  border-radius: 4px;
}
.last-active {
  font-size: 12px;
  color: #999999;
}
.user-actions {
  display: flex;
  gap: 8px;
}
.action-button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 18px;
  transition: all 0.2s;
}
.action-button:active {
  background: #e0e0e0;
  transform: scale(0.95);
}
.action-icon {
  font-size: 18px;
  color: #666666;
}
.empty-state,
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}
.empty-icon,
.loading-icon {
  font-size: 48px;
}
.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
}
.empty-desc,
.loading-text {
  font-size: 14px;
  color: #999999;
}
.loading-more,
.no-more {
  padding: 20px;
  text-align: center;
}
.loading-more-text,
.no-more-text {
  font-size: 14px;
  color: #999999;
}
// 详情弹窗样式
.detail-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}
.detail-avatar-wrapper {
  margin-bottom: 12px;
}
.detail-avatar {
  width: 80px;
  height: 80px;
  border-radius: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #007AFF;
}
.detail-avatar.online {
  background: #22C55E;
}
.detail-avatar.offline {
  background: #94A3B8;
}
.detail-avatar-text {
  font-size: 36px;
  font-weight: 600;
  color: #ffffff;
}
.detail-user-name {
  font-size: 20px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 8px;
}
.detail-status {
  margin-bottom: 4px;
}
.detail-status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}
.detail-status-badge.online {
  background: #22C55E;
  color: #ffffff;
}
.detail-status-badge.offline {
  background: #94A3B8;
  color: #ffffff;
}
.detail-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.detail-label {
  font-size: 13px;
  color: #999999;
}
.detail-value {
  font-size: 15px;
  color: #333333;
  font-weight: 500;
  word-break: break-all;
}
.user-action-hint {
  padding: 8px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-left: auto;
}
.hint-text {
  font-size: 12px;
  color: #999999;
}
// 详情弹窗样式
.detail-modal {
  width: 85%;
  max-width: 400px;
  max-height: 80vh;
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.detail-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}
.detail-modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}
.detail-modal-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
  transition: all 0.2s;
}
.detail-modal-close:active {
  background: #e0e0e0;
  transform: scale(0.95);
}
.detail-modal-content {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}
.detail-modal-button {
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.2s;
}
.detail-modal-button:active {
  transform: scale(0.98);
}
.detail-modal-button.danger {
  background: #FF3B30;
  color: #ffffff;
}
.detail-modal-button.danger:active {
  background: #D63027;
}
.detail-modal-button.disabled {
  background: #e0e0e0;
  color: #999999;
}
// 卡片箭头样式
.user-arrow {
  margin-left: auto;
  padding: 4px 8px;
  background: #f5f5f5;
  border-radius: 8px;
}
.arrow-icon {
  font-size: 18px;
  color: #999999;
  font-weight: bold;
}


// u-modal content 样式

.u-modal-content {

  padding: 20px;

}



.detail-info {

  display: flex;

  flex-direction: column;

  align-items: center;

  margin-bottom: 20px;

  padding-bottom: 20px;

  border-bottom: 1px solid #f0f0f0;

}



.detail-avatar-wrapper {

  margin-bottom: 12px;

}



.detail-avatar {

  width: 80px;

  height: 80px;

  border-radius: 40px;

  display: flex;

  align-items: center;

  justify-content: center;

  background: #007AFF;

}



.detail-avatar.online {

  background: #22C55E;

}



.detail-avatar.offline {

  background: #94A3B8;

}



.detail-avatar-text {

  font-size: 36px;

  font-weight: 600;

  color: #ffffff;

}



.detail-user-name {

  font-size: 20px;

  font-weight: 600;

  color: #333333;

  margin-bottom: 8px;

}



.detail-status {

  margin-bottom: 4px;

}



.detail-status-badge {

  padding: 4px 12px;

  border-radius: 12px;

  font-size: 14px;

  font-weight: 500;

}



.detail-status-badge.online {

  background: #22C55E;

  color: #ffffff;

}



.detail-status-badge.offline {

  background: #94A3B8;

  color: #ffffff;

}



.detail-items {

  display: flex;

  flex-direction: column;

  gap: 16px;

}



.detail-item {

  display: flex;

  flex-direction: column;

  gap: 4px;

}



.detail-label {

  font-size: 13px;

  color: #999999;

}



.detail-value {

  font-size: 15px;

  color: #333333;

  font-weight: 500;

  word-break: break-all;

}

</style>
