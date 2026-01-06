import { http } from '@/http/http'

/**
 * 角色列表参数
 */
export interface RoleListParams {
  pageIndex?: number
  pageSize?: number
  roleName?: string
  roleCode?: string
  status?: number
}

/**
 * 角色信息
 */
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description: string
  status: number // 0=禁用, 1=启用
  admin?: boolean
  createTime?: string
}

/**
 * 角色列表响应
 */
export interface RoleListResponse {
  records: RoleInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 菜单权限节点
 */
export interface MenuTreeNode {
  id: number
  title: string
  i18n?: string
  children?: MenuTreeNode[]
  isSelected?: boolean
}

/**
 * 获取角色分页列表
 */
export function getRolePageList(params: RoleListParams) {
  return http.get<RoleListResponse>('/system/role/list', params)
}

/**
 * 获取角色列表（无分页）
 */
export function getRoleList(roleName: string = '') {
  return http.get<RoleInfo[]>('/system/role/getRoleList', { roleName })
}

/**
 * 新增角色
 */
export function addRole(data: Partial<RoleInfo>) {
  return http.post('/system/role/insert', data)
}

/**
 * 更新角色
 */
export function updateRole(data: Partial<RoleInfo>) {
  return http.post('/system/role/update', data)
}

/**
 * 删除角色
 */
export function deleteRole(roleId: number) {
  return http.post('/system/role/delete', null, { roleId })
}

/**
 * 获取角色权限授权列表
 */
export function toBind(roleId: number) {
  return http.get<MenuTreeNode[]>('/system/roleMenu/toBind', { roleId })
}

/**
 * 绑定菜单权限
 */
export function bindMenu(roleId: number, menuIds: number[]) {
  return http.post('/system/roleMenu/bindMenu', null, { roleId, menuIds })
}
