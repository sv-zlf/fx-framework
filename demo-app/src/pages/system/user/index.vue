<template>
  <scroll-view
    class="user-container"
    scroll-y
    refresher-enabled
    :refresher-triggered="refreshing"
    @refresherrefresh="onRefresh"
    @scrolltolower="loadMore"
    lower-threshold="100"
  >
    <!-- 搜索和筛选 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">👤</text>
        <input
          v-model="searchForm.name"
          class="search-input"
          placeholder="搜索用户名称"
          placeholder-class="search-placeholder"
          @input="fetchUsers"
        />
        <text v-if="searchForm.name" class="clear-icon" @click="clearSearch">✕</text>
      </view>
      <view class="filter-group">
        <view class="search-input-wrapper">
          <text class="search-icon">📱</text>
          <input
            v-model="searchForm.phone"
            class="search-input"
            placeholder="搜索手机号"
            placeholder-class="search-placeholder"
            @input="fetchUsers"
          />
          <text v-if="searchForm.phone" class="clear-icon" @click="clearPhone">✕</text>
        </view>
        <picker mode="selector" :range="statusOptions" range-key="label" @change="onStatusChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedStatusLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>


    <!-- 用户列表 -->
    <view v-if="!loading || userList.length > 0" class="user-list">
      <view
        v-for="user in userList"
        :key="user.id"
        class="user-card"
      >
        <!-- 用户头部 -->
        <view class="user-header">
          <view class="user-avatar">
            <text class="avatar-text">{{ user.nickName.charAt(0).toUpperCase() }}</text>
          </view>
          <view class="user-info">
            <text class="user-name">{{ user.nickName }}</text>
            <text class="user-username">@{{ user.userName }}</text>
          </view>
          <view :class="['status-badge', { 'status-active': user.status === 1, 'status-inactive': user.status === 0 }]">
            {{ user.status === 1 ? '启用' : '禁用' }}
          </view>
        </view>

        <!-- 用户详情 -->
        <view class="user-details">
          <view class="detail-item">
            <text class="detail-label">邮箱</text>
            <text class="detail-value">{{ user.email || '-' }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">手机号</text>
            <text class="detail-value">{{ user.phone || '-' }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">创建时间</text>
            <text class="detail-value">{{ user.createTime || '-' }}</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="user-actions">
          <button
            class="action-btn btn-edit"
            @click="onEdit(user)"
          >
            <text class="btn-icon">✎</text>
            <text class="btn-text">编辑</text>
          </button>
          <button
            class="action-btn btn-delete"
            :disabled="user.admin"
            @click="onDelete(user)"
          >
            <text class="btn-icon">🗑</text>
            <text class="btn-text">删除</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && !loadingMore && userList.length === 0" class="empty-state">
      <text class="empty-icon">👥</text>
      <text class="empty-title">暂无用户</text>
      <text class="empty-desc">点击新增按钮添加第一个用户</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && userList.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 加载更多 -->
    <view v-if="loadingMore" class="loading-more">
      <text class="loading-more-text">加载更多...</text>
    </view>

    <!-- 没有更多 -->
    <view v-if="!hasMore && userList.length > 0" class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </scroll-view>


  <!-- 新增用户按钮 -->
  <view class="add-btn" @click="onAdd">
    <text class="add-icon">+</text>
  </view>

  <!-- 批量操作栏 -->
  <view v-if="selectedUsers.length > 0" class="batch-actions">
    <text class="batch-text">已选择 {{ selectedUsers.length }} 项</text>
    <button class="batch-btn" @click="onDeleteBatch">
      <text class="batch-btn-text">批量删除</text>
    </button>
  </view>

  <!-- 新增/编辑用户弹窗 -->
  <u-popup
    v-model="showModal"
    mode="bottom"
    :round="20"
    :title="modalTitle"
    :closeOnClickOverlay="true"
    @close="closeModal"
    width="100%"
    :safe-area-inset-bottom="true"
  >
    <scroll-view scroll-y class="modal-content">
      <view class="form-group">
        <text class="form-label">用户名称 *</text>
        <input
          v-model="form.userName"
          class="form-input"
          placeholder="请输入用户名称"
        />
      </view>
      <view class="form-group">
        <text class="form-label">昵称 *</text>
        <input
          v-model="form.nickName"
          class="form-input"
          placeholder="请输入昵称"
        />
      </view>
      <view class="form-group">
        <text class="form-label">手机号码</text>
        <input
          v-model="form.phone"
          class="form-input"
          placeholder="请输入手机号码"
          type="number"
        />
      </view>
      <view class="form-group">
        <text class="form-label">邮箱</text>
        <input
          v-model="form.email"
          class="form-input"
          placeholder="请输入邮箱"
          type="email"
        />
      </view>
      <view class="form-group">
        <text class="form-label">角色 *</text>
        <picker mode="selector" :range="roleList" range-key="roleName" @change="onRoleChange">
          <view class="form-picker">
            <text class="form-picker-text">{{ selectedRoleName || '请选择角色' }}</text>
            <text class="form-picker-arrow">▼</text>
          </view>
        </picker>
      </view>
      <view class="form-group">
        <text class="form-label">状态</text>
        <radio-group @change="onStatusRadioChange">
          <label class="radio-item">
            <radio :value="1" :checked="form.status === 1" color="#3B82F6" />
            <text class="radio-label">启用</text>
          </label>
          <label class="radio-item">
            <radio :value="0" :checked="form.status === 0" color="#3B82F6" />
            <text class="radio-label">禁用</text>
          </label>
        </radio-group>
      </view>
    </scroll-view>
    <view class="modal-footer">
      <button class="modal-btn modal-btn-cancel" @click="closeModal">取消</button>
      <button class="modal-btn modal-btn-confirm" @click="handleOk">确定</button>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  getUserList,
  saveOrUpdate,
  deleteUser,
  deleteBatch,
  getRoleList,
  type UserInfo,
  type UserListParams,
  type RoleInfo
} from '@/api/system/user'

defineOptions({ name: 'user-list' })

// 搜索和筛选
const searchForm = reactive<UserListParams>({
  name: '',
  phone: '',
  status: undefined
})

const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const selectedStatusLabel = ref('全部状态')

// 数据状态
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const userList = ref<UserInfo[]>([])
const hasMore = ref(true)
const pagination = reactive({
  pageIndex: 1,
  pageSize: 10,
  total: 0
})

// 选中用户
const selectedUsers = ref<number[]>([])

// 表单弹窗
const showModal = ref(false)
const modalTitle = ref('')
const formType = ref(0) // 0-新增, 1-编辑
const form = reactive<Partial<UserInfo>>({
  userName: '',
  nickName: '',
  phone: '',
  email: '',
  roles: [],
  status: 1
})

// 角色列表
const roleList = ref<RoleInfo[]>([])
const selectedRoleName = ref('')

// 获取用户列表
const fetchUsers = async (isRefresh = false) => {
  if (isRefresh) {
    refreshing.value = true
  }
  loading.value = true

  try {
    const params = {
      ...searchForm,
      pageIndex: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    const res = await getUserList(params)
    
    if (isRefresh) {
      userList.value = res.records || []
    } else {
      userList.value.push(...(res.records || []))
    }
    
    pagination.total = res.total
    hasMore.value = userList.value.length < pagination.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pagination.pageIndex = 1
  selectedUsers.value = []
  fetchUsers(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true
  pagination.pageIndex++
  fetchUsers(false).finally(() => {
    loadingMore.value = false
  })
}

// 清空搜索
const clearSearch = () => {
  searchForm.name = ''
  pagination.pageIndex = 1
  fetchUsers(true)
}

const clearPhone = () => {
  searchForm.phone = ''
  pagination.pageIndex = 1
  fetchUsers(true)
}

// 状态筛选
const onStatusChange = (e: any) => {
  const index = e.detail.value
  selectedStatusLabel.value = statusOptions[index].label
  searchForm.status = statusOptions[index].value
  pagination.pageIndex = 1
  fetchUsers(true)
}

// 新增用户
const onAdd = () => {
  modalTitle.value = '新增用户'
  formType.value = 0
  Object.assign(form, {
    userName: '',
    nickName: '',
    phone: '',
    email: '',
    roles: [],
    status: 1
  })
  selectedRoleName.value = ''
  showModal.value = true
}

// 编辑用户
const onEdit = (user: UserInfo) => {
  modalTitle.value = '编辑用户'
  formType.value = 1
  Object.assign(form, { ...user })
  
  // 查找角色名称
  if (user.roles && user.roles.length > 0) {
    const role = roleList.value.find(r => r.id === user.roles[0])
    selectedRoleName.value = role?.roleName || ''
  }
  
  showModal.value = true
}

// 关闭弹窗
const closeModal = () => {
  showModal.value = false
}

// 角色选择
const onRoleChange = (e: any) => {
  const index = e.detail.value
  const role = roleList.value[index]
  form.roles = [role.id]
  selectedRoleName.value = role.roleName
}

// 状态选择
const onStatusRadioChange = (e: any) => {
  form.status = e.detail.value
}

// 保存用户
const handleOk = async () => {
  if (!form.userName) {
    uni.showToast({ title: '请输入用户名称', icon: 'none' })
    return
  }
  if (!form.nickName) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  if (!form.roles || form.roles.length === 0) {
    uni.showToast({ title: '请选择角色', icon: 'none' })
    return
  }

  try {
    await saveOrUpdate(form)
    uni.showToast({ title: '保存成功', icon: 'success' })
    showModal.value = false
    fetchUsers(true)
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

// 删除用户
const onDelete = (user: UserInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除用户「${user.nickName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteUser(user.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchUsers(true)
        } catch (error) {
          console.error('删除用户失败:', error)
        }
      }
    }
  })
}

// 批量删除
const onDeleteBatch = () => {
  if (selectedUsers.value.length === 0) {
    uni.showToast({ title: '请选择要删除的用户', icon: 'none' })
    return
  }
  
  uni.showModal({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteBatch(selectedUsers.value)
          uni.showToast({ title: '删除成功', icon: 'success' })
          selectedUsers.value = []
          fetchUsers(true)
        } catch (error) {
          console.error('批量删除失败:', error)
        }
      }
    }
  })
}

