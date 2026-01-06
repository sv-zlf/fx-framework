<template>
  <scroll-view
    class="role-container"
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
        <text class="search-icon">🔖</text>
        <input
          v-model="searchForm.roleName"
          class="search-input"
          placeholder="搜索角色名称"
          placeholder-class="search-placeholder"
          @input="fetchRoles"
        />
        <text v-if="searchForm.roleName" class="clear-icon" @click="clearSearch">✕</text>
      </view>
      <view class="filter-group">
        <view class="search-input-wrapper">
          <text class="search-icon">#️⃣</text>
          <input
            v-model="searchForm.roleCode"
            class="search-input"
            placeholder="搜索角色编码"
            placeholder-class="search-placeholder"
            @input="fetchRoles"
          />
          <text v-if="searchForm.roleCode" class="clear-icon" @click="clearCode">✕</text>
        </view>
        <picker mode="selector" :range="statusOptions" range-key="label" @change="onStatusChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedStatusLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 角色列表 -->
    <view v-if="!loading || roleList.length > 0" class="role-list">
      <view
        v-for="role in roleList"
        :key="role.id"
        class="role-card"
      >
        <!-- 角色头部 -->
        <view class="role-header">
          <view class="role-icon">
            <text class="icon-text">{{ role.roleName.charAt(0).toUpperCase() }}</text>
          </view>
          <view class="role-info">
            <text class="role-name">{{ role.roleName }}</text>
            <text class="role-code">{{ role.roleCode }}</text>
          </view>
          <view :class="['status-badge', { 'status-active': role.status === 1, 'status-inactive': role.status === 0 }]">
            {{ role.status === 1 ? '启用' : '禁用' }}
          </view>
        </view>

        <!-- 角色详情 -->
        <view class="role-details">
          <view class="detail-item">
            <text class="detail-label">描述</text>
            <text class="detail-value">{{ role.description || '-' }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">创建时间</text>
            <text class="detail-value">{{ role.createTime || '-' }}</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="role-actions">
          <button
            class="action-btn btn-privilege"
            :disabled="role.admin"
            @click="onPrivileges(role)"
          >
            <text class="btn-icon">🔐</text>
            <text class="btn-text">权限</text>
          </button>
          <button
            class="action-btn btn-edit"
            :disabled="role.admin"
            @click="onEdit(role)"
          >
            <text class="btn-icon">✎</text>
            <text class="btn-text">编辑</text>
          </button>
          <button
            class="action-btn btn-delete"
            :disabled="role.admin"
            @click="onDelete(role)"
          >
            <text class="btn-icon">🗑</text>
            <text class="btn-text">删除</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && !loadingMore && roleList.length === 0" class="empty-state">
      <text class="empty-icon">🔖</text>
      <text class="empty-title">暂无角色</text>
      <text class="empty-desc">点击新增按钮创建第一个角色</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && roleList.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 加载更多 -->
    <view v-if="loadingMore" class="loading-more">
      <text class="loading-more-text">加载更多...</text>
    </view>

    <!-- 没有更多 -->
    <view v-if="!hasMore && roleList.length > 0" class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </scroll-view>

  <!-- 新增角色按钮 -->
  <view class="add-btn" @click="onAdd">
    <text class="add-icon">+</text>
  </view>

  <!-- 新增/编辑角色弹窗 -->
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
        <text class="form-label">角色名称 *</text>
        <input
          v-model="form.roleName"
          class="form-input"
          placeholder="请输入角色名称"
        />
      </view>
      <view class="form-group">
        <text class="form-label">角色编码 *</text>
        <input
          v-model="form.roleCode"
          class="form-input"
          placeholder="请输入角色编码"
        />
      </view>
      <view class="form-group">
        <text class="form-label">描述</text>
        <textarea
          v-model="form.description"
          class="form-textarea"
          placeholder="请输入描述"
          maxlength="200"
        />
      </view>
      <view class="form-group">
        <text class="form-label">状态</text>
        <radio-group @change="onStatusRadioChange">
          <label class="radio-item">
            <radio :value="1" :checked="form.status === 1" color="#8B5CF6" />
            <text class="radio-label">启用</text>
          </label>
          <label class="radio-item">
            <radio :value="0" :checked="form.status === 0" color="#8B5CF6" />
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
  getRolePageList,
  addRole,
  updateRole,
  deleteRole,
  type RoleInfo,
  type RoleListParams
} from '@/api/system/role'

