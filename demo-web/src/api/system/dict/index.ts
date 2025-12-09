import axios from "@/api";

/**
 * 字典类型
 */

// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/dict/type/getPageList",
    method: "post",
    data
  });
};

// 获取列表数据
export const getCustomTableListAPI = () => {
  return axios({
    url: "/mock/custom-table/list",
    method: "get"
  });
};
