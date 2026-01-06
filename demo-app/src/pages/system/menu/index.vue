<template>
  <scroll-view
    class="menu-container"
    scroll-y
    refresher-enabled
    :refresher-triggered="refreshing"
    @refresherrefresh="onRefresh"
  >
    <!-- 搜索和筛选 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">📋</text>
        <input
          v-model="searchForm.menuName"
          class="search-input"
          placeholder="搜索菜单名称"
          placeholder-class="search-placeholder"
          @input="fetchMenus"
        />
        <text v-if="searchForm.menuName" class="clear-icon" @click="clearSearch">✕</text>
      </view>
      <view class="filter-group">
        <picker mode="selector" :range="statusOptions" range-key="label" @change="onStatusChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedStatusLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
        <picker mode="selector" :range="hideOptions" range-key="label" @change="onHideChange">
          <view class="filter-picker">
            <text class="filter-text">{{ selectedHideLabel }}</text>
            <text class="filter-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 操作栏 -->
    <view class="action-bar">
      <button class="action-btn btn-expand" @click="toggleExpand">
        <text class="btn-icon">{{ allExpanded ? '▶' : '▼' }}</text>
        <text class="btn-text">{{ allExpanded ? '收起' : '展开' }}</text>
      </button>
    </view>

    <!-- 菜单树 -->
    <view class="tree-container">
      <view v-if="!loading || menuTree.length > 0" class="tree-content">
        <template v-for="node in menuTree" :key="node.id">
          <view v-for="(menuNode, index) in flattenNode(node, 0)" :key="menuNode.id" class="menu-node">
            <view class="node-item" :style="{ paddingLeft: menuNode.level * 20 + 'px' }">
              <view class="node-header" @click="toggleNode(menuNode)">
                <text class="node-arrow" v-if="menuNode.children">{{ menuNode.expanded ? '▼' : '▶' }}</text>
                <text class="node-arrow-spacer" v-else>•</text>
                <text class="node-type">{{ getTypeText(menuNode.type) }}</text>
                <text class="node-name">{{ menuNode.title || menuNode.menuName }}</text>
                <view class="status-badges">
                  <view v-if="menuNode.status === 0" class="badge badge-disabled">禁用</view>
                  <view v-if="menuNode.isHide" class="badge badge-hidden">隐藏</view>
                  <view v-if="menuNode.isExternal" class="badge badge-external">外链</view>
                </view>
              </view>
            </view>
            <!-- 操作按钮 -->
            <view class="node-actions">
              <button
                class="node-action-btn btn-add-sub"
                v-if="menuNode.type !== 3"
                @click="onAddSub(menuNode)"
              >
                <text class="btn-small-text">新增</text>
              </button>
              <button
                class="node-action-btn btn-edit"
                @click="onEdit(menuNode)"
              >
                <text class="btn-small-text">编辑</text>
              </button>
              <button
                class="node-action-btn btn-delete"
                @click="onDelete(menuNode)"
              >
                <text class="btn-small-text">删除</text>
              </button>
            </view>
          </view>
        </template>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && menuTree.length === 0" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-title">暂无菜单</text>
        <text class="empty-desc">点击新增按钮创建第一个菜单</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <text class="loading-icon">⏳</text>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </scroll-view>

  <!-- 新增菜单按钮 -->
  <view class="add-btn" @click="onAddRoot">
    <text class="add-icon">+</text>
  </view>

  <!-- 新增/编辑菜单弹窗 -->
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
      <view class="form-section">
        <text class="section-title">基本信息</text>

        <view class="form-group">
          <text class="form-label">父级菜单</text>
          <picker mode="selector" :range="parentMenuOptions" range-key="label" @change="onParentChange">
            <view class="form-picker">
              <text class="picker-text">{{ selectedParentName || '根菜单' }}</text>
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">菜单类型 *</text>
          <picker mode="selector" :range="typeOptions" range-key="label" @change="onTypeChange">
            <view class="form-picker">
              <text class="picker-text">{{ selectedTypeName }}</text>
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">菜单图标</text>
          <input
            v-model="form.svgIcon"
            class="form-input"
            placeholder="请输入菜单图标"
          />
        </view>

        <view class="form-group">
          <text class="form-label">菜单名称 *</text>
          <input
            v-model="form.menuName"
            class="form-input"
            placeholder="请输入菜单名称"
            @input="onMenuNameInput"
          />
        </view>
      </view>

      <view class="form-section">
        <text class="section-title">路由与组件</text>

        <view class="form-group">
          <text class="form-label">路由路径</text>
          <input
            v-model="form.path"
            class="form-input"
            placeholder="请输入路由路径"
            @input="onPathInput"
          />
        </view>

        <view class="form-group">
          <text class="form-label">组件路径</text>
          <input
            v-model="form.component"
            class="form-input"
            placeholder="请输入组件路径"
          />
        </view>

        <view class="form-group">
          <text class="form-label">权限标识</text>
          <input
            v-model="form.permission"
            class="form-input"
            placeholder="请输入权限标识"
          />
        </view>

        <view class="form-group">
          <text class="form-label">排序</text>
          <input
            v-model="form.sort"
            class="form-input"
            type="number"
            placeholder="请输入排序"
          />
        </view>
      </view>

      <view class="form-section">
        <text class="section-title">其他设置</text>

        <view class="form-group">
          <text class="form-label">状态</text>
          <radio-group @change="onStatusRadioChange">
            <label class="radio-item">
              <radio :value="1" :checked="form.status === 1" color="#F59E0B" />
              <text class="radio-label">启用</text>
            </label>
            <label class="radio-item">
              <radio :value="0" :checked="form.status === 0" color="#F59E0B" />
              <text class="radio-label">禁用</text>
            </label>
          </radio-group>
        </view>

        <view class="form-group">
          <text class="form-label">是否隐藏</text>
          <radio-group @change="onHideRadioChange">
            <label class="radio-item">
              <radio :value="false" :checked="!form.isHide" color="#F59E0B" />
              <text class="radio-label">否</text>
            </label>
            <label class="radio-item">
              <radio :value="true" :checked="form.isHide" color="#F59E0B" />
              <text class="radio-label">是</text>
            </label>
          </radio-group>
        </view>

        <view class="form-group">
          <text class="form-label">是否外链</text>
          <radio-group @change="onExternalRadioChange">
            <label class="radio-item">
              <radio :value="false" :checked="!form.isExternal" color="#F59E0B" />
              <text class="radio-label">否</text>
            </label>
            <label class="radio-item">
              <radio :value="true" :checked="form.isExternal" color="#F59E0B" />
              <text class="radio-label">是</text>
            </label>
          </radio-group>
        </view>

        <view v-if="form.isExternal" class="form-group">
          <text class="form-label">外链地址</text>
          <input
            v-model="form.link"
            class="form-input"
            placeholder="请输入外链地址"
          />
        </view>

        <view v-if="form.isExternal" class="form-group">
          <text class="form-label">是否iframe</text>
          <radio-group @change="onIframeRadioChange">
            <label class="radio-item">
              <radio :value="false" :checked="!form.iframe" color="#F59E0B" />
              <text class="radio-label">否</text>
            </label>
            <label class="radio-item">
              <radio :value="true" :checked="form.iframe" color="#F59E0B" />
              <text class="radio-label">是</text>
            </label>
          </radio-group>
        </view>

        <view v-if="form.isFull" class="form-group">
          <text class="form-label">是否全屏</text>
          <radio-group @change="onFullRadioChange">
            <label class="radio-item">
              <radio :value="false" :checked="!form.isFull" color="#F59E0B" />
              <text class="radio-label">否</text>
            </label>
            <label class="radio-item">
              <radio :value="true" :checked="form.isFull" color="#F59E0B" />
              <text class="radio-label">是</text>
            </label>
          </radio-group>
        </view>

        <view class="form-group">
          <text class="form-label">是否侧边栏</text>
          <input
            v-model="form.affix"
            class="form-input"
            type="number"
            placeholder="请输入侧边栏位置"
          />
        </view>
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
  getMenuTreeAll,
  addMenu,
  updateMenu,
  deleteMenu,
  type MenuInfo,
  type MenuQueryParams
} from '@/api/system/menu'

