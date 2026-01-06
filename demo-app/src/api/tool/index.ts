import { http } from '@/http/http'

/**
 * 代码生成-列表参数
 */
export interface TableListParams {
  pageIndex?: number
  pageSize?: number
  tableName?: string
}

/**
 * 表信息
 */
export interface TableInfo {
  id: number
  tableName: string
  className: string
  tableComment: string
  moduleName: string
  author: string
  generateType: number // 0=压缩包, 1=本地工程
  remark: string
  createTime?: string
}

/**
 * 表列表响应
 */
export interface TableListResponse {
  records: TableInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 字段信息
 */
export interface ColumnInfo {
  id: number
  columnName: string
  propertyName: string
  columnType: string
  javaType: string
  primaryKey: boolean
  notNull: boolean
  autoIncrement: boolean
  comment: string
}

/**
 * 获取表列表
 */
export function getTableInfoList(params: TableListParams) {
  return http.get<TableListResponse>('/tool/gen/list', params)
}

/**
 * 创建表
 */
export function createTable(params: { sql: string; moduleName: string }) {
  return http.post('/tool/gen/genTable', null, params)
}

/**
 * 更新表信息
 */
export function updateTable(data: Partial<TableInfo>) {
  return http.post('/tool/gen/update', data)
}

/**
 * 删除表
 */
export function deleteTable(id: number) {
  return http.post('/tool/gen/delete', null, { id })
}

/**
 * 生成代码-本地
 */
export function createCode(tableId: number) {
  return http.post('/tool/gen/genCode', null, { tableId })
}

/**
 * 生成代码-压缩包
 */
export function createCodeZip(tableId: number) {
  return http.post('/tool/gen/genCodeZip', null, { tableId })
}

/**
 * 查询表字段
 */
export function getColumnList(tableId: number) {
  return http.get<ColumnInfo[]>('/tool/gen/getColumnList', { tableId })
}

/**
 * 批量更新表字段信息
 */
export function batchUpdateColumn(data: ColumnInfo[]) {
  return http.post('/tool/gen/batchUpdateColumn', data)
}

/**
 * 删除表字段
 */
export function deleteColumn(id: number) {
  return http.post('/tool/gen/deleteColumn', null, { id })
}
