import { http } from '@/http/http'

/**
 * 在线用户列表参数
 */
export interface SessionListParams {
  pageIndex?: number
  pageSize?: number
  loginLocation?: string
  loginName?: string
  startLoginTime?: string | null
  endLoginTime?: string | null
}

/**
 * 会话信息
 */
export interface SessionInfo {
  id: string
  sessionId: string
  loginName: string
  host: string
  loginLocation: string
  sessionStatus: number // 1=在线, 0=离线
  browser: string
  os: string
  loginTime: string
  lastAccessTime: string
}

/**
 * 会话列表响应
 */
export interface SessionListResponse {
  records: SessionInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 获取在线用户列表
 */
export function getSessionList(params: SessionListParams) {
  return http.get<SessionListResponse>('/system/user/session/list', params)
}

/**
 * 强制用户退出
 */
export function forceLogout(sessionId: string) {
  return http.post('/system/user/session/forceLogout', null, {
    sessionId
  })
}

