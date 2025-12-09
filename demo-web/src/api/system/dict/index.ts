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



/**
 * 字典雷列表
 */

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


