import { http } from '@/http/http';

/**
 * 获取菜单数据
 */

export function getRoutersAPI() {
  return http.get<any>('/system/menu/getMenuTree')
}
