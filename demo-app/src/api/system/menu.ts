import { http } from '@/http/http'

/**
 * 菜单查询参数
 */
export interface MenuQueryParams {
  menuName?: string
  status?: number
  hide?: number
}

/**
 * 菜单信息
 */
export interface MenuInfo {
  id: number
  type: number // 1=目录, 2=菜单, 3=按钮
  parentId: string | number
  svgIcon?: string
  icon?: string
  menuName: string
  title: string
  isFull?: boolean
  permission?: string
  path?: string
  component?: string
  isHide?: boolean
  status: number // 0=禁用, 1=启用
  affix?: number
  isExternal?: boolean
  link?: string
  iframe?: number
  sort: number
  children?: MenuInfo[]
}

/**
 * 获取菜单树（用于侧边栏）
 */
export function getRoutersAPI() {
  return http.get<MenuInfo[]>('/system/menu/getMenuTree')
}

/**
 * 获取菜单树（管理用）
 */
export function getMenuTreeAll(params: MenuQueryParams) {
  return http.post<MenuInfo[]>('/system/menu/getMenuTreeAll', params)
}

/**
 * 添加菜单
 */
export function addMenu(data: Partial<MenuInfo>) {
  return http.post('/system/menu/insert', data)
}

/**
 * 修改菜单
 */
export function updateMenu(data: Partial<MenuInfo>) {
  return http.post('/system/menu/update', data)
}

/**
 * 删除菜单
 */
export function deleteMenu(menuId: number) {
  return http.post('/system/menu/delete', null, { menuId })
}
