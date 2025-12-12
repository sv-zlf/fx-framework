import axios from "@/api";

// 获取列表数据
export const getPageList = (data: any) => {
  return axios({
    url: "/system/user/getPageList",
    method: "post",
    data
  });
};

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
