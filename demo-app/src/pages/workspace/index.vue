<script lang="ts" setup>
import { ref, computed } from 'vue'
import { getRoutersAPI } from '@/api/menu'

definePage({
  style: {
    navigationStyle: "custom",
    navigationBarTitleText: "工作台",
  },
})

// 菜单数据
const loading = ref(false)
const error = ref<string | null>(null)
const modules = ref<any[]>([])
const refreshing = ref(false)

// 模块展开状态
const expandedModules = ref<Set<string>>(new Set())

// 扁平化树形结构
function flattenTree(tree: any[]): any[] {
  const result: any[] = []
  const stack = [...tree]
  while (stack.length) {
    const node = stack.pop()!
    // 如果节点的title为"组件示例"，则跳过该节点及其所有子节点
    if (node.title === '组件示例') {
      continue
    }
    if (node.isExternal) continue
    if (node.status === 0 || node.isHide) continue
    result.push(node)
    if (node.children && node.children.length) {
      for (let i = node.children.length - 1; i >= 0; i--) {
        stack.push(node.children[i])
      }
    }
  }
  return result
}

// 获取菜单数据
const fetchWorkbenchMenu = async () => {
  loading.value = true
  error.value = null

  try {
    const data = await getRoutersAPI()
    const flatMenus = flattenTree(data)

    // 筛选 type=2 的目录（作为模块）
    const directories = flatMenus
      .filter((item: any) => item.type === 1)
      .sort((a: any, b: any) => a.sort - b.sort)

    console.log('directories:', directories)
    // 筛选 type=2 的功能菜单
    const functions = flatMenus
      .filter((item: any) => item.type === 2)
      .sort((a: any, b: any) => a.sort - b.sort)

    // 将功能菜单按所属目录分组
    const moduleMap = new Map<string, any[]>()
    functions.forEach((func: any) => {
      const parent = directories.find((dir: any) => func.parentId === dir.id)
      if (parent) {
        if (!moduleMap.has(parent.id)) {
          moduleMap.set(parent.id, [])
        }
        moduleMap.get(parent.id)!.push({
          id: func.id,
          name: func.name,
          title: func.title,
          path: func.path,
          component: func.component,
          icon: func.icon,
          svgIcon: func.svgIcon,
          sort: func.sort
        })
      }
    })

    // 构建最终的模块数据
    modules.value = directories.map((dir: any) => ({
      id: dir.id,
      name: dir.name,
      title: dir.title,
      icon: dir.icon,
      svgIcon: dir.svgIcon,
      sort: dir.sort,
      functions: (moduleMap.get(dir.id) || []).sort((a, b) => a.sort - b.sort)
    }))
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载菜单数据失败'
    console.error('获取工作台菜单失败:', err)
  } finally {
    loading.value = false
  }
}

// 快捷访问功能
const quickAccessFunctions = computed(() => {
  const allFunctions: any[] = []
  modules.value.forEach(module => {
    module.functions.forEach((func: any) => {
      allFunctions.push({ ...func, moduleName: module.title })
    })
  })
  // 随机打乱数组并取前4个
  const shuffled = [...allFunctions].sort(() => Math.random() - 0.5)
  return shuffled.slice(0, 4)
})

// 下拉刷新
const onRefresh = async () => {
  refreshing.value = true
  await fetchWorkbenchMenu()
  setTimeout(() => {
    refreshing.value = false
    uni.stopPullDownRefresh()
  }, 500)
}

// 点击快捷功能
const handleQuickAction = (item: any) => {
  if (item.component) {
    uni.navigateTo({
      url: '/pages/'+item.component,
      fail: () => {
        uni.showToast({
          title: "页面开发中",
          icon: "none",
        })
      }
    })
  } else {
    uni.showToast({
      title: "该功能暂未配置路由",
      icon: "none",
    })
  }
}

// 切换模块展开状态
const handleModuleClick = (module: any) => {
  if (expandedModules.value.has(module.id)) {
    expandedModules.value.delete(module.id)
  } else {
    expandedModules.value.add(module.id)
  }
}

// 判断模块是否展开
const isModuleExpanded = (moduleId: string) => {
  return expandedModules.value.has(moduleId)
}

// 点击功能
const handleFunctionClick = (func: any) => {
  console.log(func)
  if (func.component) {
    uni.navigateTo({
      url: '/pages/'+func.component,
      fail: () => {
        uni.showToast({
          title: "页面开发中",
          icon: "none",
        })
      }
    })
  } else {
    uni.showToast({
      title: "该功能暂未配置路由",
      icon: "none",
    })
  }
}

// 获取图标名称（优先使用 svgIcon）
const getIconUrl = (item: any) => {
  if (item.svgIcon) {
    // svg 图标路径
    return `/static/my-icons/${item.svgIcon}.svg`
  }
  if (item.icon) {
    if (item.icon =='IconAlignCenter'){
      return '/static/my-icons/set.svg'
    }
    // 普通 icon 路径
    return `/static/my-icons/${item.icon}.png`
  }
  // 默认图标
  return '/static/my-icons/copyright.svg'
}

