import axios from "@/api";

// 登录
export const login = (params: any) => {
  return axios({
    url: "/system/login",
    method: "post",
    params
  });
};

// 注册
export const register = (data: any) => {
  return axios({
    url: "/system/register",
    method: "post",
    data
  });
}

// 注销
export const logout = () => {
  return axios({
    url: "/system/logout",
    method: "post"
  });
}

// 获取用户信息
export const getUserInfo = (params?: any) => {
  return axios({
    url: "/system/currentUser",
    method: "get",
    params
  });
};


// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/user/getPageList",
    method: "post",
    data
  });
};

// 安全设置
export const safeSetting = (data:any) => {
  return axios({
    url: "/system/user/safeSetting",
    method: "post",
    data
  })
}

// 基本信息设置
export const basicSetting = (data:any) => {
  return axios({
    url: "/system/user/basicSetting",
    method: "post",
    data
  })
}

//保存或者更新
export const saveOrUpdate = (data: any) => {
  return axios({
    url: "/system/user/saveOrUpdate",
    method: "post",
    data
  })
}

//删除用户
export const deleteUser = (userId: any) => {
  return axios({
    url: "/system/user/delete",
    method: "post",
    params: {
      userId
    }
  })
}

// 批量删除用户
export const deleteBatch = (userIds: any) => {
  return axios({
    url: "/system/user/deleteBatch",
    method: "post",
    params: {
      userIds
    },
    paramsSerializer: {
      indexes: null // 禁用数组
    }
  })
}
