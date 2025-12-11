<template>
  <div class="snow-fill">
    <div class="snow-fill-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="form.name" placeholder="请输入菜单名称" allow-clear />
            <a-select v-model="form.status" placeholder="请选择状态" allow-clear style="width: 120px">
              <a-option v-for="item in openState" :key="item.id" :value="item.dictItemCode">{{ item.dictItemName }}</a-option>
            </a-select>
            <a-select v-model="form.status" placeholder="请选择隐藏状态" allow-clear style="width: 150px">
              <a-option v-for="item in openState" :key="item.id" :value="item.dictItemCode">{{ item.dictItemName }}</a-option>
            </a-select>
            <a-button type="primary" @click="onSearch">
              <template #icon><icon-search /></template>
              <span>查询</span>
            </a-button>

            <a-button @click="onReset">
              <template #icon>
                <icon-refresh />
              </template>
              <template #default>重置</template>
            </a-button>
          </a-space>
        </template>
        <template #right>
          <a-space wrap>
            <a-button type="primary" @click="onAdd">
              <template #icon><icon-plus /></template>
              <span>新增</span>
            </a-button>
            <a-button type="primary" status="success" @click="onExpand">
              <template #icon>
                <icon-swap />
              </template>
              <span>{{ expand ? "收起" : "展开" }}</span>
            </a-button>
          </a-space>
        </template>
      </s-layout-tools>
      <a-table
        ref="tableRef"
        :data="tableTree"
        :loading="loading"
        row-key="id"
        :bordered="{cell:true}"
        show-empty-tree
        :pagination="false"
        size="large"
        :scroll="{ x: '150%', y: '100%' }"
        :hide-expand-button-on-empty="true"
      >
        <template #columns>
          <a-table-column title="菜单名称" :width="120" ellipsis tooltip>
            <template #cell="{ record }">
              {{ $t(`menu.${record.title}`) }}
            </template>
          </a-table-column>
          <a-table-column title="菜单类型" align="center" :width="100">
            <template #cell="{ record }">
              <a-tag v-if="record.type == 1" bordered size="small" color="purple">目录</a-tag>
              <a-tag v-else-if="record.type == 2" bordered size="small" color="green">菜单</a-tag>
              <a-tag v-else bordered size="small" color="gray">按钮</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="图标" align="center" :width="100">
            <template #cell="{ record }">
              <MenuItemIcon :svg-icon="record.svgIcon" :icon="record.icon" />
            </template>
          </a-table-column>
          <a-table-column title="路由路径" data-index="path" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="路由名称" data-index="menuName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="组件路径" :width="200" ellipsis tooltip>
            <template #cell="{ record }">
              {{ record.component }}
            </template>
          </a-table-column>
          <a-table-column title="权限标识" :width="200" ellipsis tooltip>
            <template #cell="{ record }">
              {{ record.permission }}
            </template>
          </a-table-column>
          <a-table-column title="排序" align="center" :width="80">
            <template #cell="{ record }">
              {{ record.sort }}
            </template>
          </a-table-column>
          <a-table-column title="状态" align="center" :width="100">
            <template #cell="{ record }">
              <a-space>
                <a-tag bordered size="small" color="arcoblue" v-if="record.status == 1">启用</a-tag>
                <a-tag bordered size="small" color="red" v-else>禁用</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="是否隐藏" align="center" :width="100">
            <template #cell="{ record }">
              <a-space>
                <a-tag bordered size="small" color="arcoblue" v-if="record.isHide">是</a-tag>
                <a-tag bordered size="small" color="red" v-else>否</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="是否外链" align="center" :width="100">
            <template #cell="{ record }">
              <a-space>
                <a-tag bordered size="small" color="arcoblue" v-if="record.isExternal">是</a-tag>
                <a-tag bordered size="small" color="red" v-else>否</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="是否全屏" align="center" :width="100">
            <template #cell="{ record }">
              <a-space>
                <a-tag bordered size="small" color="arcoblue" v-if="record.isFull">是</a-tag>
                <a-tag bordered size="small" color="red" v-else>否</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center" :width="100" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button size="mini" type="primary" @click="onUpdate(record)">
                  <template #icon><icon-edit /></template>
                  <span>修改</span>
                </a-button>
