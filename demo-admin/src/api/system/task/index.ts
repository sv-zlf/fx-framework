import axios from "@/api";

// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/task/getPageList",
    method: "post",
    data
  });
};

// 保存或更新
export const saveOrUpdate = (data: any) => {
  return axios({
    url: "/system/task/saveOrUpdate",
    method: "post",
    data
  });
};

// 删除
export const deleteTask = (id: number) => {
  return axios({
    url: "/system/task/delete",
    method: "post",
    params: { id }
  });
};

// 执行
export const executeTask = (id: number) => {
  return axios({
    url: "/system/task/execute",
    method: "post",
    params: { id }
  });
};

// 暂停
export const pauseTask = (id: number) => {
  return axios({
    url: "/system/task/pause",
    method: "post",
    params: { id }
  });
};

// 恢复
export const resumeTask = (id: number) => {
  return axios({
    url: "/system/task/resume",
    method: "post",
    params: { id }
  });
};