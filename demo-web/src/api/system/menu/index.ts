import axios from "@/api";

/**
 * 菜单
 */

// 获取菜单数据
export const getRoutersAPI = () => {
  return axios({
    url: "/system/menu/getMenuTree",
    method: "get"
  });
};

// 获取列表数据
export const getMenuTreeAll = (data: any) => {
  return axios({
    url: "/system/menu/getMenuTreeAll",
    method: "post",
    data
  });
};

// 添加菜单
export const addMenu = (data: any) => {
  return axios({
    url: "/system/menu/insertMenu",
    method: "post",
    data
  })
}

// 修改菜单
export const updateMenu = (data: any) => {
  return axios({
    url: "/system/menu/updateMenu",
    method: "post",
    data
  })
}
