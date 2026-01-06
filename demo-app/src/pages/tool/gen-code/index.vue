<template>
  <scroll-view
    class="gen-code-container"
    scroll-y
    refresher-enabled
    :refresher-triggered="refreshing"
    @refresherrefresh="onRefresh"
    @scrolltolower="loadMore"
    lower-threshold="100"
  >
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          v-model="searchForm.tableName"
          class="search-input"
          placeholder="搜索表名称"
          placeholder-class="search-placeholder"
          @input="fetchTables"
        />
        <text v-if="searchForm.tableName" class="clear-icon" @click="clearSearch">✕</text>
      </view>
    </view>

    <!-- 表列表 -->
    <view v-if="!loading || tableList.length > 0" class="table-list">
      <view
        v-for="table in tableList"
        :key="table.id"
        class="table-card"
      >
        <!-- 表头部 -->
        <view class="table-header" @click="onViewColumns(table)">
          <view class="table-title-row">
            <text class="table-name">{{ table.tableName }}</text>
            <text class="view-detail">查看字段 →</text>
          </view>
          <text v-if="table.tableComment" class="table-comment">{{ table.tableComment }}</text>
        </view>

        <!-- 表信息 -->
        <view class="table-info">
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">类名</text>
              <text class="info-value">{{ table.className }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">模块</text>
              <text class="info-value">{{ table.moduleName }}</text>
            </view>
          </view>
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">作者</text>
              <text class="info-value">{{ table.author }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">生成方式</text>
              <view class="generate-type-selector">
                <picker mode="selector" :range="generateTypeOptions" range-key="label" @change="(e) => onGenerateTypeChange(table, e)">
                  <view :class="['generate-type-badge', { 'type-zip': table.generateType === 0, 'type-local': table.generateType === 1 }]">
                    {{ table.generateType === 0 ? '压缩包' : '本地工程' }}
                  </view>
                </picker>
              </view>
            </view>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="table-actions">
          <button
            class="action-btn btn-generate"
            @click="onGenerate(table)"
          >
            <text class="btn-icon">⚡</text>
            <text class="btn-text">生成</text>
          </button>
          <button
            class="action-btn btn-edit"
            @click="onEdit(table)"
          >
            <text class="btn-icon">✎</text>
            <text class="btn-text">修改</text>
          </button>
          <button
            class="action-btn btn-delete"
            @click="onDelete(table)"
          >
            <text class="btn-icon">🗑</text>
            <text class="btn-text">删除</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && !loadingMore && tableList.length === 0" class="empty-state">
      <text class="empty-icon">📊</text>
      <text class="empty-title">暂无数据表</text>
      <text class="empty-desc">点击创建按钮添加第一个表</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && tableList.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 加载更多 -->
    <view v-if="loadingMore" class="loading-more">
      <text class="loading-more-text">加载更多...</text>
    </view>

    <!-- 没有更多 -->
    <view v-if="!hasMore && tableList.length > 0" class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </scroll-view>

  <!-- 创建表按钮 -->
  <view class="add-btn" @click="onCreateTable">
    <text class="add-icon">+</text>
  </view>

  <!-- 创建表弹窗 -->
  <u-popup
    v-model="showCreateModal"
    mode="center"
    :round="20"
    :title="'创建表'"
    :closeOnClickOverlay="true"
    @close="closeCreateModal"
    width="90%"
    :safe-area-inset-bottom="true"
  >
    <scroll-view scroll-y class="modal-content">
      <view class="form-group">
        <text class="form-label">建表SQL语句 *</text>
        <textarea
          v-model="createForm.sql"
          class="form-textarea sql-textarea"
          placeholder="请输入建表SQL语句，例如：&#10;CREATE TABLE user (&#10;  id BIGINT PRIMARY KEY AUTO_INCREMENT,&#10;  name VARCHAR(100) COMMENT '姓名'&#10;)"
          maxlength="2000"
        />
      </view>
      <view class="form-group">
        <text class="form-label">模块名 *</text>
        <input
          v-model="createForm.moduleName"
          class="form-input"
          placeholder="请输入模块名，例如：system"
        />
      </view>
    </scroll-view>
    <view class="modal-footer">
      <button class="modal-btn modal-btn-cancel" @click="closeCreateModal">取消</button>
      <button class="modal-btn modal-btn-confirm" @click="handleCreateTable">确定</button>
    </view>
  </u-popup>

  <!-- 编辑表弹窗 -->
  <u-popup
    v-model="showEditModal"
    mode="bottom"
    :round="20"
    :title="'编辑表信息'"
    :closeOnClickOverlay="true"
    @close="closeEditModal"
    width="100%"
    :safe-area-inset-bottom="true"
  >
    <scroll-view scroll-y class="modal-content">
      <view class="form-group">
        <text class="form-label">表名</text>
        <input
          v-model="editForm.tableName"
          class="form-input"
          placeholder="请输入表名"
        />
      </view>
      <view class="form-group">
        <text class="form-label">类名</text>
        <input
          v-model="editForm.className"
          class="form-input"
          placeholder="请输入类名"
        />
      </view>
      <view class="form-group">
        <text class="form-label">模块名</text>
        <input
          v-model="editForm.moduleName"
          class="form-input"
          placeholder="请输入模块名"
        />
      </view>
      <view class="form-group">
        <text class="form-label">作者</text>
        <input
          v-model="editForm.author"
          class="form-input"
          placeholder="请输入作者"
        />
      </view>
      <view class="form-group">
        <text class="form-label">表注释</text>
        <textarea
          v-model="editForm.tableComment"
          class="form-textarea"
          placeholder="请输入表注释"
          maxlength="200"
        />
      </view>
      <view class="form-group">
        <text class="form-label">生成方式</text>
        <radio-group @change="onGenerateTypeRadioChange">
          <label class="radio-item">
            <radio :value="0" :checked="editForm.generateType === 0" color="#3B82F6" />
            <text class="radio-label">压缩包</text>
          </label>
          <label class="radio-item">
            <radio :value="1" :checked="editForm.generateType === 1" color="#3B82F6" />
            <text class="radio-label">本地工程</text>
          </label>
        </radio-group>
      </view>
      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea
          v-model="editForm.remark"
          class="form-textarea"
          placeholder="请输入备注"
          maxlength="200"
        />
      </view>
    </scroll-view>
    <view class="modal-footer">
      <button class="modal-btn modal-btn-cancel" @click="closeEditModal">取消</button>
      <button class="modal-btn modal-btn-confirm" @click="handleUpdateTable">确定</button>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  getTableInfoList,
  createTable,
  updateTable,
  deleteTable,
  createCode,
  createCodeZip,
  type TableInfo,
  type TableListParams
} from '@/api/tool'

defineOptions({ name: 'gen-code-list' })

// 搜索
const searchForm = reactive<TableListParams>({
  tableName: ''
})

// 数据状态
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const tableList = ref<TableInfo[]>([])
const hasMore = ref(true)
const pagination = reactive({
  pageIndex: 1,
  pageSize: 10,
  total: 0
})

// 生成方式选项
const generateTypeOptions = [
  { label: '压缩包', value: 0 },
  { label: '本地工程', value: 1 }
]

// 创建表弹窗
const showCreateModal = ref(false)
const createForm = reactive({
  sql: '',
  moduleName: ''
})

// 编辑表弹窗
const showEditModal = ref(false)
const editForm = reactive<Partial<TableInfo>>({
  tableName: '',
  className: '',
  moduleName: '',
  author: '',
  tableComment: '',
  generateType: 0,
  remark: ''
})

// 获取表列表
const fetchTables = async (isRefresh = false) => {
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
    const res = await getTableInfoList(params)
    
    if (isRefresh) {
      tableList.value = res.records || []
    } else {
      tableList.value.push(...(res.records || []))
    }
    
    pagination.total = res.total
    hasMore.value = tableList.value.length < pagination.total
  } catch (error) {
    console.error('获取表列表失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pagination.pageIndex = 1
  fetchTables(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true
  pagination.pageIndex++
  fetchTables(false).finally(() => {
    loadingMore.value = false
  })
}

// 清空搜索
const clearSearch = () => {
  searchForm.tableName = ''
  pagination.pageIndex = 1
  fetchTables(true)
}

// 创建表
const onCreateTable = () => {
  createForm.sql = ''
  createForm.moduleName = ''
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const handleCreateTable = async () => {
  if (!createForm.sql) {
    uni.showToast({ title: '请输入建表SQL语句', icon: 'none' })
    return
  }
  if (!createForm.moduleName) {
    uni.showToast({ title: '请输入模块名', icon: 'none' })
    return
  }

  try {
    await createTable(createForm)
    uni.showToast({ title: '创建成功', icon: 'success' })
    showCreateModal.value = false
    fetchTables(true)
  } catch (error) {
    console.error('创建表失败:', error)
  }
}

// 编辑表
const onEdit = (table: TableInfo) => {
  Object.assign(editForm, table)
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const onGenerateTypeRadioChange = (e: any) => {
  editForm.generateType = e.detail.value
}

const handleUpdateTable = async () => {
  if (!editForm.tableName) {
    uni.showToast({ title: '请输入表名', icon: 'none' })
    return
  }
  if (!editForm.className) {
    uni.showToast({ title: '请输入类名', icon: 'none' })
    return
  }
  if (!editForm.moduleName) {
    uni.showToast({ title: '请输入模块名', icon: 'none' })
    return
  }

  try {
    await updateTable(editForm)
    uni.showToast({ title: '更新成功', icon: 'success' })
    showEditModal.value = false
    fetchTables(true)
  } catch (error) {
    console.error('更新表失败:', error)
  }
}

// 生成方式变更
const onGenerateTypeChange = async (table: TableInfo, e: any) => {
  const index = e.detail.value
  const type = generateTypeOptions[index].value
  
  try {
    table.generateType = type
    await updateTable(table)
    uni.showToast({ 
      title: `已切换为${type === 0 ? '压缩包' : '本地工程'}`, 
      icon: 'success' 
    })
  } catch (error) {
    console.error('更新生成方式失败:', error)
    // 恢复原值
    table.generateType = type === 0 ? 1 : 0
  }
}

// 查看字段
const onViewColumns = (table: TableInfo) => {
  uni.navigateTo({
    url: `/pages/tool/gen-code/columns?tableId=${table.id}&tableName=${table.tableName}&tableComment=${table.tableComment}`
  })
}

// 生成代码
const onGenerate = async (table: TableInfo) => {
  try {
    if (table.generateType === 0) {
      // 压缩包下载
      await createCodeZip(table.id)
      uni.showToast({ title: '代码已生成并下载', icon: 'success' })
    } else {
      // 本地工程
      await createCode(table.id)
      uni.showToast({ title: '代码生成成功', icon: 'success' })
    }
    fetchTables(true)
  } catch (error) {
    console.error('生成代码失败:', error)
  }
}

// 删除表
const onDelete = (table: TableInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除表「${table.tableName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteTable(table.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchTables(true)
        } catch (error) {
          console.error('删除表失败:', error)
        }
      }
    }
  })
}

// 初始化
fetchTables(true)
</script>

<style lang="scss" scoped>
.gen-code-container {
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

// 表列表
.table-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.table-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.table-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.table-name {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
  flex: 1;
}

.view-detail {
  font-size: 13px;
  color: #3B82F6;
}

.table-comment {
  font-size: 13px;
  color: #64748B;
}

.table-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  gap: 16px;
}

.info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #94A3B8;
}

.info-value {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.generate-type-selector {
  display: flex;
  align-items: center;
}

.generate-type-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.type-zip {
  background: #DBEAFE;
  color: #1E40AF;
}

.type-local {
  background: #DCFCE7;
  color: #166534;
}

// 操作按钮
.table-actions {
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

.btn-generate {
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

.sql-textarea {
  font-family: 'Courier New', monospace;
  min-height: 150px;
  font-size: 13px;
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
