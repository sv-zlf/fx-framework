<template>
  <scroll-view
    class="file-container"
    scroll-y
    :scroll-into-view="scrollToId"
    :scroll-into-view-align="scrollToAlign"
  >
    <!-- Header -->
    <view class="header">
      <view class="header-content">
        <text class="header-title">文件管理</text>
      </view>
      <view class="header-stats">
        <view class="stat-item">
          <text class="stat-label">总大小</text>
          <text class="stat-value">{{ getStats().totalSize }}</text>
        </view>
      </view>
    </view>

    <!-- 搜索和筛选 -->
    <view class="filter-bar">
      <view class="search-wrapper">
        <input
          v-model="searchForm.fileName"
          class="search-input"
          placeholder="搜索文件名称"
          @input="onSearch"
        />
        <text v-if="searchForm.fileName" class="search-clear" @click="clearSearch">✕</text>
      </view>
      <picker mode="selector" :range="fileTypeOptions" range-key="label" @change="onFileTypeChange">
        <view class="filter-chip" :class="{ 'active': searchForm.fileType }">
          <text class="filter-icon">•</text>
          <text class="filter-label">{{ selectedFileTypeLabel }}</text>
        </view>
      </picker>
    </view>

    <!-- 文件列表 -->
    <view class="file-list-wrapper">
      <view class="file-list" :id="`list-page-${currentPage}`">
        <view
          v-for="file in fileList"
          :key="file.id"
          class="file-item"
          @click="onPreview(file)"
        >
          <view class="file-item-left">
            <view class="file-icon" :class="`type-${file.fileType}`">
              <text class="file-icon-text">{{ getShortFileType(file.fileType) }}</text>
            </view>
            <view class="file-info">
              <text class="file-name">{{ file.fileName }}</text>
              <text class="file-meta">{{ formatDate(file.createTime) }}</text>
            </view>
          </view>
          <view class="file-item-right">
            <text class="file-size">{{ formatFileSize(file.fileSize) }}</text>
            <button class="action-menu" @click.stop="showActionMenu(file)">
              <text class="action-icon">···</text>
            </button>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && fileList.length === 0" class="empty-state">
      <view class="empty-icon">
        <text class="empty-icon-text">·</text>
      </view>
      <text class="empty-title">暂无文件</text>
      <text class="empty-desc">点击右下角 + 号添加文件</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 分页器 -->
    <view v-if="!loading && fileList.length > 0 && totalPages > 1" class="pagination">
      <text class="pagination-info">{{ pagination.total }} 条 · 第 {{ currentPage }} / {{ totalPages }} 页</text>
      <view class="pagination-controls">
        <button
          class="pagination-btn"
          :class="{ 'btn-disabled': currentPage <= 1 }"
          @click="onPageChange(currentPage - 1)"
        >
          <text class="btn-text">上一页</text>
        </button>
        <button
          class="pagination-btn"
          :class="{ 'btn-disabled': currentPage >= totalPages }"
          @click="onPageChange(currentPage + 1)"
        >
          <text class="btn-text">下一页</text>
        </button>
      </view>
    </view>

    <!-- 浮动操作按钮 -->
    <view class="fab-container">
      <button class="fab-btn" @click="onUpload">
        <text class="fab-icon">+</text>
      </button>
    </view>
  </scroll-view>

  <!-- 上传弹窗 -->
  <u-popup
    v-model="showUploadModal"
    mode="center"
    :round="24"
    :closeOnClickOverlay="true"
    @close="closeUploadModal"
    width="90%"
    :safe-area-inset-bottom="true"
  >
    <view class="upload-modal">
      <view class="modal-header">
        <text class="modal-title">上传文件</text>
        <button class="modal-close" @click="closeUploadModal">✕</button>
      </view>
      <view class="modal-body">
        <view v-if="uploadFileList.length === 0" class="upload-placeholder" @click="selectFiles">
          <view class="upload-icon">
            <text class="upload-icon-text">+</text>
          </view>
          <text class="upload-placeholder-text">点击选择文件</text>
        </view>
        <view v-else class="upload-files">
          <view v-for="(file, index) in uploadFileList" :key="index" class="upload-file-item">
            <text class="upload-file-icon">{{ getFileTypeIcon(file.name) }}</text>
            <view class="upload-file-info">
              <text class="upload-file-name">{{ file.name }}</text>
              <text class="upload-file-size">{{ formatFileSize(file.size) }}</text>
            </view>
            <button class="upload-file-remove" @click="removeFile(index)">✕</button>
          </view>
        </view>
      </view>
      <view class="modal-footer">
        <text class="upload-stats">已选择 {{ uploadFileList.length }} / 10 个文件</text>
        <view class="modal-actions">
          <button class="footer-btn btn-cancel" @click="closeUploadModal">取消</button>
          <button 
            class="footer-btn btn-upload" 
            :disabled="uploadFileList.length === 0 || uploading"
            @click="handleUpload"
          >
            <text class="btn-text">{{ uploading ? '上传中...' : '上传' }}</text>
          </button>
        </view>
      </view>
    </view>
  </u-popup>

  <!-- 预览弹窗 -->
  <u-popup
    v-model="showPreviewModal"
    mode="center"
    :round="20"
    :closeOnClickOverlay="true"
    @close="closePreviewModal"
    width="92%"
    :safe-area-inset-bottom="true"
  >
    <view class="preview-modal">
      <view class="preview-header">
        <text class="preview-title">{{ currentPreviewFile?.fileName }}</text>
        <button class="preview-close" @click="closePreviewModal">✕</button>
      </view>
      <view class="preview-body">
        <image
          v-if="previewUrl"
          class="preview-image"
          :src="previewUrl"
          mode="widthFix"
          @click="closePreviewModal"
        />
        <view v-else class="preview-no-preview">
          <text class="no-preview-icon">•</text>
          <text class="no-preview-text">该文件类型不支持预览</text>
        </view>
      </view>
      <view class="preview-footer">
        <view class="preview-info">
          <text class="info-label">大小</text>
          <text class="info-value">{{ currentPreviewFile ? formatFileSize(currentPreviewFile.fileSize) : '-' }}</text>
        </view>
        <button class="preview-action" @click="downloadCurrent">
          <text class="action-icon">↓</text>
          <text class="action-text">下载</text>
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import {
  getFileList,
  upload,
  deleteFile,
  type FileInfo,
  type FileListParams
} from '@/api/file'

