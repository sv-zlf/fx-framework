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

// 新增字典类型
export const addDictType = (data: any) => {
  return axios({
    url: "/system/dict/type/insert",
    method: "post",
    data
  });
}

// 修改字典类型
export const updateDictType = (data: any) => {
  return axios({
    url: "/system/dict/type/update",
    method: "post",
    data
  });
}

// 删除字典类型
export const deleteDictType = (id: any) => {
  return axios({
    url: "/system/dict/type/delete",
    method: "post",
    params: {
      id
    }
  });
}
/**
 * 字典雷列表
 */

// 分页获取字典列表
  export const getDictItemPageList = (data: any) => {
    return axios({
      url: "/system/dict/item/getPageList",
      method: "post",
      data
    })
}
// 按照字典类型获取列表
export const getListByCode = (code: string) => {
  return axios({
    url: "/system/dict/item/getDictItemList",
    method: "get",
    params: {
      dictTypeCode : code
    }
  })
}

// 新增字典项
export const addDictItem = (data: any) => {
    return axios({
      url: "/system/dict/item/insert",
      method: "post",
      data
    })
}

// 修改字典项
export const updateDictItem = (data: any) => {
  return axios({
    url: "/system/dict/item/update",
    method: "post",
    data
  })
}

// 删除字典项
export const deleteDictItem = (id: any) => {
    return axios({
      url: "/system/dict/item/delete",
      method: "post",
      params: {
        id
      }
    })
}
