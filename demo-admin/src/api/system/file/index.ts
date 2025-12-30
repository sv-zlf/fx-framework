import axios from "@/api";

// 单文件上传
export const upload = (file: FormData, subPath?: string) => {
  return axios({
    url: "/file/upload",
    method: "post",
    data: file,
    params: { subPath },
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
};

// 多文件上传
export const uploadBatch = (files: FormData, subPath?: string) => {
  return axios({
    url: "/file/upload/batch",
    method: "post",
    data: files,
    params: { subPath },
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
};

// 获取文件分页列表
export const getFileList = (data: any) => {
  return axios({
    url: "/file/list",
    method: "post",
    data
  });
};

// 文件下载
export const download = (id: number) => {
  return `/file/download/${id}`;
};

// 文件预览
export const preview = (id: number) => {
  return `/file/preview/${id}`;
};

// 删除文件
export const deleteFile = (id: number) => {
  return axios({
    url: `/file/delete/${id}`,
    method: "delete"
  });
};

// 获取文件信息
export const getFileInfo = (id: number) => {
  return axios({
    url: `/file/info/${id}`,
    method: "get"
  });
};