<!--                <a-button size="mini" type="primary" status="success" v-if="record.type != 3" @click="onCurrentAdd(record)">-->
<!--                  <template #icon><icon-plus /></template>-->
<!--                  <span>新增</span>-->
<!--                </a-button>-->
                <a-popconfirm type="warning" content="确定删除该项吗?">
                  <a-button size="mini" type="primary" status="danger">
                    <template #icon><icon-delete /></template>
                    <span>删除</span>
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </div>

    <a-modal :width="dialogWidth()" v-model:visible="open" @close="afterClose" @ok="handleOk" @cancel="afterClose">
      <template #title> {{ title }} </template>
      <div>
        <a-form ref="formRef" auto-label-width :layout="formLayout" :rules="rules" :model="addFrom">
          <a-form-item field="type" label="菜单类型" validate-trigger="blur">
            <a-radio-group type="button" :disabled="!!addFrom.id" v-model="addFrom.type" @change="typeChange">
              <a-radio v-for="item in menuType" :key="item.value" :value="item.value">{{ item.name }}</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item field="parentId" label="上级菜单" validate-trigger="blur" :disabled="!!addFrom.id">
            <a-tree-select
              v-model="addFrom.parentId"
              :data="menuTree"
              :field-names="{
                key: 'id',
                title: 'i18n',
                children: 'children'
              }"
              allow-clear
              placeholder="请选择上级菜单"
            ></a-tree-select>
            <template #extra>
              <div>未选择则默认第一级</div>
            </template>
          </a-form-item>
          <a-row :gutter="24" v-if="[1, 2].includes(addFrom.type)">
            <a-col :span="12">
              <a-form-item field="svgIcon" label="自定义图标" validate-trigger="blur">
                <s-select-icon type="svg" v-model="addFrom.svgIcon" />
                <template #extra>
                  <div>自定义图标优先级高于菜单图标</div>
                </template>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item field="icon" label="菜单图标" validate-trigger="blur">
                <s-select-icon type="arco" v-model="addFrom.icon" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item field="title" label="菜单标题" validate-trigger="blur">
            <a-input
              v-model="addFrom.title"
              placeholder="请输入菜单标题"
              allow-clear
              @input="(e: string) => onTrim(e, 'title')"
            />
            <template #extra>
              <div>
                优先匹配国际化Key
                <a-typography-text code v-if="addFrom.title"> menu.{{ addFrom.title }} </a-typography-text>
                （无对应Key则直接取标题展示）
              </div>
            </template>
          </a-form-item>
          <a-form-item v-if="[1, 2].includes(addFrom.type)" field="path" label="路由路径" validate-trigger="blur">
            <a-input v-model="addFrom.path" placeholder="请输入路由路径，如：/home" allow-clear @input="pathChange" />
            <template #extra>
              <div>
                菜单名称由路径自动生成
                <a-typography-text code v-if="addFrom.menuName"> {{ addFrom.menuName }} </a-typography-text>
              </div>
            </template>
          </a-form-item>
          <a-form-item v-if="addFrom.type == 3" field="permission" label="权限标识" validate-trigger="blur">
            <a-input
              v-model="addFrom.permission"
              placeholder="请输入权限标识，如：sys:btn:add"
              allow-clear
              @input="(e: string) => onTrim(e, 'permission')"
            />
          </a-form-item>

          <a-form-item
            v-if="addFrom.type == 2 && !addFrom.isExternal"
            field="component"
            label="组件路径"
            validate-trigger="blur"
          >
            <a-input
              v-model="addFrom.component"
              placeholder="请输入组件路径"
              allow-clear
              @input="(e: string) => onTrim(e, 'component')"
            >
              <template #prepend>@/views/</template>
              <template #append>.vue</template>
            </a-input>
          </a-form-item>
          <a-form-item
            v-else-if ="addFrom.type == 2 && addFrom.isExternal"
            field="component"
            label="组件路径"
            validate-trigger="blur"
          >
            <a-input
              v-model="addFrom.component"
              placeholder="请输入组件路径(外链地址)"
              allow-clear
              @input="(e: string) => onTrim(e, 'component')"
            >
            </a-input>
            <template #extra>
              <div>
                外链地址
              </div>
            </template>
          </a-form-item>
          <a-row :gutter="24">
            <a-col :span="8" v-if="[1, 2].includes(addFrom.type)">
              <a-form-item field="hide" label="显示状态" validate-trigger="blur">
                <a-switch type="round" v-model="addFrom.isHide" :checked-value="false" :unchecked-value="true">
                  <template #checked> 显示 </template>
                  <template #unchecked> 隐藏 </template>
                </a-switch>
              </a-form-item>
            </a-col>
            <a-col :span="8" v-if="[1, 2].includes(addFrom.type)">
              <a-form-item field="disable" label="启用状态" validate-trigger="blur">
                <a-switch type="round" v-model="addFrom.status" :checked-value="1" :unchecked-value="0">
                  <template #checked> 启用 </template>
                  <template #unchecked> 禁用 </template>
                </a-switch>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24" v-if="addFrom.type == 2">
            <a-col :span="8">
              <a-form-item field="affix" label="固定Tabs" validate-trigger="blur">
                <a-switch type="round" v-model="addFrom.affix"  :checked-value="1" :unchecked-value="0">
                  <template #checked> 是 </template>
                  <template #unchecked> 否 </template>
                </a-switch>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item field="isLink" label="是否外链" validate-trigger="blur">
                <a-switch type="round" v-model="addFrom.isExternal" @change="onIsLink">
                  <template #checked> 是 </template>
                  <template #unchecked> 否 </template>
                </a-switch>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item field="iframe" label="内嵌窗口" validate-trigger="blur" :disabled="!addFrom.isExternal">
                <a-switch type="round" v-model="addFrom.iframe" >
                  <template #checked> 是 </template>
                  <template #unchecked> 否 </template>
                </a-switch>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item field="link" label="外链路径" validate-trigger="blur" v-if="addFrom.type == 2 && addFrom.isLink">
            <a-input v-model="addFrom.link" placeholder="请输入路由路径" allow-clear />
          </a-form-item>
          <a-form-item field="affix" label="全屏显示" validate-trigger="blur" v-if="addFrom.type == 2">
            <a-switch type="round" v-model="addFrom.isFull">
              <template #checked> 是 </template>
              <template #unchecked> 否 </template>
            </a-switch>
          </a-form-item>
          <a-form-item field="sort" label="排序" validate-trigger="blur">
            <a-input-number
              v-model="addFrom.sort"
              :step="1"
              :precision="0"
              :min="1"
              :max="9999"
              :style="{ width: '120px' }"
              placeholder="请输入"
              mode="button"
              class="input-demo"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import MenuItemIcon from "@/layout/components/Menu/menu-item-icon.vue";
