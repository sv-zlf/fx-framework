import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';


/**
 * 预览支持的 MIME 类型白名单（按需扩展）
 */
export enum PreviewMimeType {
  // 图片
  JPEG = 'image/jpeg',
  PNG = 'image/png',
  GIF = 'image/gif',
  WEBP = 'image/webp',
  // PDF
  PDF = 'application/pdf',
  // 视频
  MP4 = 'video/mp4',
  MPEG = 'video/mpeg',
  AVI = 'video/avi',
  // 纯文本
  TEXT = 'text/plain'
}

/**
 * 预览配置项（对齐下载工具类的参数风格）
 */
export interface PreviewOptions {
  /** 预览接口URL */
  url: string;
  /** 默认文件名（仅用于错误提示） */
  defaultFileName?: string;
  /** 是否使用Blob方式生成预览URL（默认true，更安全） */
  useBlob?: boolean;
  /** 请求配置（Axios/Fetch） */
  requestConfig?: AxiosRequestConfig | RequestInit;
}

/**
 * Fetch 预览配置项（单独拆分）
 */
export interface FetchPreviewOptions {
  /** 预览接口URL */
  url: string;
  /** 默认文件名（仅用于错误提示） */
  defaultFileName?: string;
  /** 是否使用Blob方式生成预览URL（默认true） */
  useBlob?: boolean;
  /** Fetch 请求配置（纯 Fetch 类型） */
  fetchConfig?: RequestInit;
}


/**
 * 后端文件预览工具类（对齐下载工具类结构，自动携带 Token）
 * 支持：Blob 预览URL生成、直接URL预览、预览类型校验、Token 自动注入
 */
export class FilePreviewUtil {
  // ====================================== 复用：获取用户 Token 的辅助方法（和下载工具类一致） ======================================
  /**
   * 从 localStorage 中获取用户 Token
   * @returns Token 字符串（格式：Bearer + token），无则返回空字符串
   */
  private static getAuthToken(): string {
    try {
      let userInfo: any = {};
      if (localStorage.getItem('user-info')) {
        userInfo = JSON.parse(localStorage.getItem('user-info') as string);
      }
      return 'Bearer ' + userInfo.token;
    } catch (error) {
      console.error('获取用户 Token 失败：', error);
      return '';
    }
  }

  // ====================================== 预览专属：类型校验方法 ======================================
  /**
   * 判断文件 MIME 类型是否支持预览
   * @param mimeType 文件MIME类型
   * @returns 是否支持预览
   */
  public static isPreviewable(mimeType: string): boolean {
    if (!mimeType) return false;
    const allowedTypes = Object.values(PreviewMimeType);
    return allowedTypes.includes(mimeType as PreviewMimeType);
  }

  /**
   * 判断文件是否为图片类型（预览场景高频判断）
   * @param mimeType 文件MIME类型
   * @returns 是否为图片
   */
  public static isImageType(mimeType: string): boolean {
    if (!mimeType) return false;
    return mimeType.startsWith('image/');
  }

  /**
   * 判断文件是否为PDF类型
   * @param mimeType 文件MIME类型
   * @returns 是否为PDF
   */
  public static isPdfType(mimeType: string): boolean {
    return mimeType === PreviewMimeType.PDF;
  }

  /**
   * 判断文件是否为视频类型
   * @param mimeType 文件MIME类型
   * @returns 是否为视频
   */
  public static isVideoType(mimeType: string): boolean {
    if (!mimeType) return false;
    return mimeType.startsWith('video/');
  }

  // ====================================== 复用+调整：处理Blob中的错误信息 ======================================
  /**
   * 处理预览时Blob中的错误信息（后端返回JSON错误而非文件流）
   */
  private static async handleBlobError(blob: Blob): Promise<boolean> {
    try {
      // 若Blob是JSON类型或体积过小，判定为错误响应
      if (blob.type.includes('application/json') || blob.size < 1024) {
        const text = await new Response(blob).text();
        const errorData = JSON.parse(text);
        arcoMessage('error', errorData.message || '预览请求失败，请重试', true, 'top');
        return true;
      }
      return false;
    } catch (error) {
      console.error('预览Blob错误解析失败：', error);
      return false;
    }
  }

  // ====================================== 预览核心：释放Blob URL资源（避免内存泄漏） ======================================
  /**
   * 释放预览用的Blob URL资源
   * @param blobUrl 预览生成的Blob URL
   */
  public static releasePreviewBlobUrl(blobUrl: string): void {
    try {
      if (blobUrl && blobUrl.startsWith('blob:')) {
        window.URL.revokeObjectURL(blobUrl);
      }
    } catch (error) {
      console.error('释放预览Blob URL失败：', error);
      arcoMessage('warning', '预览资源释放异常', true, 'top');
    }
  }

