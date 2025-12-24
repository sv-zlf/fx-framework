import axios from "@/api";

// 获取列表数据
export const getPageList = (params: any) => {
  return axios({
    url: "/system/user/session/getPageList",
    method: "get",
    params
  });
};
