<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.roleName" placeholder="请输入角色名称" allow-clear />
            <a-input v-model="searchForm.roleCode" placeholder="请输入角色标识" allow-clear />
            <a-select placeholder="角色状态" v-model="searchForm.status" style="width: 120px" allow-clear>
              <a-option v-for="item in openState" :key="item.id" :value="item.dictItemCode">{{ item.dictItemName }}</a-option>
            </a-select>
            <a-button type="primary" @click="search">
              <template #icon><icon-search /></template>
              <span>查询</span>
            </a-button>
            <a-button @click="reset">
              <template #icon><icon-refresh /></template>
              <span>重置</span>
            </a-button>
          </a-space>
        </template>
        <template #right>
          <a-space wrap>
            <a-button type="primary" status="success" @click="onAdd">
              <template #icon><icon-plus /></template>
              <span>新增</span>
            </a-button>
<!--            <a-button type="primary" status="danger">-->
<!--              <template #icon><icon-delete /></template>-->
<!--              <span>删除</span>-->
<!--            </a-button>-->
          </a-space>
        </template>
      </s-layout-tools>
      <a-table
        row-key="id"
        :data="roleList"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1000 }"
        :pagination="pagination"
        :row-selection="{ type: 'checkbox', showCheckedAll: true }"
        :selected-keys="selectedKeys"
        @select="select"
        @select-all="selectAll"
      >
        <template #columns>
          <a-table-column title="序号" :width="64">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="角色名称" data-index="roleName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="角色标识" data-index="roleCode" :width="150" ellipsis tooltip></a-table-column>
          <a-table-column title="状态" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="arcoblue" v-if="record.status === 1">启用</a-tag>
              <a-tag bordered size="small" color="red" v-else>禁用</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="描述" data-index="description" ellipsis tooltip></a-table-column>
          <a-table-column title="创建时间" data-index="createTime" :width="180"></a-table-column>
          <a-table-column title="操作" :width="280" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" status="success" size="mini" :disabled="record.admin" @click="onPrivileges(record)">
                  <template #icon><icon-safe /></template>
                  <span>分配权限</span>
                </a-button>
                <a-button type="primary" size="mini" :disabled="record.admin" @click="onUpdate(record)">
                  <template #icon><icon-edit /></template>
                  <span>修改</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该角色吗?"  @ok="onDelete(record.id)">
                  <a-button type="primary" status="danger" size="mini" :disabled="record.admin">
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
        <a-form ref="formRef" auto-label-width :layout="formLayout" :rules="rules" :model="from">
          <a-form-item field="name" label="角色名称" validate-trigger="blur">
            <a-input v-model="from.roleName" placeholder="请输入角色名称" allow-clear />
          </a-form-item>
          <a-form-item field="code" label="角色编码" validate-trigger="blur">
            <a-input v-model="from.roleCode" placeholder="请输入角色编码" allow-clear />
          </a-form-item>
          <a-form-item field="status" label="状态" validate-trigger="blur">
            <a-switch type="round" :checked-value="1" :unchecked-value="0" v-model="from.status">
              <template #checked> 启用 </template>
              <template #unchecked> 禁用 </template>
            </a-switch>
          </a-form-item>
          <a-form-item field="description" label="描述" validate-trigger="blur">
            <a-textarea v-model="from.description" placeholder="请输入描述" allow-clear />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>

    <a-drawer :visible="drawerOpen" :width="dialogWidth('500px', '100%')" @ok="drawerOk" @cancel="drawerCancel">
      <template #title> 分配权限 </template>
      <div>
        <a-card>
          <a-row :gutter="24" justify="center">
            <a-col :span="8">
              <span class="text-right-gap">展开全部</span>
              <a-switch type="round" v-model="treeSwitch.expandAll" @change="onExpandAll">
                <template #checked> 是 </template>
                <template #unchecked> 否 </template>
              </a-switch>
            </a-col>
            <a-col :span="8">
              <span class="text-right-gap">全选节点</span>
              <a-switch type="round" v-model="treeSwitch.selectAll" @change="onSelectAll">
                <template #checked> 是 </template>
                <template #unchecked> 否 </template>
              </a-switch>
            </a-col>
            <a-col :span="8">
              <a-tooltip
                content="权限树的父子节点独立，因为若节点关联，父节点会存在半选情况，半选节点的ID不会返回，会导致菜单无法渲染"
              >
                <span>父子关联 <icon-question-circle-fill /></span>
              </a-tooltip>
            </a-col>
          </a-row>
        </a-card>

        <a-tree
          ref="treeRef"
          :fieldNames="{
            key: 'id',
            title: 'i18n',
            children: 'children',
            icon: ''
          }"
          :check-strictly="true"
          :checkable="true"
          :show-line="true"
          :unmount-on-close="true"
          v-model:checked-keys="permissionKeys"
          :data="permissionTree"
        />
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import useGlobalProperties from "@/hooks/useGlobalProperties";
import {addRole, bindMenu, getPageList, toBind, updateRole, deleteRole} from "@/api/system/role";
import { deepClone } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";

