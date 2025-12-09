import pinia from "@/store/index";
import { storeToRefs } from "pinia";
import { useRouteConfigStore } from "@/store/modules/route-config";
import { useThemeConfig } from "@/store/modules/theme-config";
import { deepClone } from "@/utils/index";
import { arrayFlattened } from "@/utils/tree-tools";

/**
 * 统一处理所有的路由跳转：当前路由高亮、tabs栏数据
 * 处理项目内跳转，存入当前跳转路由和tabs标签栏数据
 * menu和tabs以及手动刷新浏览器等功能只需要跳转即可，缓存和高亮的逻辑这边负责
 * @param {any} current 需要跳转的路由和路由参数
 */
export const currentlyRoute = (current: any) => {
  const route = deepCloneRoute(current);
  const themeStore = useThemeConfig();
  const { isTabs } = storeToRefs(themeStore);
  const store = useRouteConfigStore(pinia);
  const { tabsList, routeList } = storeToRefs(store);
  // tabs无数据则默认添加首页
  if (tabsList.value.length == 0 && routeList.value.length != 0) {
    store.setTabs(routeList.value[0]);
  }
  // 存入当前路由-高亮
  store.setCurrentRoute(route);
  // 如果是外链路由则不做后续任何缓存操作，条件: 有外链 && 非内嵌
  if (route.meta.isExternal && route.meta.iframe == 1) return;
  // 存入tabs栏数据，条件：开启tabs
  if (isTabs.value && !route.meta.isFull) store.setTabs(route);
  // 不缓存路由 || 不渲染tabs ，符合任意条件则不缓存路由
  // if (!route.meta.keepAlive || !isTabs.value) return;
  if (!isTabs.value) return;
  store.setRoutePaths(route.path); // 缓存路由
};

/**
 * 深拷贝路由，切断与原路由的联系，防止路由参数污染
 * @param route 当前路由
 * @returns 深拷贝后的路由
 */
export const deepCloneRoute = (route: any) => {
  return deepClone({
    path: route.fullPath,
    name: route.name,
    meta: route.meta,
    query: route.query,
    params: route.params
  });
};

/**
 * 模块替换，对路由中的模块进行转换
 * @param {array} tree 过滤角色权限后的树
 */
export const moduleReplacement = (tree: any) => {
  tree.forEach((item: any) => {
    item.children && delete item.children;
    moduleMatch(item);
  });
  return tree;
};



export const normalizeRouteTree = (rawTree: any[]): any[] => {
  if (!Array.isArray(rawTree)) return [];

  // 第一步：先将后端原始树 → 前端标准结构树（核心映射）
  const standardTree = rawTree
    .map(rawNode => mapToStandardRoute(rawNode)) // 逐个节点映射
    .filter(Boolean); // 过滤掉映射失败的节点

  // 第二步：对标准结构做最终容错（确保字段无异常）
  return standardTree.map(node => {
    // 过滤禁用的路由（meta.disable 为 true 时，不参与路由和菜单渲染）
    if (node.meta.disable) return null;

    // 处理 children（过滤禁用的子节点）
    node.children = node.children.filter((child: any) => !child.meta.disable);

    return node;
  }).filter(Boolean); // 过滤掉 null 节点
};

/**
 * 后端字段 → 前端标准字段的映射规则
 * key：前端标准字段名；value：后端对应的字段名（支持嵌套，如 'meta.title' → 'menuName'）
 */
const routeMapConfig = {
  // 路由基础字段（非 meta 内的字段）
  path: 'path', // 后端路由路径字段名（根据实际调整，比如有的后端叫 path）
  component: 'component', // 后端组件路径字段名（比如有的后端叫 component）
  name: 'menuName',
  // meta 内的字段（统一封装到 meta 对象）
  'meta.title': 'title', // 菜单名称（国际化需额外处理）
  'meta.status': 'status', // 菜单名称（国际化需额外处理）
  'meta.isFull': 'isFull',
  'meta.affix': 'affix', // 是否固定 tabsView
  'meta.isExternal': 'isExternal', // 外链地址
  'meta.iframe': 'iframe', // 是否内嵌 iframe
  'meta.svgIcon': 'svgIcon', // 菜单图标
  'meta.icon': 'icon', // 菜单图标
  'meta.sort': 'sort' ,// 菜单排序
  'meta.type':'type',
  'meta.hide': 'isHide'
};
/**
 * 单个路由节点：后端结构 → 前端标准结构
 * @param rawNode 后端返回的原始路由节点
 * @returns 前端标准路由节点
 */