defineOptions({ name: 'menu-list' })

// 搜索和筛选
const searchForm = reactive<MenuQueryParams>({
  menuName: '',
  status: undefined,
  hide: undefined
})

const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const hideOptions = [
  { label: '全部显示', value: undefined },
  { label: '显示', value: 0 },
  { label: '隐藏', value: 1 }
]

const selectedStatusLabel = ref('全部状态')
const selectedHideLabel = ref('全部显示')

// 数据状态
const loading = ref(false)
const refreshing = ref(false)
const menuTree = ref<MenuInfo[]>([])
const allExpanded = ref(false)

// 表单弹窗
const showModal = ref(false)
const modalTitle = ref('')
const formType = ref(0) // 0-新增根菜单, 1-编辑, 2-新增子菜单
const form = reactive<Partial<MenuInfo>>({
  type: 1,
  parentId: '',
  svgIcon: '',
  icon: '',
  menuName: '',
  title: '',
  isFull: false,
  permission: '',
  path: '',
  component: '',
  isHide: false,
  status: 1,
  affix: 0,
  isExternal: false,
  link: '',
  iframe: 0,
  sort: 1
})

// 选项
const typeOptions = [
  { label: '目录', value: 1 },
  { label: '菜单', value: 2 },
  { label: '按钮', value: 3 }
]

