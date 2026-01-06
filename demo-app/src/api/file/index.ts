import { http } from '@/http/http'

/**
 * 文件列表参数
 */
export interface FileListParams {
  pageIndex?: number
  pageSize?: number
  fileName?: string
  fileType?: string
}

/**
 * 文件信息
 */
export interface FileInfo {
  id: number
  originalName: string
  fileName: string
  fileType: string // image, document, video, audio, archive, other
  fileSize: number
  username: string
  createTime: string
}

/**
 * 文件列表响应
 */
export interface FileListResponse {
  records: FileInfo[]
  total: number
  current: number
  pageSize: number
}

/**
 * 上传文件
 */
export function upload(file: any, subPath?: string) {
  return http.post('/file/upload', file, { subPath }, {
    header: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传文件
 */
export function uploadBatch(files: any[], subPath?: string) {
  return http.post('/file/upload/batch', files, { subPath }, {
    header: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取文件列表
 */
export function getFileList(params: FileListParams) {
  return http.get<FileListResponse>('/file/list', params)
}

/**
 * 删除文件
 */
export function deleteFile(id: number) {
  return http.delete(`/file/delete/${id}`)
}

/**
 * 批量删除文件
 */
export function deleteBatch(ids: number[]) {
  return http.post('/file/delete/batch', { ids })
}

/**
 * 获取文件信息
 */
export function getFileInfo(id: number) {
  return http.get(`/file/info/${id}`)
}