// 获取颜色
const getColor = (index: number) => {
  const colors = ["#007AFF", "#00D26A", "#FFB800", "#6F42C1", "#FA2A2D", "#FF6B6B", "#4ECDC4", "#95E1D3"]
  return colors[index % colors.length]
}

// 页面加载时获取菜单数据
onMounted(() => {
  fetchWorkbenchMenu()
})

// 监听下拉刷新事件
onPullDownRefresh(() => {
  onRefresh()
})
</script>

<template>
  <scroll-view
    class="workspace-container"
    scroll-y
    refresher-enabled
    :refresher-triggered="refreshing"
    @refresherrefresh="onRefresh"
  >
    <!-- 错误提示 -->
    <view v-if="error" class="error-banner">
      <text>{{ error }}</text>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="section-header">
        <text class="section-title">快捷功能</text>
      </view>
      <view v-if="quickAccessFunctions.length > 0" class="action-grid">
        <view
          v-for="(item, index) in quickAccessFunctions"
          :key="item.id"
          class="action-item"
          @click="handleQuickAction(item)"
        >
          <view class="action-icon" >
            <image
              :src="getIconUrl(item)"
              class="icon-image"
              mode="aspectFit"
            />
          </view>
          <text class="action-title">{{ item.title }}</text>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无快捷功能</text>
      </view>
    </view>

    <!-- 工作台模块 -->
    <view v-if="!loading" class="modules-section">
      <view class="section-header">
        <text class="section-title">我的工作台</text>
      </view>
      <view v-if="modules.length > 0" class="modules-list">
        <view
          v-for="module in modules"
          :key="module.id"
          class="module-card"
          @click="handleModuleClick(module)"
        >
          <view class="module-header">
            <view class="module-title">
<!--              <text :class="getIconClass(module)" class="module-icon"></text>-->
              <text>{{ module.title }}</text>
            </view>
            <view class="module-count">
              <text>{{ module.functions.length }}</text>
            </view>
          </view>
          <view v-if="module.functions.length > 0" class="module-functions">
            <view
              v-for="func in (isModuleExpanded(module.id) ? module.functions : module.functions.slice(0, 4))"
              :key="func.id"
              class="function-item"
              @click.stop="handleFunctionClick(func)"
            >
              <view class="function-icon">
                <image
                  :src="getIconUrl(func)"
                  mode="aspectFit"
                />
              </view>

<!--              <text :class="getIconClass(func)" class="function-icon"></text>-->
              <text class="function-name">{{ func.title }}</text>
            </view>
            <view
              v-if="module.functions.length > 4"
              class="function-more"
              @click.stop="handleModuleClick(module)"
            >
              <text v-if="!isModuleExpanded(module.id)">+{{ module.functions.length - 4 }} 更多</text>
              <text v-else>收起</text>
            </view>
          </view>
          <view v-else class="module-empty">
            <text class="empty-icon">📭</text>
            <text class="empty-text">暂无功能</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-icon">🏢</text>
        <text class="empty-text">暂无可用模块</text>
        <view class="empty-action">
          <text @click="fetchWorkbenchMenu">重新加载</text>
        </view>
      </view>
    </view><view v-if="loading" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
.workspace-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: calc(var(--status-bar-height) + 12px);
  padding-bottom: calc(var(--tab-bar-height) + 20px);
}

.error-banner {
  background: #ffe6e6;
  color: #d32f2f;
  padding: 12px 20px;
  font-size: 14px;
  text-align: center;
}

.quick-actions {
  background: #ffffff;
  margin: 12px;
  padding: 20px;
  border-radius: 12px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 375px) {
  .action-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 12px;
  border-radius: 12px;
  transition: all 0.3s;
}

.action-item:active {
  opacity: 0.7;
  transform: scale(0.95);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  //box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-icon text {
  font-size: 28px;
  color: #ffffff;
}

.action-title {
  font-size: 13px;
  color: #333333;
  text-align: center;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-all;
}

.modules-section {
  padding: 0 12px;
}

.modules-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.module-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.module-card:active {
  transform: scale(0.98);
}

.module-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.module-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333333;
}

.module-icon {
  font-size: 20px;
  color: #007AFF;
}

.module-count {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 13px;
  color: #666666;
}

.module-functions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.function-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.2s;
}

.function-item:active {
  background: #e9ecef;
  transform: translateX(4px);
}

.function-icon {
  //font-size: 18px;
  //color: #6c757d;
  width: 18px;
  height:18px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  //margin-bottom: 8px;
}

.function-name {
  flex: 1;
  font-size: 14px;
  color: #495057;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: nowrap;
}

.function-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #007AFF;
  font-size: 13px;
}

.module-empty, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 12px;
}

.empty-icon {
  font-size: 48px;
}

.empty-text {
  font-size: 14px;
  color: #999999;
}

.empty-action {
  margin-top: 8px;
}

.empty-action text {
  padding: 8px 24px;
  background: #007AFF;
  color: #ffffff;
  border-radius: 20px;
  font-size: 14px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}

.loading-icon {
  font-size: 48px;
  animation: rotate 1s linear infinite;
}

.loading-text {
  font-size: 14px;
  color: #999999;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}





</style>