  // ====================================== 预览核心：Axios生成预览URL（自动携带Token） ======================================
  /**
   * 通过Axios请求后端文件流，生成预览用的Blob URL（自动携带Token）
   * @param options 预览配置
   * @returns 预览URL（Blob URL 或 直接接口URL）
   */
  static async getPreviewUrlByAxios(
    options: PreviewOptions
  ): Promise<string> {
    const { url, useBlob = true, requestConfig = {} } = options;

    try {
      // 1. 获取Token并构建请求配置（用户传入的headers优先级更高）
      const authToken = this.getAuthToken();
      const axiosConfig: AxiosRequestConfig = {
        ...(requestConfig as AxiosRequestConfig),
        // 预览需返回Blob（除非指定不使用Blob）
        ...(useBlob ? { responseType: 'blob' } : {}),
        headers: {
          // 预览专用Accept头（优先接收可预览类型）
          'Accept': Object.values(PreviewMimeType).join(', ') + ', application/octet-stream',
          // 自动注入Token
          ...(authToken ? { Authorization: authToken } : {}),
          // 合并用户传入的headers
          ...(requestConfig as AxiosRequestConfig).headers,
        },
      };

      // 2. 发送请求
      const response: AxiosResponse<Blob> = await axios(url, axiosConfig);

      // 3. 非Blob模式：直接返回接口URL
      if (!useBlob) {
        return url;
      }

      // 4. 处理Blob错误
      const isError = await this.handleBlobError(response.data);
      if (isError) {
        throw new Error('预览Blob解析失败');
      }

      // 5. 生成Blob预览URL
      const blobUrl = window.URL.createObjectURL(response.data);
      arcoMessage('success', '预览资源加载成功', true, 'top');
      return blobUrl;
    } catch (error) {
      console.error('Axios生成预览URL失败：', error);
      arcoMessage('error', `文件预览失败：${(error as Error).message || '未知错误'}`, true, 'top');
      throw error;
    }
  }

  // ====================================== 预览核心：Fetch生成预览URL（自动携带Token） ======================================
  /**
   * 通过Fetch请求后端文件流，生成预览用的Blob URL（自动携带Token）
   * @param options 预览配置
   * @returns 预览URL（Blob URL 或 直接接口URL）
   */
  static async getPreviewUrlByFetch(
    options: FetchPreviewOptions
  ): Promise<string> {
    const { url, useBlob = true, fetchConfig = {} } = options;

    try {
      // 1. 获取Token并构建请求配置
      const authToken = this.getAuthToken();
      const fetchHeaders = new Headers(fetchConfig.headers);

      fetchHeaders.set('Accept', Object.values(PreviewMimeType).join(', ') + ', application/octet-stream');
      if (authToken && !fetchHeaders.has('Authorization')) {
        fetchHeaders.set('Authorization', authToken);
      }


      const finalFetchConfig: RequestInit = {
        ...fetchConfig,
        method: fetchConfig.method || 'GET',
        headers: fetchHeaders,
        // Fetch的signal是原生AbortSignal，无需兼容
        signal: fetchConfig.signal,
      };

      // 2. 发送请求
      const response = await fetch(url, finalFetchConfig);
      if (!response.ok) {
        throw new Error(`预览请求失败：${response.status} ${response.statusText}`);
      }

      // 3. 非Blob模式：直接返回接口URL
      if (!useBlob) {
        return url;
      }

      // 4. 处理响应Blob
      const blob = await response.blob();
      const isError = await this.handleBlobError(blob);
      if (isError) {
        throw new Error('预览Blob解析失败');
      }

      // 5. 生成Blob预览URL
      const blobUrl = window.URL.createObjectURL(blob);
      arcoMessage('success', '预览资源加载成功', true, 'top');
      return blobUrl;
    } catch (error) {
      console.error('Fetch生成预览URL失败：', error);
      arcoMessage('error', `文件预览失败：${(error as Error).message || '未知错误'}`, true, 'top');
      throw error;
    }
  }

  // ====================================== 预览扩展：先获取预览链接，再生成预览URL ======================================
  /**
   * 请求后端获取预览链接，再生成预览URL（适配后端返回链接的场景）
   * @param url 获取预览链接的接口URL
   * @param requestConfig Axios请求配置
   * @param useBlob 是否使用Blob模式
   * @returns 预览URL
   */
  static async getPreviewUrlThenPreview(
    url: string,
    requestConfig: AxiosRequestConfig = {},
    useBlob = true
  ): Promise<string> {
    try {
      // 1. 获取Token并注入请求头
      const authToken = this.getAuthToken();
      const axiosConfig: AxiosRequestConfig = {
        ...requestConfig,
        headers: {
          ...(authToken ? { Authorization: authToken } : {}),
          ...requestConfig.headers,
        },
      };

      // 2. 请求预览链接
      const response = await axios(url, axiosConfig);
      const previewUrl = response.data.url;
      if (!previewUrl) {
        throw new Error('后端未返回有效预览链接');
      }

      // 3. 生成预览URL
      return this.getPreviewUrlByAxios({
        url: previewUrl,
        useBlob,
        requestConfig
      });
    } catch (error) {
      console.error('获取预览链接失败：', error);
      arcoMessage('error', '文件预览失败，请重试', true, 'top');
      throw error;
    }
  }
}

// ====================================== 简化版快捷函数（对齐下载工具类的快捷函数风格） ======================================
/**
 * 快捷预览函数（GET请求，Axios，Blob模式）
 * @param url 预览接口URL
 * @param params 请求参数
 * @param defaultFileName 默认文件名
 * @returns 预览URL
 */
export const quickPreviewFile = (
  url: string,
  params: Record<string, any> = {},
  defaultFileName = 'preview-file'
) => {
  return FilePreviewUtil.getPreviewUrlByAxios({
    url,
    defaultFileName,
    useBlob: true,
    requestConfig: { method: 'GET', params }
  });
};

/**
 * 快捷预览函数（POST请求，Axios，Blob模式）
 * @param url 预览接口URL
 * @param data 请求体数据
 * @param defaultFileName 默认文件名
 * @returns 预览URL
 */
export const quickPostPreviewFile = (
  url: string,
  data: Record<string, any> = {},
  defaultFileName = 'preview-file'
) => {
  return FilePreviewUtil.getPreviewUrlByAxios({
    url,
    defaultFileName,
    useBlob: true,
    requestConfig: { method: 'POST', data }
  });
};
