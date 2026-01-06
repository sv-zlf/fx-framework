<template>
  <scroll-view class="user-detail-container" scroll-y>
    <!-- 头部 -->
    <view class="header">
      <view class="user-avatar-large">
        <text class="avatar-text">{{ form.nickName.charAt(0).toUpperCase() }}</text>
      </view>
      <text class="user-name">{{ form.nickName }}</text>
      <text class="user-username">@{{ form.userName }}</text>
    </view>

    <!-- 表单 -->
    <view class="form-container">
      <view class="form-section">
        <text class="section-title">基本信息</text>
        
        <view class="form-item">
          <text class="item-label">用户名称 *</text>
          <input
            v-model="form.userName"
            class="item-input"
            placeholder="请输入用户名称"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label">昵称 *</text>
          <input
            v-model="form.nickName"
            class="item-input"
            placeholder="请输入昵称"
          />
        </view>
      </view>

      <view class="form-section">
        <text class="section-title">联系方式</text>
        
        <view class="form-item">
          <text class="item-label">手机号码</text>
          <input
            v-model="form.phone"
            class="item-input"
            placeholder="请输入手机号码"
            type="number"
          />
        </view>
        
        <view class="form-item">
          <text class="item-label">邮箱</text>
          <input
            v-model="form.email"
            class="item-input"
            placeholder="请输入邮箱"
            type="email"
          />
        </view>
      </view>

      <view class="form-section">
        <text class="section-title">权限设置</text>
        
        <view class="form-item">
          <text class="item-label">角色 *</text>
          <picker mode="selector" :range="roleList" range-key="roleName" @change="onRoleChange">
            <view class="item-picker">
              <text class="picker-text">{{ selectedRoleName || '请选择角色' }}</text>
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="item-label">状态</text>
          <view class="switch-row">
            <text class="switch-label">{{ form.status === 1 ? '启用' : '禁用' }}</text>
            <switch
              :checked="form.status === 1"
              @change="onStatusSwitchChange"
              color="#3B82F6"
            />
          </view>
        </view>
      </view>
    </view>
  </scroll-view>

  <!-- 底部按钮 -->
  <view class="footer-actions">
    <button class="footer-btn btn-save" @click="handleSave">
      <text class="btn-icon">💾</text>
      <text class="btn-text">保存</text>
    </button>
    <button class="footer-btn btn-cancel" @click="handleCancel">
      <text class="btn-icon">✕</text>
      <text class="btn-text">取消</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  saveOrUpdate,
  getRoleList,
  type UserInfo,
  type RoleInfo
} from '@/api/system/user'

defineOptions({ name: 'user-detail' })

// 表单数据
const form = reactive<Partial<UserInfo>>({
  id: undefined,
  userName: '',
  nickName: '',
  phone: '',
  email: '',
  roles: [],
  status: 1
})

const isEdit = ref(false)

// 角色列表
const roleList = ref<RoleInfo[]>([])
const selectedRoleName = ref('')

// 角色选择
const onRoleChange = (e: any) => {
  const index = e.detail.value
  const role = roleList.value[index]
  form.roles = [role.id]
  selectedRoleName.value = role.roleName
}

// 状态切换
const onStatusSwitchChange = (e: any) => {
  form.status = e.detail.value ? 1 : 0
}

// 保存
const handleSave = async () => {
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
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

// 取消
const handleCancel = () => {
  uni.navigateBack()
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
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage.options
  
  // 判断是新增还是编辑
  if (options.userId) {
    isEdit.value = true
    form.id = Number(options.userId)
    form.userName = options.userName || ''
    form.nickName = options.nickName || ''
    form.phone = options.phone || ''
    form.email = options.email || ''
    form.status = Number(options.status) || 1
    
    if (options.roleId) {
      form.roles = [Number(options.roleId)]
    }
  }
  
  fetchRoles()
})
</script>

<style lang="scss" scoped>
.user-detail-container {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 80px;
}

// 头部
.header {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  padding: 40px 20px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.user-avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 40px;
  background: rgba(255, 255, 255, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.avatar-text {
  font-size: 36px;
  font-weight: 600;
  color: #ffffff;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.user-username {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

// 表单容器
.form-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.item-label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.item-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 15px;
  background: #F8FAFC;
  transition: all 0.2s;
}

.item-input:focus {
  border-color: #3B82F6;
  background: #ffffff;
}

.item-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  background: #F8FAFC;
}

.picker-text {
  font-size: 15px;
  color: #334155;
}

.picker-arrow {
  font-size: 10px;
  color: #94A3B8;
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #F8FAFC;
  border-radius: 8px;
}

.switch-label {
  font-size: 15px;
  color: #334155;
}

// 底部操作
.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 12px 16px;
  border-top: 1px solid #E2E8F0;
  display: flex;
  gap: 12px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.footer-btn {
  flex: 1;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  border: none;
  font-size: 15px;
  font-weight: 500;
  gap: 6px;
}

.btn-icon {
  font-size: 16px;
}

.btn-text {
  font-size: 15px;
}

.btn-save {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  color: #ffffff;
}

.btn-cancel {
  background: #F1F5F9;
  color: #475569;
}
</style>