export const mapToStandardRoute = (rawNode: any): any => {
  if (!rawNode) return null;

  // 初始化前端标准节点结构
  const standardNode: any = {
    path: '',
    name: 'home',
    component: '',
    meta: {
      title: '未命名菜单',
      status: 0,
      isFull: false,
      affix: 0,
      isExternal: 0,
      iframe: 0,
      icon: '',
      svgIcon: '',
      sort: 999 ,// 默认排序（数字越小越靠前）
      type:'0',
      hide: 0
    },
    children: []
  };

  // 按照映射规则，赋值基础字段和 meta 字段
  Object.entries(routeMapConfig).forEach(([standardKey, rawKey]) => {
    // 处理 meta 内的字段（如 'meta.title'）
    if (standardKey.startsWith('meta.')) {
      const metaKey = standardKey.split('meta.')[1];
      // 从后端原始节点中获取值（支持后端字段是下划线，如 is_full → isFull）
      standardNode.meta[metaKey] = getRawValue(rawNode, rawKey) ?? standardNode.meta[metaKey];
    } else {
      // 处理基础字段（如 path、isFull）
      standardNode[standardKey] = getRawValue(rawNode, rawKey) ?? standardNode[standardKey];
    }
  });

  //  补充必要的默认值（避免字段缺失）
  // 生成唯一 name（优先用后端返回的 name → path → id）
  standardNode.name = rawNode.name || standardNode.path || `route-${rawNode.id || Date.now()}`;

  // 处理 children（递归转换子菜单）
  standardNode.children = rawNode.children
    ? rawNode.children.map((child: any) => mapToStandardRoute(child)).filter(Boolean)
    : [];
  // 处理 path 兜底（避免空路径）
  standardNode.path = standardNode.path || `/${standardNode.name}`;
  // 处理外链和 iframe 的依赖关系（按前端规则补全）
  if (standardNode.meta.isExternal) {
    standardNode.meta.iframe = standardNode.meta.iframe ?? false;
  } else {
    standardNode.meta.iframe = false; // 无链接时，iframe 强制为 false
  }

  return standardNode;
};

/**
 * 辅助函数：从后端原始节点中获取值（兼容下划线命名 is_full → 驼峰 isFull）
 * @param rawNode 后端原始节点
 * @param rawKey 后端字段名（如 'isFull' 或 'is_full'）
 * @returns 后端字段值
 */
const getRawValue = (rawNode: any, rawKey: string): any => {
  // 优先直接取字段（驼峰）
  if (rawNode.hasOwnProperty(rawKey)) {
    return rawNode[rawKey];
  }
  // 兼容后端下划线命名（如 is_full → isFull）
  const camelCaseKey = rawKey.replace(/_(\w)/g, (_, c) => c.toUpperCase());
  if (rawNode.hasOwnProperty(camelCaseKey)) {
    return rawNode[camelCaseKey];
  }
  // 兼容后端大写命名（如 IS_FULL → isFull）
  const lowerCamelKey = rawKey.charAt(0).toLowerCase() + rawKey.slice(1).replace(/_(\w)/g, (_, c) => c.toUpperCase());
  if (rawNode.hasOwnProperty(lowerCamelKey)) {
    return rawNode[lowerCamelKey];
  }
  return undefined;
};

/**
 * 模块匹配
 * 1、导入 views 目录及其子目录下的所有 .vue 文件。
 * 2、匹配views下的所有文件路径，将模块转换为按需引入的真实模块
 * 3、未匹配上，不做处理
 */
// 匹配views里面所有的.vue文件
const modules = import.meta.glob("@/views/**/*.vue");
export const moduleMatch = (item: any) => {
  // 菜单无需匹配
  if (item.meta.type === 1) {
    return;
  }
  // 容错：若 component 为空，直接设为 404 组件
  if (!item.component) {
    item.component = () => import('@/views/error/404.vue');
    return;
  }

  let matched = false;
  // 遍历 modules 匹配组件（保持你的原有逻辑，优化匹配判断）
  for (const key in modules) {
    // 关键：适配不同环境下的 key 格式（
    const dir = key
      .replace(/^\/src\/views\//, '') // 移除前缀（根据实际 key 格式调整）
      .replace(/^@\/views\//, '') // 兼容 Webpack 的 @ 别名
      .replace('.vue', ''); // 移除后缀

    // 匹配成功：赋值为懒加载组件
    if (item.component === dir) {
      item.component = modules[key]; // 修复：直接赋值 modules[key]，而非 () => modules[key]()
      matched = true;
      break;
    }
  }

  // 匹配失败：fallback 到 404 组件（避免字符串 component 导致警告）
  if (!matched) {
    console.warn(`组件匹配失败：未找到 ${item.component}，已 fallback 到 404`);
    item.component = () => import('@/views/error/404.vue');
  }
};
/**
 * 路由树转一维数组
 * @param {array} tree 路由树
 * @returns 一维路由数组
 */
export function linearArray(tree: any) {
  const nodes: any = deepClone(tree);
  return arrayFlattened(nodes, "children");
}