// 获取角色列表
const fetchRoles = async () => {
  try {
    const res = await getRoleList()
    roleList.value = res || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 初始化
fetchUsers(true)
fetchRoles()
</script>

<style lang="scss" scoped>
.user-container {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 100px;
}

// 搜索栏
.search-bar {
  background: #ffffff;
  padding: 12px 16px;
  border-bottom: 1px solid #E2E8F0;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: #F1F5F9;
  border-radius: 8px;
  padding: 10px 12px;
  margin-bottom: 12px;
}

.search-icon {
  font-size: 16px;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  font-size: 14px;
}

.search-placeholder {
  color: #94A3B8;
}

.clear-icon {
  font-size: 16px;
  color: #94A3B8;
  padding: 4px;
}

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-picker {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #F1F5F9;
  padding: 10px 12px;
  border-radius: 8px;
}

.filter-text {
  font-size: 14px;
  color: #475569;
}

.filter-arrow {
  font-size: 10px;
  color: #94A3B8;
}

// 用户列表
.user-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
}

.user-username {
  font-size: 13px;
  color: #64748B;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status-active {
  background: #DCFCE7;
  color: #16A34A;
}

.status-inactive {
  background: #FEE2E2;
  color: #DC2626;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #94A3B8;
}

.detail-value {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

// 操作按钮
.user-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1;
  min-width: 70px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: none;
  font-size: 13px;
  gap: 4px;
}

.btn-icon {
  font-size: 14px;
}

.btn-text {
  font-size: 13px;
}

.btn-edit {
  background: #3B82F6;
  color: #ffffff;
}

.btn-delete {
  background: #EF4444;
  color: #ffffff;
}

.btn-delete:disabled {
  background: #E2E8F0;
  color: #94A3B8;
}

// 新增按钮
.add-btn {
  position: fixed;
  right: 24px;
  bottom: 100px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  z-index: 100;
}

.add-icon {
  font-size: 28px;
  color: #ffffff;
  font-weight: 300;
}

// 批量操作栏
.batch-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 12px 16px;
  border-top: 1px solid #E2E8F0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.batch-text {
  font-size: 14px;
  color: #64748B;
}

