<template>
  <scroll-view
    class="task-container"
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
        <text class="search-icon">🔍</text>
        <input
          v-model="searchForm.taskName"
          class="search-input"
          placeholder="搜索任务名称"
          placeholder-class="search-placeholder"
          @input="fetchTasks"
        />
        <text v-if="searchForm.taskName" class="clear-icon" @click="clearSearch">✕</text>
      </view>
      <view class="filter-group">
        <picker mode="selector" :range="taskGroups" range-key="label" @change="onGroupChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedGroupLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
        <picker mode="selector" :range="statusOptions" range-key="label" @change="onStatusChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedStatusLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 任务列表 -->
    <view v-if="!loading || taskList.length > 0" class="task-list">
      <view
        v-for="task in taskList"
        :key="task.id"
        class="task-card"
      >
        <!-- 任务头部 -->
        <view class="task-header">
          <view class="task-name-row">
            <text class="task-name">{{ task.taskName }}</text>
            <view :class="['status-badge', { 'status-normal': task.status === 0, 'status-paused': task.status === 1 }]">
              {{ task.status === 0 ? '正常' : '暂停' }}
            </view>
          </view>
          <text class="task-group">{{ task.taskGroup }}</text>
        </view>

        <!-- 任务详情 -->
        <view class="task-details">
          <view class="detail-item">
            <text class="detail-label">Cron表达式</text>
            <text class="detail-value cron-value">{{ task.cronExpression }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">调用目标</text>
            <text class="detail-value">{{ task.invokeTarget }}</text>
          </view>
          <view v-if="task.description" class="detail-item">
            <text class="detail-label">描述</text>
            <text class="detail-value">{{ task.description }}</text>
          </view>
          <view class="detail-row">
            <view class="detail-item">
              <text class="detail-label">并发</text>
              <text :class="['detail-value', { 'concurrent-allowed': task.concurrent === 1, 'concurrent-forbidden': task.concurrent === 0 }]">
                {{ task.concurrent === 1 ? '允许' : '禁止' }}
              </text>
            </view>
            <view class="detail-item">
              <text class="detail-label">执行次数</text>
              <text class="detail-value">{{ task.executionCount }}</text>
            </view>
          </view>
          <view v-if="task.failureCount > 0" class="detail-item">
            <text class="detail-label">失败次数</text>
            <text class="detail-value failure-count">{{ task.failureCount }}</text>
          </view>
          <view v-if="task.lastExecutionTime" class="detail-item">
            <text class="detail-label">上次执行</text>
            <text class="detail-value">{{ task.lastExecutionTime }}</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="task-actions">
          <button
            class="action-btn btn-execute"
            :disabled="task.status === 1"
            @click="onExecute(task)"
          >
            <text class="btn-icon">▶</text>
            <text class="btn-text">执行</text>
          </button>
          <button
            v-if="task.status === 0"
            class="action-btn btn-pause"
            @click="onPause(task)"
          >
            <text class="btn-icon">⏸</text>
            <text class="btn-text">暂停</text>
          </button>
          <button
            v-else
            class="action-btn btn-resume"
            @click="onResume(task)"
          >
            <text class="btn-icon">▶</text>
            <text class="btn-text">恢复</text>
          </button>
          <button class="action-btn btn-edit" @click="onEdit(task)">
            <text class="btn-icon">✎</text>
            <text class="btn-text">修改</text>
          </button>
          <button class="action-btn btn-delete" @click="onDelete(task)">
            <text class="btn-icon">🗑</text>
            <text class="btn-text">删除</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && !loadingMore && taskList.length === 0" class="empty-state">
      <text class="empty-icon">📋</text>
      <text class="empty-title">暂无定时任务</text>
      <text class="empty-desc">点击新增按钮创建第一个任务</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && taskList.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 加载更多 -->
    <view v-if="loadingMore" class="loading-more">
      <text class="loading-more-text">加载更多...</text>
    </view>

    <!-- 没有更多 -->
    <view v-if="!hasMore && taskList.length > 0" class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </scroll-view>

  <!-- 新增按钮 -->
  <view class="add-btn" @click="onAdd">
    <text class="add-icon">+</text>
  </view>

  <!-- 新增/编辑弹窗 -->
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
        <text class="form-label">任务名称 *</text>
        <input
          v-model="form.taskName"
          class="form-input"
          placeholder="请输入任务名称"
        />
      </view>
      <view class="form-group">
        <text class="form-label">任务分组 *</text>
        <picker mode="selector" :range="taskGroups" range-key="label" @change="onFormGroupChange">
          <view class="form-picker">
            <text class="form-picker-text">{{ form.taskGroup }}</text>
            <text class="form-picker-arrow">▼</text>
          </view>
        </picker>
      </view>
      <view class="form-group">
        <text class="form-label">Cron表达式 *</text>
        <input
          v-model="form.cronExpression"
          class="form-input"
          placeholder="例如: 0/5 * * * * ?"
        />
        <text class="form-hint" @click="openCronHelper">Cron表达式生成器 →</text>
      </view>
      <view class="form-group">
        <text class="form-label">调用目标 *</text>
        <input
          v-model="form.invokeTarget"
          class="form-input"
          placeholder="例如: com.fxly.demo.api.core.task.SampleTask"
        />
      </view>
      <view class="form-group">
        <text class="form-label">任务描述</text>
        <textarea
          v-model="form.description"
          class="form-textarea"
          placeholder="请输入任务描述"
          maxlength="200"
        />
      </view>
      <view class="form-group">
        <text class="form-label">任务状态</text>
        <radio-group @change="onStatusRadioChange">
          <label class="radio-item">
            <radio :value="0" :checked="form.status === 0" color="#3B82F6" />
            <text class="radio-label">正常</text>
          </label>
          <label class="radio-item">
            <radio :value="1" :checked="form.status === 1" color="#3B82F6" />
            <text class="radio-label">暂停</text>
          </label>
        </radio-group>
      </view>
      <view class="form-group">
        <text class="form-label">并发执行</text>
        <radio-group @change="onConcurrentRadioChange">
          <label class="radio-item">
            <radio :value="1" :checked="form.concurrent === 1" color="#3B82F6" />
            <text class="radio-label">允许</text>
          </label>
          <label class="radio-item">
            <radio :value="0" :checked="form.concurrent === 0" color="#3B82F6" />
            <text class="radio-label">禁止</text>
          </label>
        </radio-group>
      </view>
      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea
          v-model="form.remark"
          class="form-textarea"
          placeholder="请输入备注"
          maxlength="200"
        />
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
  getTaskList,
  saveOrUpdate,
  deleteTask,
  executeTask,
  pauseTask,
  resumeTask,
  type TaskInfo,
  type TaskListParams
} from '@/api/task'