const selectedTypeName = ref('目录')
const parentMenuOptions = ref<any[]>([])
const selectedParentName = ref('')

// 获取菜单列表
const fetchMenus = async (isRefresh = false) => {
  if (isRefresh) {
    refreshing.value = true
  }
  loading.value = true

  try {
    const res = await getMenuTreeAll(searchForm)

    // 初始化expanded状态
    const initTree = (nodes: MenuInfo[]) => {
      nodes.forEach(node => {
        node.expanded = false
        node.level = 0
        if (node.children) {
          initTree(node.children)
        }
      })
    }
    initTree(res)

    menuTree.value = res

    // 构建父级菜单选项
    buildParentOptions(res)
  } catch (error) {
    console.error('获取菜单列表失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 构建父级菜单选项
const buildParentOptions = (nodes: MenuInfo[], prefix = '') => {
  nodes.forEach(node => {
    parentMenuOptions.value.push({
      label: prefix + (node.title || node.menuName),
      value: node.id
    })
    if (node.children) {
      buildParentOptions(node.children, prefix + (node.title || node.menuName) + ' / ')
    }
  })
}

// 下拉刷新
const onRefresh = () => {
  fetchMenus(true)
}

// 清空搜索
const clearSearch = () => {
  searchForm.menuName = ''
  fetchMenus(true)
}

// 状态筛选
const onStatusChange = (e: any) => {
  const index = e.detail.value
  selectedStatusLabel.value = statusOptions[index].label
  searchForm.status = statusOptions[index].value
  fetchMenus(true)
}

// 隐藏筛选
const onHideChange = (e: any) => {
  const index = e.detail.value
  selectedHideLabel.value = hideOptions[index].label
  searchForm.hide = hideOptions[index].value
  fetchMenus(true)
}

// 获取类型文本
const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '目录',
    2: '菜单',
    3: '按钮'
  }
  return typeMap[type] || ''
}

// 展开收起
const toggleNode = (node: MenuInfo) => {
  if (node.children) {
    node.expanded = !node.expanded
  }
}

const toggleExpand = () => {
  allExpanded.value = !allExpanded.value
  const expandNodes = (nodes: MenuInfo[]) => {
    nodes.forEach(node => {
      node.expanded = allExpanded.value
      if (node.children) {
        expandNodes(node.children)
      }
    })
  }
  expandNodes(menuTree.value)
}

// 扁平化节点
const flattenNode = (node: MenuInfo, level: number): MenuInfo[] => {
  node.level = level
  const result: MenuInfo[] = [node]
  if (node.children && node.expanded) {
    node.children.forEach(child => {
      result.push(...flattenNode(child, level + 1))
    })
  }
  return result
}

// 新增根菜单
const onAddRoot = () => {
  modalTitle.value = '新增根菜单'
  formType.value = 0
  resetForm()
  form.parentId = ''
  showModal.value = true
}

// 新增子菜单
const onAddSub = (parentNode: MenuInfo) => {
  modalTitle.value = '新增子菜单'
  formType.value = 2
  resetForm()
  form.parentId = parentNode.id
  form.type = parentNode.type === 1 ? 2 : 3
  selectedTypeName.value = typeOptions.find(t => t.value === form.type)?.label || ''
  showModal.value = true
}

// 编辑菜单
const onEdit = (menu: MenuInfo) => {
  modalTitle.value = '编辑菜单'
  formType.value = 1
  Object.assign(form, { ...menu })
  if (form.parentId === 0) form.parentId = ''
  selectedTypeName.value = typeOptions.find(t => t.value === form.type)?.label || ''
  showModal.value = true
}

// 关闭弹窗
const closeModal = () => {
  showModal.value = false
}

// 父级选择
const onParentChange = (e: any) => {
  const index = e.detail.value
  const parent = parentMenuOptions.value[index]
  form.parentId = parent.value
  selectedParentName.value = parent.label
}

// 类型选择
const onTypeChange = (e: any) => {
  const index = e.detail.value
  const type = typeOptions[index]
  form.type = type.value
  selectedTypeName.value = type.label
}

// 菜单名称输入
const onMenuNameInput = () => {
  // 自动生成路由名称
  if (form.path && !form.menuName) {
    form.menuName = form.path.trim()
      .replace(/[.:\/?=&"-]/g, '_')
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join('')
  }
}

// 路由输入
const onPathInput = () => {
  // 自动生成菜单名称
  if (form.menuName && !form.path) {
    form.menuName = form.path.trim()
      .replace(/[.:\/?=&"-]/g, '_')
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join('')
  }
}

// 状态选择
const onStatusRadioChange = (e: any) => {
  form.status = e.detail.value
}

// 隐藏选择
const onHideRadioChange = (e: any) => {
  form.isHide = e.detail.value === 'true'
}

// 外链选择
const onExternalRadioChange = (e: any) => {
  form.isExternal = e.detail.value === 'true'
  if (!form.isExternal) {
    form.link = ''
    form.iframe = 0
  }
}

// iframe选择
const onIframeRadioChange = (e: any) => {
  form.iframe = e.detail.value === 'true' ? 1 : 0
}

// 全屏选择
const onFullRadioChange = (e: any) => {
  form.isFull = e.detail.value === 'true'
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    type: 1,
    parentId: '',
    svgIcon: '',
    icon: '',
    menuName: '',
    title: '',
    isFull: false,
    permission: '',
    path: '',
    component: '',
    isHide: false,
    status: 1,
    affix: 0,
    isExternal: false,
    link: '',
    iframe: 0,
    sort: 1
  })
  selectedTypeName.value = '目录'
  selectedParentName.value = ''
  parentMenuOptions.value = []
}

// 保存菜单
const handleOk = async () => {
  if (!form.menuName) {
    uni.showToast({ title: '请输入菜单名称', icon: 'none' })
    return
  }

  try {
    if (formType.value === 0 || formType.value === 2) {
      await addMenu(form)
      uni.showToast({ title: '新增成功', icon: 'success' })
    } else {
      await updateMenu(form)
      uni.showToast({ title: '更新成功', icon: 'success' })
    }
    showModal.value = false
    fetchMenus(true)
  } catch (error) {
    console.error('保存菜单失败:', error)
  }
}

// 删除菜单
const onDelete = (menu: MenuInfo) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除菜单「${menu.title || menu.menuName}」吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteMenu(menu.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchMenus(true)
        } catch (error) {
          console.error('删除菜单失败:', error)
        }
      }
    }
  })
}

