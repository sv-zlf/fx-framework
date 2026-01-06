import { http } from '@/http/http'

/**
 * 定时任务列表参数
 */
export interface TaskListParams {
  pageIndex?: number
  pageSize?: number
  taskName?: string
  taskGroup?: string
  status?: number
}

/**
 * 定时任务信息
 */
export interface TaskInfo {
  id: number
  taskName: string
  taskGroup: string
  cronExpression: string
  invokeTarget: string
  description: string
  status: number // 0=正常, 1=暂停
  concurrent: number // 0=禁止, 1=允许
  executionCount: number
  failureCount: number
  lastExecutionTime: string
  lastExecutionResult: string
  remark: string
}

/**
 * 任务列表响应
 */
export interface TaskListResponse {
  records: TaskInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 获取定时任务列表
 */
export function getTaskList(params: TaskListParams) {
  return http.get<TaskListResponse>('/system/task/list', params)
}

/**
 * 保存或更新任务
 */
export function saveOrUpdate(data: Partial<TaskInfo>) {
  return http.post('/system/task/saveOrUpdate', data)
}

/**
 * 删除任务
 */
export function deleteTask(id: number) {
  return http.post('/system/task/delete', null, { id })
}

/**
 * 执行任务
 */
export function executeTask(id: number) {
  return http.post('/system/task/execute', null, { id })
}

/**
 * 暂停任务
 */
export function pauseTask(id: number) {
  return http.post('/system/task/pause', null, { id })
}

/**
 * 恢复任务
 */
export function resumeTask(id: number) {
  return http.post('/system/task/resume', null, { id })
}
