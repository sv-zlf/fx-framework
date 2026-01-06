<template>
  <scroll-view class="privileges-container" scroll-y>
    <!-- 头部 -->
    <view class="header">
      <text class="title">{{ roleName }}</text>
      <text class="subtitle">权限配置</text>
    </view>

    <!-- 操作栏 -->
    <view class="action-bar">
      <button class="action-bar-btn" @click="expandAll">
        <text class="btn-text">展开全部</text>
      </button>
      <button class="action-bar-btn" @click="collapseAll">
        <text class="btn-text">收起全部</text>
      </button>
      <button class="action-bar-btn" @click="selectAll">
        <text class="btn-text">全选</text>
      </button>
      <button class="action-bar-btn" @click="unselectAll">
        <text class="btn-text">取消全选</text>
      </button>
    </view>

    <!-- 权限树 -->
    <view class="tree-container">
      <view v-for="(node, index) in permissionTree" :key="node.id" class="tree-node">
        <view class="node-item" :style="{ paddingLeft: node.level * 20 + 'px' }">
          <view class="node-content" @click="toggleNode(node)">
            <text class="node-arrow">{{ node.expanded ? '▼' : '▶' }}</text>
            <checkbox
              :checked="node.isSelected"
              @change="(e) => onNodeCheck(node, e)"
              color="#8B5CF6"
            />
            <text class="node-title">{{ node.i18n || node.title }}</text>
          </view>
        </view>
        <!-- 子节点 -->
        <view v-if="node.expanded && node.children" class="node-children">
          <template v-for="child in node.children" :key="child.id">
            <view 
              v-for="(nestedNode, nestedIndex) in flattenNode(child, node.level + 1)" 
              :key="nestedNode.id" 
              class="tree-node"
            >
              <view class="node-item" :style="{ paddingLeft: nestedNode.level * 20 + 'px' }">
                <view class="node-content" @click="toggleNode(nestedNode)">
                  <text class="node-arrow" v-if="nestedNode.children">{{ nestedNode.expanded ? '▼' : '▶' }}</text>
                  <checkbox
                    :checked="nestedNode.isSelected"
                    @change="(e) => onNodeCheck(nestedNode, e)"
                    color="#8B5CF6"
                  />
                  <text class="node-title">{{ nestedNode.i18n || nestedNode.title }}</text>
                </view>
              </view>
            </template>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && permissionTree.length === 0" class="empty-state">
      <text class="empty-icon">🔐</text>
      <text class="empty-title">暂无权限</text>
      <text class="empty-desc">该角色没有可配置的权限</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-icon">⏳</text>
      <text class="loading-text">加载中...</text>
    </view>
  </scroll-view>

  <!-- 底部按钮 -->
  <view class="footer-actions">
    <button class="footer-btn btn-save" @click="handleSave">
      <text class="btn-icon">💾</text>
      <text class="btn-text">保存</text>
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
  toBind,
  bindMenu,
  type MenuTreeNode
} from '@/api/system/role'

defineOptions({ name: 'role-privileges' })

// 角色信息
const roleId = ref<number>()
const roleName = ref('')

// 权限树
const loading = ref(false)
const permissionTree = ref<MenuTreeNode[]>([])
const selectedMenuIds = ref<number[]>([])

// 展开节点
const toggleNode = (node: MenuTreeNode) => {
  if (node.children) {
    node.expanded = !node.expanded
  }
}

// 扁平化节点（用于递归渲染）
const flattenNode = (node: MenuTreeNode, level: number): MenuTreeNode[] => {
  node.level = level
  const result: MenuTreeNode[] = [node]
  if (node.children && node.expanded) {
    node.children.forEach(child => {
      result.push(...flattenNode(child, level + 1))
    })
  }
  return result
}

// 节点选中
const onNodeCheck = (node: MenuTreeNode, e: any) => {
  node.isSelected = e.detail.value
  // 更新子节点
  updateChildren(node, node.isSelected)
  // 更新父节点状态
  updateParent(node)
}

// 递归更新子节点状态
const updateChildren = (node: MenuTreeNode, checked: boolean) => {
  if (node.children) {
    node.children.forEach(child => {
      child.isSelected = checked
      updateChildren(child, checked)
    })
  }
}