// 初始化
fetchMenus(true)
</script>

<style lang="scss" scoped>
.menu-container {
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

// 操作栏
.action-bar {
  background: #ffffff;
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1px solid #E2E8F0;
}

.action-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #F59E0B, #FBBF24);
  color: #ffffff;
  border-radius: 8px;
  border: none;
  font-size: 14px;
}

.btn-icon {
  font-size: 14px;
}

.btn-text {
  font-size: 14px;
}

// 树容器
.tree-container {
  padding: 12px;
}

.tree-content {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.menu-node {
  display: flex;
  flex-direction: column;
}

.node-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.node-header {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 10px 12px;
  background: #F8FAFC;
  border-radius: 8px;
  transition: all 0.2s;
}

.node-header:active {
  background: #F1F5F9;
}

.node-arrow {
  font-size: 10px;
  color: #94A3B8;
  width: 12px;
  text-align: center;
}

.node-arrow-spacer {
  font-size: 10px;
  color: #94A3B8;
  width: 12px;
  text-align: center;
}

.node-type {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.type-directory {
  background: #8B5CF6;
  color: #ffffff;
}

.type-menu {
  background: #10B981;
  color: #ffffff;
}

.type-button {
  background: #64748B;
  color: #ffffff;
}

.node-name {
  font-size: 15px;
  color: #334155;
  font-weight: 500;
}

.status-badges {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.badge-disabled {
  background: #FEE2E2;
  color: #DC2626;
}

.badge-hidden {
  background: #F3E8FF;
  color: #6366F1;
}

.badge-external {
  background: #DBEAFE;
  color: #1E40AF;
}

.node-actions {
  display: flex;
  gap: 6px;
  margin-top: 6px;
}

.node-action-btn {
  padding: 4px 12px;
  border-radius: 6px;
  border: none;
  font-size: 12px;
}

.btn-small-icon {
  font-size: 14px;
  color: #10B981;
}

.btn-small-text {
  font-size: 12px;
}

.btn-add-sub {
  background: #10B981;
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
  background: linear-gradient(135deg, #F59E0B, #FBBF24);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
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

// 弹窗样式
.modal-content {
  max-height: 70vh;
  padding: 16px 20px;
}

.form-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #F1F5F9;
}

.form-section:last-child {
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.section-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 12px;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
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

.picker-text {
  font-size: 14px;
  color: #334155;
}

.picker-arrow {
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
  background: linear-gradient(135deg, #F59E0B, #FBBF24);
  color: #ffffff;
}
</style>