.batch-btn {
  padding: 8px 16px;
  background: #EF4444;
  color: #ffffff;
  border-radius: 8px;
  border: none;
  font-size: 14px;
}

.batch-btn-text {
  font-size: 14px;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 14px;
  color: #94A3B8;
}

// 加载状态
.loading-state,
.loading-more,
.no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.loading-icon {
  font-size: 24px;
  margin-right: 8px;
}

.loading-text {
  font-size: 14px;
  color: #94A3B8;
}

.loading-more-text,
.no-more-text {
  font-size: 14px;
  color: #94A3B8;
}

// 弹窗样式
.modal-content {
  max-height: 60vh;
  padding: 16px 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 14px;
  background: #F8FAFC;
}

.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  background: #F8FAFC;
}

.form-picker-text {
  font-size: 14px;
  color: #334155;
}

.form-picker-arrow {
  font-size: 10px;
  color: #94A3B8;
}

.radio-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.radio-label {
  margin-left: 8px;
  font-size: 14px;
  color: #334155;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #E2E8F0;
}

.modal-btn {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  border: none;
  font-size: 16px;
  font-weight: 500;
}

.modal-btn-cancel {
  background: #F1F5F9;
  color: #475569;
}

.modal-btn-confirm {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  color: #ffffff;
}
</style>
