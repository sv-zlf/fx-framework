import axios from "@/api";

// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/role/getPageList",
    method: "post",
    data
  });
};

// 获取列表
export const getRoleList = (roleName: string) => {
  return axios({
    url: "/system/role/getRoleList",
    method: "get",
    params: {
      roleName
    }
  });
}

// 新增角色
export const addRole = (data: any) => {
  return axios({
    url: "/system/role/insert",
    method: "post",
    data
  });
}

// 更新角色
export const updateRole = (data: any) => {
  return axios({
    url: "/system/role/update",
    method: "post",
    data
  });
}

// 删除角色
export const deleteRole = (roleId: any) => {
  return axios({
    url: "/system/role/delete",
    method: "post",
    params: {
      roleId
    }
  });
}


/*
 * 菜单授权
 */
// 获取角色权限授权列表
export const toBind = (roleId: any) => {
  return axios({
    url: "/system/roleMenu/toBind",
    method: "get",
    params: {
      roleId:roleId
    }
  })
}

// 绑定菜单
export const bindMenu = (roleId: number, menuIds: number[]) => {
  return axios({
    url: "/system/roleMenu/bindMenu",
    method: "post",
    params: {
      roleId,
      menuIds
    },
    paramsSerializer: {
      indexes: null // 禁用数组索引（避免生成 menuIds[0]=5&menuIds[1]=1）
    }
  })
}
