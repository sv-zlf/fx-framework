import axios from "@/api";

// 生成代码-本地
export const createCode = (params: any) => {
  return axios({
    url: "/tool/gen/genCode",
    method: "post",
    params
  });
}
// 生成代码-压缩包
export const createCodeZip = (params: any) => {
  return axios({
    url: "/tool/gen/genCodeZip",
    method: "post",
    params
  });
}

// 获取列表数据
export const getPageList = (params: any) => {
  return axios({
    url: "/tool/gen/getPageList",
    method: "get",
    params
  });
};

// 生成表
export const createTable = (params: any) => {
  return axios({
    url: "/tool/gen/genTable",
    method: "post",
    params
  })
}

// 更新表信息
export const updateTable = (data: any) => {
  return axios({
    url: "/tool/gen/update",
    method: "post",
    data
  })
}