defineOptions({ name: 'file-list' })

// 搜索和筛选
const searchForm = reactive<FileListParams>({
  fileName: '',
  fileType: ''
})

const fileTypeOptions = [
  { label: '全部', value: '' },
  { label: '图片', value: 'image' },
  { label: '文档', value: 'document' },
  { label: '视频', value: 'video' },
  { label: '音频', value: 'audio' },
  { label: '压缩包', value: 'archive' },
  { label: '其他', value: 'other' }
]

const selectedFileTypeLabel = ref('全部')

// 数据状态
const loading = ref(false)
const fileList = ref<FileInfo[]>([])
const pagination = reactive({
  pageIndex: 1,
  pageSize: 15,
  total: 0
})

// 滚动控制
const scrollToId = ref('')
const scrollToAlign = ref('nearest')

// 总页数
const totalPages = computed(() => {
  const total = pagination.total
  const pageSize = pagination.pageSize
  const pages = Math.ceil(total / pageSize)
  return Math.max(1, pages)
})

// 当前页
const currentPage = computed(() => pagination.pageIndex)

// 上传相关
const showUploadModal = ref(false)
const uploadFileList = ref<any[]>([])
const uploading = ref(false)

// 预览相关
const showPreviewModal = ref(false)
const previewUrl = ref('')
const currentPreviewFile = ref<FileInfo | null>(null)

// 统计信息
const getStats = () => {
  const total = fileList.value.reduce((sum, file) => sum + file.fileSize, 0)
  return {
    totalSize: formatFileSize(total)
  }
}

