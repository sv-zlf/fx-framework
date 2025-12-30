import axios from "@/api";

// 获取列表数据
export const getUserSessionList = (params: any) => {
  return axios({
    url: "/system/user/session/list",
    method: "get",
    params,
    paramsSerializer: {
      indexes: null
    }
  });
};

// 删除会话
export const deleteSession = (id: number) => {
  return axios({
    url: "/system/user/session/delete",
    method: "post",
    params: { id }
  });
};

// 强制退出
export const forceLogout = (sessionId: string) => {
  return axios({
    url: "/system/user/session/forceLogout",
    method: "post",
    params: { sessionId }
  });
};
