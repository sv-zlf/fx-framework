import { defineStore } from "pinia";
import persistedstateConfig from "@/store/config/index";
import {getListByCode} from "@/api/system/dict";
/**
 * 用户信息
 * @methods setAccount 设置账号信息
 * @methods setToken 设置token
 * @methods logOut 退出登录
 */
const systemStore = () => {
  // 字典数据
  // const dict = ref<any>([]);
  // 字典缓存：{ code: 列表数据 }，key为字典编码，value为对应列表
  const dict = ref<Record<string, any[]>>({});
  // 防重复请求：{ code: 是否正在请求 }
  const isFetchingDict = reactive<Record<string, boolean>>({});

  // 设置字典数据
  /**
   * 按code获取字典数据（优先缓存，无则请求接口）
   * @param code 字典编码（如'status'）
   */
  async function setDictData(code: string) {
    // 缓存已存在 → 直接返回
    if (dict.value[code]?.length) return;
    // 正在请求中 → 直接返回，避免重复请求
    if (isFetchingDict[code] ?? false) return;
    // 标记为请求中
    isFetchingDict[code] = true;
    try {
      // 调用单个字典接口：只获取当前code对应的列表
      const res = await getListByCode(code);
      // 缓存结果（自动通过Pinia persist同步到本地）
      dict.value[code] = res.data || [];
    } catch (error) {
      console.error(`获取字典[${code}]失败`, error);
      dict.value[code] = [];
    } finally {
      console.log("字典缓存结果：", dict.value)
      isFetchingDict[code] = false;
    }
  }


  /**
   * 清除指定字典缓存（可选：用于强制刷新场景）
   * @param code 字典编码，不传则清除所有
   */
  function clearDict(code?: string) {
    if (code) {
      delete dict.value[code];
    } else {
      dict.value = {};
    }
  }

  return { dict, setDictData, clearDict, isFetchingDict };
};

export const useSystemStore = defineStore("system", systemStore, {
  persist: persistedstateConfig("system", ["dict"])
});