import useGlobalProperties from "@/hooks/useGlobalProperties";
import {addMenu, getMenuTreeAll, updateMenu} from "@/api/system/menu/index";
import { deepClone, getPascalCase } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";

const proxy = useGlobalProperties();
const openState = ref(dictFilter("STATUS"));
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();
const form = ref({
  name: "",
  hide: "",
  disable: ""
});

const onReset = () => {
  form.value = { name: "", hide: "", disable: "" };
  getMenuList();
};

// 新增
const open = ref(false);
const rules = ref({
  parentId: [{ required: false, message: "请选择上级菜单" }],
  name: [{ required: true, message: "请输入菜单名称" }],
  title: [{ required: true, message: "请输入菜单标题" }],
  path: [{ required: true, message: "请输入路由路径" }],
  permission: [{ required: true, message: "请输入权限标识" }]
});

const menuType = ref([
  { name: "目录", value: 1 },
  { name: "菜单", value: 2 },
  { name: "按钮", value: 3 }
]);
const addFrom = ref<any>({
  type: 1,
  parentId: "",
  svgIcon: "",
  icon: "",
  menuName: "",
  title: "",
  isFull: false,
  permission: "",
  path: "",
  component: "",
  isHide: false,
  status: 1,
  affix: 0,
  isExternal: false,
  iframe: 0,
  sort: 1
});
const formType = ref(0); // 0新增 1修改
const title = ref("");
const formRef = ref();
const onAdd = () => {
  title.value = "新增菜单";
  formType.value = 0;
  open.value = true;
};
const handleOk = async () => {
  let state = await formRef.value.validate();
  if (state) return (open.value = true); // 校验不通过
  console.log("addFrom.value", addFrom.value);
  console.log(formType.value)
  if (formType.value == 1) {
    await updateMenu(addFrom.value);
    arcoMessage("success", "新增菜单数据成功");
  }
  else if (formType.value == 0) {
    await addMenu(addFrom.value);
    arcoMessage("success", "新增菜单数据成功");
  }

  getMenuList();
};
// 关闭对话框动画结束后触发
const afterClose = () => {
  formRef.value.resetFields();
  addFrom.value = {
    type: 1,
    parentId: "",
    svgIcon: "",
    icon: "",
    menuName: "",
    title: "",
    isFull: false,
    permission: "",
    path: "",
    component: "",
    isHide: false,
    status: 1,
    affix: 0,
    isExternal: false,
    iframe: 0,
    sort: 1
  };
};
// 修改
const onUpdate = (row: Menu.MenuOptions) => {
  let data = deepClone(row);
  delete data.children;
  if (data.parentId == "0") data.parentId = "";
  let form = {
    ...data,
  };
  formType.value = 1;
  // if (form.meta) delete form.meta;
  typeChange(form.type);
  console.log("form", form)
  addFrom.value = form;
  title.value = "修改菜单";
  open.value = true;
};
// 列表新增
// const onCurrentAdd = (record: any) => {
//   let {
//     id,
//     meta: { type }
//   } = record;
//   addFrom.value.parentId = id;
//   addFrom.value.type = type == 2 ? 3 : type;
//   title.value = "新增菜单";
//   open.value = true;
// };

