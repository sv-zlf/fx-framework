import axios from "@/api";

// 获取系统日志列表数据
export const getLogList = (data: any) => {
  return axios({
    url: "/system/log/list",
    method: "get",
    params: data
  });
};

// 删除系统日志
export const deleteLog = (id: number) => {
  return axios({
    url: "/system/log/delete",
    method: "post",
    params: {
      id
    }});
};

