import { http } from '@/http/http'
import {IAuthLoginRes} from "@/api/types/login";
import {ILoginForm} from "@/api/login";

/**
 * 获取菜单数据
 */

export function getRoutersAPI() {
  return http.get<any>('/system/menu/getMenuTree')
}