// 菜单类型
const typeChange = (val: number) => {
  rules.value.parentId[0].required = val == 3;
  formRef.value.clearValidate();
};

// 去除空格
const onTrim = (val: string, key: string) => {
  addFrom.value[key] = val.trim();
};

// 菜单名称
const pathChange = (str: string) => {
  addFrom.value.path = str.trim();
  addFrom.value.menuName = getPascalCase(str.trim().replace(/[./:?=&"-]/g, "_"));
};

// 是否外链
const onIsLink = (is: boolean) => {
  // 非外链
  if (!is) {
    // 关联iframe和link
    addFrom.value.iframe = false;
    addFrom.value.component = "";
  } else {
    // 外链
    addFrom.value.component = "link/external/external";
  }
};

const onSearch = () => getMenuList();
const loading = ref(false);
const tableRef = ref();
const tableTree = ref([]);
const menuTree = ref<any>([]);
const getMenuList = async () => {
  try {
    loading.value = true;
    let params = {
      page: 1,
      size: 10,
      ...form.value
    };
    let res  = await getMenuTreeAll(params);
    // 语言翻译
    translation(res.data);
    // 列表数据
    tableTree.value = res.data;
    // 过滤type:3的节点，该节点是按钮权限，不显示在菜单中-用于下拉选择
    menuTree.value = filterTree(res.data);
  } finally {
    loading.value = false;
  }
};

const expand = ref(false);
const onExpand = () => {
  expand.value = !expand.value;
  tableRef.value.expandAll(expand.value);
};

// 语言转化
const translation = (tree: any) => {
  tree.forEach((item: any) => {
    if (item.children) translation(item.children);
    if (item.title) {
      item.i18n = proxy.$t(`menu.${item.title}`);
    }
  });
};

/**
 * 过滤type:3的节点，该节点是按钮权限，不显示在菜单中-用于下拉选择
 * @param {object} nodes 路由树
 * @returns 节点过滤后的路由树
 */
const filterTree = (nodes: Menu.MenuOptions[]) => {
  // 过滤当前层级的节点，排除 type 为 3 的节点
  return nodes
    .filter((node: any) => node.type !== 3)
    .map((node: any) => {
      // 创建新节点以避免修改原数据
      const newNode = { ...node };
      // 递归处理子节点
      if (newNode.children) {
        const filteredChildren = filterTree(newNode.children);
        if (filteredChildren.length > 0) {
          newNode.children = filteredChildren;
        } else {
          // 若子节点全被过滤，删除 children 属性
          delete newNode.children;
        }
      }
      return newNode;
    });
};

getMenuList();
</script>

<style lang="scss" scoped>
:deep(.arco-typography code) {
  font-size: 100%;
}
</style>