defineOptions({ name: 'role-list' })

// 搜索和筛选
const searchForm = reactive<RoleListParams>({
  roleName: '',
  roleCode: '',
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
const roleList = ref<RoleInfo[]>([])
const hasMore = ref(true)
const pagination = reactive({
  pageIndex: 1,
  pageSize: 10,
  total: 0
})

// 表单弹窗
const showModal = ref(false)
const modalTitle = ref('')
const formType = ref(0) // 0-新增, 1-编辑
const form = reactive<Partial<RoleInfo>>({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

// 获取角色列表
const fetchRoles = async (isRefresh = false) => {
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
    const res = await getRolePageList(params)
    
    if (isRefresh) {
      roleList.value = res.records || []
    } else {
      roleList.value.push(...(res.records || []))
    }
    
    pagination.total = res.total
    hasMore.value = roleList.value.length < pagination.total
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pagination.pageIndex = 1
  fetchRoles(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true
  pagination.pageIndex++
  fetchRoles(false).finally(() => {
    loadingMore.value = false
  })
}

// 清空搜索
const clearSearch = () => {
  searchForm.roleName = ''
  pagination.pageIndex = 1
  fetchRoles(true)
}

const clearCode = () => {
  searchForm.roleCode = ''
  pagination.pageIndex = 1
  fetchRoles(true)
}

// 状态筛选
const onStatusChange = (e: any) => {
  const index = e.detail.value
  selectedStatusLabel.value = statusOptions[index].label
  searchForm.status = statusOptions[index].value
  pagination.pageIndex = 1
  fetchRoles(true)
}

// 新增角色
const onAdd = () => {
  modalTitle.value = '新增角色'
  formType.value = 0
  Object.assign(form, {
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  })
  showModal.value = true
}

// 编辑角色
const onEdit = (role: RoleInfo) => {
  modalTitle.value = '编辑角色'
  formType.value = 1
  Object.assign(form, { ...role })
  showModal.value = true
}

// 关闭弹窗
const closeModal = () => {
  showModal.value = false
}

// 状态选择
const onStatusRadioChange = (e: any) => {
  form.status = e.detail.value
}

// 保存角色
const handleOk = async () => {
  if (!form.roleName) {
    uni.showToast({ title: '请输入角色名称', icon: 'none' })
    return
  }
  if (!form.roleCode) {
    uni.showToast({ title: '请输入角色编码', icon: 'none' })
    return
  }

  try {
    if (formType.value === 0) {
      await addRole(form)
      uni.showToast({ title: '新增成功', icon: 'success' })
    } else {
      await updateRole(form)
      uni.showToast({ title: '更新成功', icon: 'success' })
    }
    showModal.value = false
    fetchRoles(true)
  } catch (error) {
    console.error('保存角色失败:', error)
  }
}

// 分配权限
const onPrivileges = (role: RoleInfo) => {
  uni.navigateTo({
    url: `/pages/system/role/privileges?roleId=${role.id}&roleName=${role.roleName}`
  })
}

// 删除角色
const onDelete = (role: RoleInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除角色「${role.roleName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteRole(role.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchRoles(true)
        } catch (error) {
          console.error('删除角色失败:', error)
        }
      }
    }
  })
}

// 初始化
fetchRoles(true)
</script>

<style lang="scss" scoped>
.role-container {
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

// 角色列表
.role-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.role-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.role-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-text {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
}

.role-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
}

.role-code {
  font-size: 13px;
  color: #64748B;
  font-family: 'Courier New', monospace;
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

.role-details {
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
.role-actions {
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

.btn-privilege {
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  color: #ffffff;
}

.btn-edit {
  background: #3B82F6;
  color: #ffffff;
}

.btn-delete {
  background: #EF4444;
  color: #ffffff;
}

.action-btn:disabled {
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
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.4);
  z-index: 100;
}

.add-icon {
  font-size: 28px;
  color: #ffffff;
  font-weight: 300;
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

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 14px;
  background: #F8FAFC;
  min-height: 80px;
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
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  color: #ffffff;
}
</style>