const proxy = useGlobalProperties();
const openState = ref(dictFilter("STATUS"));
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();
const searchForm = ref({
  roleName: "",
  roleCode: "",
  status: null
});
const search = () => {
  getRole();
};
const reset = () => {
  searchForm.value = {
    roleName: "",
    roleCode: "",
    status: null
  };
  getRole();
};

// 新增
const open = ref(false);
const rules = {
  roleName: [{ required: true, message: "请输入角色名称" }],
  roleCode: [{ required: true, message: "请输入角色编码" }],
  status: [{ required: true, message: "请选择状态" }]
};
const from = ref<any>({
  roleName: "",
  roleCode: "",
  status: 1,
  description: ""
});
const formType = ref(0); // 0新增 1修改
const title = ref("");
const formRef = ref();
const onAdd = () => {
  title.value = "新增角色";
  open.value = true;
  formType.value = 0;
};
// 关闭对话框动画结束后触发
const afterClose = () => {
  formRef.value.resetFields();
  from.value = {
    name: "",
    code: "",
    status: 1,
    sort: 1,
    description: ""
  };
};
// 修改角色
const onUpdate = (row: any) => {
  title.value = "修改角色";
  from.value = deepClone(row);
  open.value = true;
  formType.value = 1;
};

// 删除角色
const onDelete = async (id: any) => {
  await deleteRole( id);
  arcoMessage("success", "删除成功");
  getRole();
}
const handleOk = async () => {
  let state = await formRef.value.validate();
  // 校验不通过
  if (state) return (open.value = true);
  if (formType.value === 0){
    await addRole(from.value)
    arcoMessage("success", "新增成功");
  }
  if (formType.value === 1) {
    await updateRole(from.value)
    arcoMessage("success", "更新成功");
  }
  getRole();
};

// 获取列表
const loading = ref(false);
//  分页
const pagination = ref({
  total: null,
  current:1,
  pageSize: 10,
  showPageSize:true,
  showTotal: true,
  onChange: (current: number) => {
    pagination.value.current = current;
    getRole();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getRole();
  }
});
const roleList = ref([]);
const getRole = async () => {
  try {
    loading.value = true;
    const params = {
      ...searchForm.value,
      pageIndex: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };
    let res = await getPageList(params);
    roleList.value = res.data.records;
    pagination.value.total = res.data.total;
  } finally {
    loading.value = false;
  }
};
const selectedKeys = ref([]);
const select = (list: []) => {
  selectedKeys.value = list;
};
const selectAll = (state: boolean) => {
  selectedKeys.value = state ? (roleList.value.map((el: any) => el.id) as []) : [];
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

//  ========================    分配权限 =====================

// 获取权限树
const treeRef = ref();
const treeSwitch = ref({
  expandAll: false, // 展开全部
  selectAll: false // 全选
});
// 展开全部
const onExpandAll = (state: boolean) => {
  treeRef.value.expandAll(state);
};
// 全选
const onSelectAll = (state: boolean) => {
  treeRef.value.checkAll(state);
};

// 重置节点操作开关
const treeSwitchReset = () => {
  treeSwitch.value = {
    expandAll: false, // 固定
    selectAll: false // 全选
  };
};

const permissionTree = ref([]);
const permissionKeys = ref<(number)[]>([]);
const roleId = ref();

// 分配权限
const drawerOpen = ref(false);
const onPrivileges = async (row: any) => {
  roleId.value = row.id;
  let { data } = await toBind(roleId.value);
  translation(data);
  permissionTree.value = data;
  // permissionKeys.value = res.data;
  permissionKeys.value = extractCheckedKeys(data);
  drawerOpen.value = true;
  treeRef.value.expandAll(true);
};
// 提取选中的菜单
const extractCheckedKeys = (treeData: any[]) => {
  const checkedIds: (number)[] = []
  // 递归遍历树形结构
  const traverse = (nodes: any[]) => {
    // 边界处理：如果 nodes 不是数组，直接返回
    if (!Array.isArray(nodes) || nodes.length === 0) return;

    nodes.forEach(node => {
      // 只要节点的 isSelected 为 true，就收集其 id
      if (node.isSelected === true) {
        checkedIds.push(node.id); // id 类型需与节点 id 一致（string/number）
      }
      // 递归处理子节点（如果有 children）
      traverse(node.children);
    });
  };
  traverse(treeData);
  return checkedIds;
};
// 授权提交
const drawerOk = async () => {
  drawerOpen.value = false;
  await bindMenu(roleId.value,permissionKeys.value);
  treeSwitchReset();
  console.log(permissionKeys.value)
  arcoMessage("success", "提交成功");
  getRole();
};
// 授权取消
const drawerCancel = () => {
  drawerOpen.value = false;
  treeSwitchReset();
};

//  =======================================

getRole();
</script>

<style lang="scss" scoped>
.text-right-gap {
  margin-right: $margin;
}
</style>
