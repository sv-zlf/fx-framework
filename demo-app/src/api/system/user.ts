import { http } from '@/http/http'

/**
 * 用户列表参数
 */
export interface UserListParams {
  pageIndex?: number
  pageSize?: number
  name?: string
  phone?: string
  status?: number
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  userName: string
  nickName: string
  email: string
  phone: string
  status: number // 0=禁用, 1=启用
  admin?: boolean
  createTime?: string
  roles?: number[]
}

/**
 * 用户列表响应
 */
export interface UserListResponse {
  records: UserInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 角色信息
 */
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description?: string
}

/**
 * 获取用户列表
 */
export function getUserList(params: UserListParams) {
  return http.get<UserListResponse>('/system/user/list', params)
}

/**
 * 保存或更新用户
 */
export function saveOrUpdate(data: Partial<UserInfo>) {
  return http.post('/system/user/saveOrUpdate', data)
}

/**
 * 删除用户
 */
export function deleteUser(userId: number) {
  return http.post('/system/user/delete', null, { userId })
}

/**
 * 批量删除用户
 */
export function deleteBatch(userIds: number[]) {
  return http.post('/system/user/deleteBatch', null, { userIds })
}

/**
 * 获取角色列表
 */
export function getRoleList(roleName: string = '') {
  return http.get<RoleInfo[]>('/system/role/getRoleList', { roleName })
}