defineOptions({ name: 'task-list' })

// 搜索和筛选
const searchForm = reactive<TaskListParams>({
  taskName: '',
  taskGroup: '',
  status: undefined
})

const taskGroups = [
  { label: '全部分组', value: '' },
  { label: '默认分组', value: 'DEFAULT' },
  { label: '系统分组', value: 'SYSTEM' },
  { label: '业务分组', value: 'BUSINESS' }
]

const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '正常', value: 0 },
  { label: '暂停', value: 1 }
]

const selectedGroupLabel = ref('全部分组')
const selectedStatusLabel = ref('全部状态')

// 数据状态
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const taskList = ref<TaskInfo[]>([])
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
const form = reactive<Partial<TaskInfo>>({
  taskName: '',
  taskGroup: 'DEFAULT',
  cronExpression: '',
  invokeTarget: '',
  description: '',
  status: 0,
  concurrent: 1,
  remark: ''
})

// 获取任务列表
const fetchTasks = async (isRefresh = false) => {
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
    const res = await getTaskList(params)
    
    if (isRefresh) {
      taskList.value = res.records || []
    } else {
      taskList.value.push(...(res.records || []))
    }
    
    pagination.total = res.total
    hasMore.value = taskList.value.length < pagination.total
  } catch (error) {
    console.error('获取任务列表失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pagination.pageIndex = 1
  fetchTasks(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true
  pagination.pageIndex++
  fetchTasks(false).finally(() => {
    loadingMore.value = false
  })
}

// 清空搜索
const clearSearch = () => {
  searchForm.taskName = ''
  pagination.pageIndex = 1
  fetchTasks(true)
}

// 分组筛选
const onGroupChange = (e: any) => {
  const index = e.detail.value
  selectedGroupLabel.value = taskGroups[index].label
  searchForm.taskGroup = taskGroups[index].value
  pagination.pageIndex = 1
  fetchTasks(true)
}

// 状态筛选
const onStatusChange = (e: any) => {
  const index = e.detail.value
  selectedStatusLabel.value = statusOptions[index].label
  searchForm.status = statusOptions[index].value
  pagination.pageIndex = 1
  fetchTasks(true)
}

// 新增任务
const onAdd = () => {
  modalTitle.value = '新增任务'
  formType.value = 0
  Object.assign(form, {
    taskName: '',
    taskGroup: 'DEFAULT',
    cronExpression: '',
    invokeTarget: '',
    description: '',
    status: 0,
    concurrent: 1,
    remark: ''
  })
  showModal.value = true
}

// 编辑任务
const onEdit = (task: TaskInfo) => {
  modalTitle.value = '修改任务'
  formType.value = 1
  Object.assign(form, { ...task })
  showModal.value = true
}

// 关闭弹窗
const closeModal = () => {
  showModal.value = false
}

// 表单分组选择
const onFormGroupChange = (e: any) => {
  const index = e.detail.value
  form.taskGroup = taskGroups[index].value
}

// 表单状态选择
const onStatusRadioChange = (e: any) => {
  form.status = e.detail.value
}

// 表单并发选择
const onConcurrentRadioChange = (e: any) => {
  form.concurrent = e.detail.value
}

// 打开Cron帮助
const openCronHelper = () => {
  uni.navigateTo({
    url: '/pages/common/webview/index?url=https://cron.qqe2.com/'
  })
}

// 保存任务
const handleOk = async () => {
  if (!form.taskName) {
    uni.showToast({ title: '请输入任务名称', icon: 'none' })
    return
  }
  if (!form.cronExpression) {
    uni.showToast({ title: '请输入Cron表达式', icon: 'none' })
    return
  }
  if (!form.invokeTarget) {
    uni.showToast({ title: '请输入调用目标', icon: 'none' })
    return
  }

  try {
    await saveOrUpdate(form)
    uni.showToast({ title: '保存成功', icon: 'success' })
    showModal.value = false
    fetchTasks(true)
  } catch (error) {
    console.error('保存任务失败:', error)
  }
}

// 执行任务
const onExecute = async (task: TaskInfo) => {
  try {
    await executeTask(task.id)
    uni.showToast({ title: '执行成功', icon: 'success' })
    fetchTasks(true)
  } catch (error) {
    console.error('执行任务失败:', error)
  }
}

// 暂停任务
const onPause = async (task: TaskInfo) => {
  try {
    await pauseTask(task.id)
    uni.showToast({ title: '暂停成功', icon: 'success' })
    fetchTasks(true)
  } catch (error) {
    console.error('暂停任务失败:', error)
  }
}

// 恢复任务
const onResume = async (task: TaskInfo) => {
  try {
    await resumeTask(task.id)
    uni.showToast({ title: '恢复成功', icon: 'success' })
    fetchTasks(true)
  } catch (error) {
    console.error('恢复任务失败:', error)
  }
}

// 删除任务
const onDelete = (task: TaskInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除任务「${task.taskName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteTask(task.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchTasks(true)
        } catch (error) {
          console.error('删除任务失败:', error)
        }
      }
    }
  })
}

