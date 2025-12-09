import axios from "@/api";

// 登录
export const login = (params: any) => {
  return axios({
    url: "/system/login",
    method: "post",
    params
  });
};

// 获取用户信息
export const getUserInfoAPI = (params?: any) => {
  return axios({
    url: "/system/currentUser",
    method: "get",
    params
  });
};