// 获取文件列表
const fetchFiles = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageIndex: pagination.pageIndex,
      pageSize: pagination.pageSize
    }
    const res = await getFileList(params)
    
    fileList.value = res.records || []
    pagination.total = res.total
    
    // 滚动到列表顶部
    nextTick(() => {
      scrollToId.value = `list-page-${pagination.pageIndex}`
    })
  } catch (error) {
    console.error('获取文件列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const onSearch = () => {
  pagination.pageIndex = 1
  fetchFiles()
}

// 清空搜索
const clearSearch = () => {
  searchForm.fileName = ''
  pagination.pageIndex = 1
  fetchFiles()
}

// 文件类型筛选
const onFileTypeChange = (e: any) => {
  const index = e.detail.value
  selectedFileTypeLabel.value = fileTypeOptions[index].label
  searchForm.fileType = fileTypeOptions[index].value
  pagination.pageIndex = 1
  fetchFiles()
}

// 分页
const onPageChange = (page: number) => {
  if (page < 1 || page > totalPages.value || page === currentPage.value) {
    return
  }
  
  pagination.pageIndex = page
  fetchFiles()
}

// 上传
const onUpload = () => {
  uploadFileList.value = []
  showUploadModal.value = true
}

const closeUploadModal = () => {
  showUploadModal.value = false
  uploadFileList.value = []
}

// 选择文件
const selectFiles = () => {
  uni.chooseFile({
    count: 10,
    success: (res) => {
      uploadFileList.value.push(...res.tempFilePaths.map((path: string, index: number) => ({
        path,
        name: res.tempFiles[index].name,
        size: res.tempFiles[index].size
      })))
    }
  })
}

// 移除文件
const removeFile = (index: number) => {
  uploadFileList.value.splice(index, 1)
}

// 开始上传
const handleUpload = async () => {
  if (uploadFileList.value.length === 0) {
    uni.showToast({ title: '请选择要上传的文件', icon: 'none' })
    return
  }
  
  uploading.value = true
  
  for (const file of uploadFileList.value) {
    try {
      await upload(file.path, '')
    } catch (error) {
      console.error('上传文件失败:', error)
    }
  }
  
  uploading.value = false
  showUploadModal.value = false
  uploadFileList.value = []
  fetchFiles()
  uni.showToast({ title: '上传完成', icon: 'success' })
}

// 预览
const onPreview = (file: FileInfo) => {
  if (file.fileType === 'image') {
    currentPreviewFile.value = file
    previewUrl.value = `/api/file/preview?id=${file.id}`
    showPreviewModal.value = true
  }
}

const closePreviewModal = () => {
  showPreviewModal.value = false
  previewUrl.value = ''
  currentPreviewFile.value = null
}

const downloadCurrent = () => {
  if (currentPreviewFile.value) {
    onDownload(currentPreviewFile.value)
  }
}

// 下载
const onDownload = (file: FileInfo) => {
  uni.showLoading({ title: '下载中...' })
  uni.downloadFile({
    url: `/api/file/download?id=${file.id}`,
    success: () => {
      uni.hideLoading()
      uni.showToast({ title: '下载成功', icon: 'success' })
    },
    fail: () => {
      uni.hideLoading()
      uni.showToast({ title: '下载失败', icon: 'none' })
    }
  })
}

// 显示操作菜单
const showActionMenu = (file: FileInfo) => {
  uni.showActionSheet({
    itemList: ['预览', '下载', '删除'],
    success: (res) => {
      switch (res.tapIndex) {
        case 0:
          onPreview(file)
          break
        case 1:
          onDownload(file)
          break
        case 2:
          onDelete(file)
          break
      }
    }
  })
}

// 删除
const onDelete = (file: FileInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除文件「${file.fileName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteFile(file.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchFiles()
        } catch (error) {
          console.error('删除文件失败:', error)
        }
      }
    }
  })
}

// 获取短文件类型
const getShortFileType = (fileType: string) => {
  const map: Record<string, string> = {
    image: 'IMG',
    document: 'DOC',
    video: 'VID',
    audio: 'AUD',
    archive: 'ZIP',
    other: 'FILE'
  }
  return map[fileType] || 'FILE'
}