// 初始化
fetchTasks(true)
</script>

<style lang="scss" scoped>
.task-container {
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

// 任务列表
.task-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.task-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.task-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.task-name {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
  flex: 1;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-normal {
  background: #DCFCE7;
  color: #16A34A;
}

.status-paused {
  background: #FEE2E2;
  color: #DC2626;
}

.task-group {
  font-size: 13px;
  color: #64748B;
}

.task-details {
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

.detail-row {
  display: flex;
  gap: 16px;
}

.detail-row .detail-item {
  flex: 1;
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

.cron-value {
  font-family: 'Courier New', monospace;
  background: #F1F5F9;
  padding: 6px 10px;
  border-radius: 6px;
}

.concurrent-allowed {
  color: #16A34A;
}

.concurrent-forbidden {
  color: #EA580C;
}

.failure-count {
  color: #DC2626;
}

// 操作按钮
.task-actions {
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
  transition: all 0.2s;
}

.btn-icon {
  font-size: 14px;
}

.btn-text {
  font-size: 13px;
}

.btn-execute {
  background: #3B82F6;
  color: #ffffff;
}

.btn-execute:disabled {
  background: #E2E8F0;
  color: #94A3B8;
}

.btn-pause {
  background: #F59E0B;
  color: #ffffff;
}

.btn-resume {
  background: #10B981;
  color: #ffffff;
}

.btn-edit {
  background: #6366F1;
  color: #ffffff;
}

.btn-delete {
  background: #EF4444;
  color: #ffffff;
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

.form-hint {
  display: block;
  font-size: 12px;
  color: #3B82F6;
  margin-top: 4px;
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
