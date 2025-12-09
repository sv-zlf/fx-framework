import axios from "@/api";

/**
 * 菜单
 */

// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/menu/getMenuTreeAll",
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