// 获取文件类型图标
const getFileTypeIcon = (fileName: string) => {
  const ext = fileName.split('.').pop()?.toLowerCase()
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp']
  const docExts = ['doc', 'docx', 'pdf', 'txt', 'xls', 'xlsx', 'ppt', 'pptx']
  const videoExts = ['mp4', 'avi', 'mov', 'mkv', 'flv']
  const audioExts = ['mp3', 'wav', 'aac', 'flac', 'm4a']
  const archiveExts = ['zip', 'rar', '7z', 'tar', 'gz']
  
  if (imageExts.includes(ext)) return '🖼'
  if (docExts.includes(ext)) return '📄'
  if (videoExts.includes(ext)) return '🎥'
  if (audioExts.includes(ext)) return '🎵'
  if (archiveExts.includes(ext)) return '📦'
  return '📎'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(hours / 24)
  
  if (days > 30) {
    return dateStr
  }
  if (days > 0) {
    return `${days}天前`
  }
  if (hours > 0) {
    return `${hours}小时前`
  }
  
  const minutes = Math.floor(diff / (1000 * 60))
  return `${minutes}分钟前`
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + sizes[i]
}

// 初始化
fetchFiles()
</script>

<style lang="scss" scoped>
.file-container {
  min-height: 100vh;
  background: #FFFFFF;
}

// Header
.header {
  background: #FFFFFF;
  padding: 32px 24px 20px;
  border-bottom: 1px solid #E5E5E5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.header-content {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 28px;
  font-weight: 700;
  color: #000000;
  letter-spacing: -0.5px;
  margin-bottom: 8px;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-label {
  font-size: 14px;
  color: #666666;
  font-weight: 400;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #000000;
}

// Filter Bar
.filter-bar {
  background: #FFFFFF;
  padding: 20px 24px;
  border-bottom: 1px solid #F0F0F0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-wrapper {
  flex: 1;
  position: relative;
}

.search-input {
  width: 100%;
  height: 48px;
  background: #F5F5F5;
  border: 1px solid #E5E5E5;
  border-radius: 8px;
  padding: 0 16px 0 44px;
  font-size: 15px;
  color: #000000;
  font-weight: 400;
  transition: all 0.2s;
}

.search-input:focus {
  background: #FFFFFF;
  border-color: #000000;
  outline: none;
}

.search-clear {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #999999;
  cursor: pointer;
}

.filter-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #F5F5F5;
  border: 1px solid #E5E5E5;
  padding: 12px 20px;
  border-radius: 8px;
  height: 48px;
  transition: all 0.2s;
  cursor: pointer;
}

.filter-chip.active {
  background: #000000;
  border-color: #000000;
}

.filter-chip.active .filter-label {
  color: #FFFFFF;
}

.filter-icon {
  font-size: 20px;
  color: #999999;
}

.filter-label {
  font-size: 15px;
  font-weight: 500;
  color: #666666;
}

// File List
.file-list-wrapper {
  padding: 20px 24px;
  background: #FAFAFA;
  min-height: calc(100vh - 400px);
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s;
  border: 1px solid #F0F0F0;
}

.file-item:active {
  background: #FAFAFA;
  border-color: #000000;
}

.file-item-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
  letter-spacing: -0.5px;
  flex-shrink: 0;
}

.type-image {
  background: #000000;
  color: #FFFFFF;
}

.type-document {
  background: #000000;
  color: #FFFFFF;
}

.type-video {
  background: #000000;
  color: #FFFFFF;
}

.type-audio {
  background: #000000;
  color: #FFFFFF;
}

.type-archive {
  background: #000000;
  color: #FFFFFF;
}

.type-other {
  background: #000000;
  color: #FFFFFF;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.file-name {
  font-size: 16px;
  font-weight: 600;
  color: #000000;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  font-size: 13px;
  color: #999999;
}

.file-item-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.file-size {
  font-size: 14px;
  font-weight: 500;
  color: #000000;
  background: #F5F5F5;
  padding: 8px 16px;
  border-radius: 8px;
}

.action-menu {
  cursor: pointer;
}

.action-icon {
  font-size: 20px;
  color: #999999;
  letter-spacing: -2px;
}

// Pagination
.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  background: #FFFFFF;
  border-top: 1px solid #E5E5E5;
}

