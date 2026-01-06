<template>
  <scroll-view
    class="columns-container"
    scroll-y
  >
    <!-- 头部 -->
    <view class="header">
      <text class="title">{{ tableName }}</text>
      <text class="subtitle">字段列表</text>
    </view>

    <!-- 字段列表 -->
    <view v-if="!loading || columnList.length > 0" class="column-list">
      <view
        v-for="(column, index) in columnList"
        :key="column.id"
        class="column-card"
      >
        <!-- 字段基本信息 -->
        <view class="column-header">
          <view class="column-title-row">
            <text class="column-name">{{ column.columnName }}</text>
            <view class="column-badges">
              <text v-if="column.primaryKey" class="badge badge-primary">主键</text>
              <text v-if="column.notNull" class="badge badge-not-null">非空</text>
              <text v-if="column.autoIncrement" class="badge badge-auto">自增</text>
            </view>
          </view>
          <text v-if="column.comment" class="column-comment">{{ column.comment }}</text>
        </view>

        <!-- 字段详情 -->
        <view class="column-details">
          <view class="detail-item">
            <text class="detail-label">字段属性名</text>
            <input
              v-model="column.propertyName"
              class="detail-input"
              placeholder="请输入属性名"
            />
          </view>
          <view class="detail-item">
            <text class="detail-label">字段类型</text>
            <text class="detail-value">{{ column.columnType }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">Java类型</text>
            <picker mode="selector" :range="javaTypeOptions" range-key="label" @change="(e) => onJavaTypeChange(column, e)">
              <view class="detail-picker">
                <text class="picker-text">{{ column.javaType }}</text>
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          <view class="detail-item">
            <text class="detail-label">字段注释</text>
            <input
              v-model="column.comment"
              class="detail-input"
              placeholder="请输入字段注释"
            />
          </view>
          <view class="detail-item">
            <text class="detail-label">是否自增</text>
            <switch
              :checked="column.autoIncrement"
              @change="(e) => column.autoIncrement = e.detail.value"
              color="#3B82F6"
            />
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="column-actions">
          <button
            class="action-btn btn-delete"
            @click="onDelete(column)"
          >
            <text class="btn-icon">🗑</text>
            <text class="btn-text">删除</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && columnList.length === 0" class="empty-state">
      <text class="empty-icon">📋</text>
      <text class="empty-title">暂无字段</text>
      <text class="empty-desc">该表还没有任何字段</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && columnList.length === 0" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>
  </scroll-view>

  <!-- 底部保存按钮 -->
  <view class="footer-actions">
    <button class="footer-btn btn-save" @click="handleSave">
      <text class="btn-icon">💾</text>
      <text class="btn-text">保存修改</text>
    </button>
    <button class="footer-btn btn-back" @click="handleBack">
      <text class="btn-icon">←</text>
      <text class="btn-text">返回</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  getColumnList,
  batchUpdateColumn,
  deleteColumn,
  type ColumnInfo
} from '@/api/tool'

defineOptions({ name: 'column-list' })

// 数据状态
const loading = ref(false)
const columnList = ref<ColumnInfo[]>([])

// 表信息
const tableName = ref('')
const tableId = ref<number>()

// Java类型选项
const javaTypeOptions = [
  { label: 'Integer', value: 'Integer' },
  { label: 'Long', value: 'Long' },
  { label: 'Float', value: 'Float' },
  { label: 'Double', value: 'Double' },
  { label: 'Boolean', value: 'Boolean' },
  { label: 'String', value: 'String' },
  { label: 'BigDecimal', value: 'BigDecimal' },
  { label: 'LocalDate', value: 'LocalDate' },
  { label: 'LocalDateTime', value: 'LocalDateTime' }
]

// 获取字段列表
const fetchColumns = async () => {
  loading.value = true
  try {
    const res = await getColumnList(tableId.value!)
    columnList.value = res || []
  } catch (error) {
    console.error('获取字段列表失败:', error)
  } finally {
    loading.value = false
  }
}

// Java类型变更
const onJavaTypeChange = (column: ColumnInfo, e: any) => {
  const index = e.detail.value
  column.javaType = javaTypeOptions[index].value
}

// 保存修改
const handleSave = async () => {
  try {
    await batchUpdateColumn(columnList.value)
    uni.showToast({ title: '保存成功', icon: 'success' })
    fetchColumns()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 删除字段
const onDelete = (column: ColumnInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除字段「${column.columnName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteColumn(column.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchColumns()
        } catch (error) {
          console.error('删除字段失败:', error)
        }
      }
    }
  })
}

// 返回
const handleBack = () => {
  uni.navigateBack()
}

// 初始化
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage.options
  
  tableId.value = Number(options.tableId)
  tableName.value = `${options.tableName}（${options.tableComment}）`
  
  fetchColumns()
})
</script>

<style lang="scss" scoped>
.columns-container {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 80px;
}

// 头部
.header {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  padding: 24px 20px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.title {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.subtitle {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

// 字段列表
.column-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.column-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.column-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}

.column-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.column-name {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
  flex: 1;
}

.column-badges {
  display: flex;
  gap: 4px;
}

.badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.badge-primary {
  background: #DBEAFE;
  color: #1E40AF;
}

.badge-not-null {
  background: #FEF3C7;
  color: #92400E;
}

.badge-auto {
  background: #DCFCE7;
  color: #166534;
}

.column-comment {
  font-size: 13px;
  color: #64748B;
}

.column-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-label {
  font-size: 12px;
  color: #94A3B8;
}

.detail-value {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.detail-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 14px;
  background: #F8FAFC;
}

.detail-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  background: #F8FAFC;
}

.picker-text {
  font-size: 14px;
  color: #334155;
  font-family: 'Courier New', monospace;
}

.picker-arrow {
  font-size: 10px;
  color: #94A3B8;
}

// 操作按钮
.column-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
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

.btn-delete {
  background: #EF4444;
  color: #ffffff;
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

.btn-save {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  color: #ffffff;
}

.btn-back {
  background: #F1F5F9;
  color: #475569;
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
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.loading-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.loading-text {
  font-size: 14px;
  color: #94A3B8;
}
</style>
