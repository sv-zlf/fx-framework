import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
// 注意：Arco Design Vue 3.x 需从子路径导入 message
// import { message } from '@arco-design/web-vue/es/message';

/**
 * 后端文件下载工具类（新增 Token 携带逻辑）
 * 支持：文件流（Blob）下载、下载链接跳转、请求头自动携带 Token
 */
export class BackendFileDownloadUtil {
  // ====================================== 新增：获取用户 Token 的辅助方法 ======================================
  /**
   * 从 localStorage 中获取用户 Token
   * @returns Token 字符串（格式：Bearer + token），无则返回空字符串
   */
  private static getAuthToken(): string {
    try {
      let userInfo: any = {};
      // 从 localStorage 获取用户信息
      if (localStorage.getItem('user-info')) {
        userInfo = JSON.parse(localStorage.getItem('user-info') as string);
      }
      // 拼接 Bearer Token（后端常用的认证格式）
      return 'Bearer '+userInfo.token;
    } catch (error) {
      console.error('获取用户 Token 失败：', error);
      return '';
    }
  }

  // ====================================== 原有核心方法（保留并修改） ======================================
  /**
   * 解析响应头中的文件名（处理中文乱码问题）
   */
  private static parseFileNameFromHeaders(headers: Record<string, string | undefined>, defaultName = 'download'): string {
    try {
      const disposition = headers['content-disposition'] || headers['Content-Disposition'];
      if (!disposition) return defaultName;

      const filenameMatch = disposition.match(/filename="?([^";]+)"?/);
      const filenameUtf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/);

      let fileName = defaultName;
      if (filenameUtf8Match && filenameUtf8Match[1]) {
        fileName = decodeURIComponent(filenameUtf8Match[1]);
      } else if (filenameMatch && filenameMatch[1]) {
        fileName = decodeURIComponent(escape(filenameMatch[1]));
      }

