import axios from "@/api";

// 获取列表数据
export const getPageList = (params: any) => {
  return axios({
    url: "/tool/gen/getPageList",
    method: "get",
    params
  });
};
