import axios from "@/api";

// 单文件上传
export const upload = (file: any, subPath?: string) => {
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
export const uploadBatch = (files: [], subPath?: string) => {
  return axios({
    url: "/file/upload/batch",
    method: "post",
    data: files,
    params: { subPath },
    headers: {
      "Content-Type": "multipart/form-data"
    },
    paramsSerializer: {
      indexes: null // 禁用数组索引（避免生成 menuIds[0]=5&menuIds[1]=1）
    }
  });
};

// 获取文件分页列表
export const getFileList = (params: any) => {
  return axios({
    url: "/file/list",
    method: "get",
    params
  });
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