.pagination-info {
  font-size: 14px;
  color: #666666;
  font-weight: 400;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-btn {
  padding: 12px 24px;
  background: #FFFFFF;
  border: 1px solid #000000;
  border-radius: 8px;
  color: #000000;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:active {
  background: #000000;
  color: #FFFFFF;
}

.pagination-btn.btn-disabled {
  background: #FAFAFA;
  border-color: #E5E5E5;
  color: #CCCCCC;
  cursor: not-allowed;
}

.btn-text {
  font-size: 15px;
}

// Empty State
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 20px;
  text-align: center;
  min-height: calc(100vh - 400px);
}

.empty-icon {
  width: 80px;
  height: 80px;
  border: 2px solid #000000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.empty-icon-text {
  font-size: 32px;
  color: #000000;
  font-weight: 300;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #000000;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 14px;
  color: #999999;
}

// Loading
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 20px;
  min-height: calc(100vh - 400px);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #E5E5E5;
  border-top-color: #000000;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 15px;
  color: #999999;
  margin-top: 20px;
}

// FAB
.fab-container {
  position: fixed;
  right: 32px;
  bottom: 32px;
  z-index: 100;
}

.fab-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.2s;
}

.fab-btn:active {
  transform: scale(0.9);
}

.fab-icon {
  font-size: 32px;
  color: #FFFFFF;
  font-weight: 300;
}

// Upload Modal
.upload-modal {
  background: #FFFFFF;
  border-radius: 24px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
  border-bottom: 1px solid #F0F0F0;
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
  color: #000000;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #F5F5F5;
  color: #999999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
}

.modal-body {
  max-height: 50vh;
  overflow-y: auto;
  padding: 24px 32px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  border: 2px dashed #000000;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-placeholder:active {
  background: #F5F5F5;
}

.upload-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.upload-icon-text {
  font-size: 40px;
  color: #FFFFFF;
  font-weight: 300;
}

.upload-placeholder-text {
  font-size: 16px;
  font-weight: 600;
  color: #000000;
}

.upload-files {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #F5F5F5;
  border-radius: 12px;
}

.upload-file-icon {
  font-size: 24px;
}

.upload-file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.upload-file-name {
  font-size: 15px;
  font-weight: 500;
  color: #000000;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-file-size {
  font-size: 13px;
  color: #999999;
}

.upload-file-remove {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #F5F5F5;
  color: #999999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
}

.upload-file-remove:active {
  background: #E5E5E5;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 24px 32px;
  background: #F5F5F5;
  flex-direction: column;
  gap: 16px;
}

.upload-stats {
  font-size: 14px;
  color: #666666;
  text-align: center;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.footer-btn {
  flex: 1;
  height: 48px;
  border-radius: 8px;
  border: none;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.footer-btn:active {
  transform: scale(0.98);
}

.btn-cancel {
  background: #FFFFFF;
  border: 1px solid #000000;
  color: #000000;
}

.btn-upload {
  background: #000000;
  color: #FFFFFF;
}

.btn-upload:disabled {
  background: #FAFAFA;
  border-color: #E5E5E5;
  color: #CCCCCC;
  cursor: not-allowed;
}

.btn-text {
  font-size: 15px;
}

// Preview Modal
.preview-modal {
  background: #FFFFFF;
  border-radius: 20px;
  overflow: hidden;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  background: #000000;
}

.preview-title {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #FFFFFF;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
}

.preview-body {
  padding: 32px;
  background: #FAFAFA;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

.preview-image {
  width: 100%;
  border-radius: 8px;
}

.preview-no-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
}

.no-preview-icon {
  font-size: 64px;
  color: #000000;
  opacity: 0.2;
  margin-bottom: 16px;
}

.no-preview-text {
  font-size: 15px;
  color: #999999;
}

.preview-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  background: #FFFFFF;
  border-top: 1px solid #F0F0F0;
}

.preview-info {
  display: flex;
  gap: 12px;
}

.info-label {
  font-size: 14px;
  color: #999999;
}

.info-value {
  font-size: 15px;
  font-weight: 500;
  color: #000000;
}

.preview-action {
  padding: 12px 32px;
  background: #000000;
  color: #FFFFFF;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-action:active {
  transform: scale(0.98);
}

.action-icon {
  font-size: 16px;
}

.action-text {
  font-size: 15px;
}
</style>