// 递归更新父节点状态
const updateParent = (node: MenuTreeNode) => {
  const parent = findParent(permissionTree.value, node.id)
  if (parent) {
    const allChecked = parent.children?.every(child => child.isSelected)
    const someChecked = parent.children?.some(child => child.isSelected)
    parent.isSelected = allChecked
  }
}

// 查找父节点
const findParent = (nodes: MenuTreeNode[], nodeId: number, parent?: MenuTreeNode): MenuTreeNode | null => {
  for (const node of nodes) {
    if (node.id === nodeId) {
      return parent || null
    }
    if (node.children) {
      const found = findParent(node.children, nodeId, node)
      if (found) return found
    }
  }
  return null
}

// 提取选中的菜单ID
const extractCheckedIds = (nodes: MenuTreeNode[]): number[] => {
  const checkedIds: number[] = []
  
  const traverse = (nodeList: MenuTreeNode[]) => {
    nodeList.forEach(node => {
      if (node.isSelected === true) {
        checkedIds.push(node.id)
      }
      if (node.children) {
        traverse(node.children)
      }
    })
  }
  
  traverse(nodes)
  return checkedIds
}

// 展开全部
const expandAll = () => {
  const expandNodes = (nodes: MenuTreeNode[]) => {
    nodes.forEach(node => {
      node.expanded = true
      if (node.children) {
        expandNodes(node.children)
      }
    })
  }
  expandNodes(permissionTree.value)
}

// 收起全部
const collapseAll = () => {
  const collapseNodes = (nodes: MenuTreeNode[]) => {
    nodes.forEach(node => {
      node.expanded = false
      if (node.children) {
        collapseNodes(node.children)
      }
    })
  }
  collapseNodes(permissionTree.value)
}

// 全选
const selectAll = () => {
  const selectNodes = (nodes: MenuTreeNode[]) => {
    nodes.forEach(node => {
      node.isSelected = true
      if (node.children) {
        selectNodes(node.children)
      }
    })
  }
  selectNodes(permissionTree.value)
}

// 取消全选
const unselectAll = () => {
  const unselectNodes = (nodes: MenuTreeNode[]) => {
    nodes.forEach(node => {
      node.isSelected = false
      if (node.children) {
        unselectNodes(node.children)
      }
    })
  }
  unselectNodes(permissionTree.value)
}

// 保存
const handleSave = async () => {
  const checkedIds = extractCheckedIds(permissionTree.value)
  if (checkedIds.length === 0) {
    uni.showToast({ title: '请至少选择一个权限', icon: 'none' })
    return
  }

  try {
    await bindMenu(roleId.value, checkedIds)
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存权限失败:', error)
  }
}

// 返回
const handleBack = () => {
  uni.navigateBack()
}

// 获取权限树
const fetchPermissions = async () => {
  loading.value = true
  try {
    const { data } = await toBind(roleId.value)
    // 初始化expanded状态
    const initTree = (nodes: MenuTreeNode[]) => {
      nodes.forEach(node => {
        node.expanded = false
        node.level = 0
        if (node.children) {
          initTree(node.children)
        }
      })
    }
    initTree(data)
    permissionTree.value = data
  } catch (error) {
    console.error('获取权限树失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage.options
  
  roleId.value = Number(options.roleId)
  roleName.value = options.roleName || ''
  
  fetchPermissions()
})
</script>

<style lang="scss" scoped>
.privileges-container {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 80px;
}

// 头部
.header {
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
  padding: 24px 20px;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
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

// 操作栏
.action-bar {
  background: #ffffff;
  padding: 12px 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  border-bottom: 1px solid #E2E8F0;
}

.action-bar-btn {
  padding: 8px 16px;
  background: #F1F5F9;
  color: #475569;
  border-radius: 8px;
  border: none;
  font-size: 14px;
}

.btn-text {
  font-size: 14px;
}

// 树容器
.tree-container {
  background: #ffffff;
  margin: 12px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.tree-node {
  display: flex;
  flex-direction: column;
}

.node-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-arrow {
  font-size: 10px;
  color: #94A3B8;
  width: 12px;
  text-align: center;
}

.node-title {
  font-size: 15px;
  color: #334155;
  font-weight: 500;
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
  background: linear-gradient(135deg, #8B5CF6, #A78BFA);
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