      return fileName;
    } catch (error) {
      console.error('解析文件名失败：', error);
      return defaultName;
    }
  }

  /**
   * 将 Blob 数据触发浏览器下载（通用方法）
   */
  private static downloadBlob(blob: Blob, fileName: string): void {
    try {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('文件下载失败：', error);
      throw new Error(`文件下载失败：${(error as Error).message}`);
    }
  }

  /**
   * 处理 Blob 中的错误信息（后端返回的不是文件流而是JSON错误时）
   */
  private static async handleBlobError(blob: Blob): Promise<boolean> {
    try {
      if (blob.type.includes('application/json') || blob.size < 1024) {
        const text = await new Response(blob).text();
        const errorData = JSON.parse(text);
        arcoMessage('error', errorData.message || '请求失败，请重试')
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  }

  // ====================================== Axios 请求文件流下载（修改：添加 Token 注入） ======================================
  /**
   * 通过 Axios 请求后端文件流并下载（自动携带 Token）
   */
  static async downloadByAxios(
    url: string,
    config: AxiosRequestConfig = {},
    defaultFileName = 'download'
  ): Promise<void> {
    try {
      // 1. 获取 Token
      const authToken = this.getAuthToken();

      // 2. 构建请求配置（自动注入 Token，用户传入的 headers 优先级更高）
      const axiosConfig: AxiosRequestConfig = {
        ...config,
        responseType: 'blob',
        headers: {
          // 基础头信息
          'Accept': 'application/octet-stream',
          // 自动注入 Token（用户传入的 Authorization 会覆盖此值）
          ...(authToken ? { Authorization: authToken } : {}),
          // 合并用户传入的 headers
          ...config.headers,
        },
      };

      // 3. 发送请求
      const response: AxiosResponse<Blob> = await axios(url, axiosConfig);

      // 4. 处理错误信息
      const isError = await this.handleBlobError(response.data);
      if (isError) return;

      // 5. 解析文件名并下载
      const headers = Object.fromEntries(
        Object.entries(response.headers).filter(([_, value]) => value != null)
      ) as Record<string, string | undefined>;
      const fileName = this.parseFileNameFromHeaders(headers, defaultFileName);

      this.downloadBlob(response.data, fileName);
      arcoMessage('success', `文件「${fileName}」下载成功！`, true, 'top')
    } catch (error) {
      console.error('请求文件流失败：', error);
      arcoMessage('error', '文件下载失败，请重试', true, 'top')
      throw error;
    }
  }

  // ====================================== Fetch 请求文件流下载（修改：添加 Token 注入） ======================================
  /**
   * 通过 Fetch 请求后端文件流并下载（自动携带 Token）
   */
  static async downloadByFetch(
    url: string,
    options: RequestInit = {},
    defaultFileName = 'download'
  ): Promise<void> {
    try {
      // 1. 获取 Token
      const authToken = this.getAuthToken();

      // 2. 构建请求配置（自动注入 Token，用户传入的 headers 优先级更高）
      const fetchHeaders = new Headers(options.headers);
      // 添加基础头信息
      fetchHeaders.set('Accept', 'application/octet-stream');
      // 自动注入 Token（用户传入的 Authorization 会覆盖此值）
      if (authToken && !fetchHeaders.has('Authorization')) {
        fetchHeaders.set('Authorization', authToken);
      }

      const fetchOptions: RequestInit = {
        ...options,
        headers: fetchHeaders,
      };

      // 3. 发送请求
      const response = await fetch(url, fetchOptions);
      if (!response.ok) {
        throw new Error(`请求失败：${response.status} ${response.statusText}`);
      }

      // 4. 处理响应数据
      const blob = await response.blob();
      const headers: Record<string, string> = {};
      response.headers.forEach((value, key) => {
        headers[key] = value;
      });

      // 5. 处理错误信息
      const isError = await this.handleBlobError(blob);
      if (isError) return;

      // 6. 解析文件名并下载
      const fileName = this.parseFileNameFromHeaders(headers, defaultFileName);
      this.downloadBlob(blob, fileName);
      arcoMessage('success', `文件「${fileName}」下载成功！`)
    } catch (error) {
      console.error('请求文件流失败：', error);
      arcoMessage('error', '文件下载失败，请重试')
      throw error;
    }
  }

  // ====================================== 下载链接处理（无需 Token，保留原有逻辑） ======================================
  /**
   * 处理后端返回的下载链接（跳转/新开窗口）
   */
  static downloadByUrl(url: string, blank = false): void {
    try {
      if (blank) {
        window.open(url, '_blank');
      } else {
        const link = document.createElement('a');
        link.href = url;
        link.download = '';
        link.target = '_self';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
      arcoMessage('success', '文件下载已启动！')
    } catch (error) {
      console.error('链接下载失败：', error);
      arcoMessage('error', '文件下载失败，请重试')
    }
  }

  /**
   * 请求后端获取下载链接，再触发下载（修改：添加 Token 注入）
   */
  static async getDownloadUrlThenDownload(
    url: string,
    config: AxiosRequestConfig = {},
    blank = false
  ): Promise<void> {
    try {
      // 1. 获取 Token 并注入请求头
      const authToken = this.getAuthToken();
      const axiosConfig: AxiosRequestConfig = {
        ...config,
        headers: {
          ...(authToken ? { Authorization: authToken } : {}),
          ...config.headers,
        },
      };

      // 2. 请求链接
      const response = await axios(url, axiosConfig);
      const downloadUrl = response.data.url;
      if (!downloadUrl) {
        throw new Error('后端未返回下载链接');
      }

      // 3. 触发下载
      this.downloadByUrl(downloadUrl, blank);
    } catch (error) {
      console.error('获取下载链接失败：', error);
      arcoMessage('error', '文件下载失败，请重试')
      throw error;
    }
  }
}

// ====================================== 简化版工具函数（保留原有逻辑，自动携带 Token） ======================================
export const quickDownloadFile = (
  url: string,
  params: Record<string, any> = {},
  defaultFileName = 'download'
) => {
  return BackendFileDownloadUtil.downloadByAxios(url, { method: 'GET', params }, defaultFileName);
};

export const quickPostDownloadFile = (
  url: string,
  data: Record<string, any> = {},
  defaultFileName = 'download'
) => {
  return BackendFileDownloadUtil.downloadByAxios(url, { method: 'POST', data }, defaultFileName);
};
