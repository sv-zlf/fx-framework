<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="form.dictTypeName" placeholder="请输入字典名称" allow-clear />
            <a-input v-model="form.dictTypeCode" placeholder="请输入字典编码" allow-clear />
            <a-select placeholder="启用状态" v-model="form.status" style="width: 120px" allow-clear>
              <a-option v-for="item in openState" :key="item.value" :value="item.value">{{ item.dictTypeName }}</a-option>
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
            <a-button type="primary" @click="onAdd">
              <template #icon><icon-plus /></template>
              <span>新增</span>
            </a-button>
            <a-button type="primary" status="danger">
              <template #icon><icon-delete /></template>
              <span>删除</span>
            </a-button>
          </a-space>
        </template>
      </s-layout-tools>

      <a-table
        row-key="id"
        :data="dictList"
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
          <a-table-column title="字典名称" data-index="dictTypeName" :width="220" ellipsis tooltip></a-table-column>
          <a-table-column title="字典编码" data-index="dictTypeCode" :width="220" ellipsis tooltip></a-table-column>
          <a-table-column title="状态" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="arcoblue" v-if="record.status === 1">启用</a-tag>
              <a-tag bordered size="small" color="red" v-else>禁用</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="备注" data-index="remark" ellipsis tooltip></a-table-column>
          <a-table-column title="创建时间" data-index="createTime" :width="180"></a-table-column>
          <a-table-column title="操作" :width="280" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" status="success" size="mini" @click="onDictData(record)">
                  <template #icon><icon-file /></template>
                  <span>字典值</span>
                </a-button>
                <a-button type="primary" size="mini" @click="onUpdate(record)">
                  <template #icon><icon-edit /></template>
                  <span>修改</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该字典吗?">
                  <a-button type="primary" status="danger" size="mini">
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
  </div>
</template>

<script setup lang="ts">
import { getDivisionAPI, getAccountAPI, getRoleAPI } from "@/api/modules/system/index";
import { deepClone } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";

const router = useRouter();
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();
const openState = ref(dictFilter("status"));
const sexOption = ref(dictFilter("gender"));
const form = ref({
  name: "",
  phone: "",
  status: null,
  createTime: []
});
const search = () => {
  getAccount();
};
const reset = () => {
  form.value = {
    name: "",
    phone: "",
    status: null,
    createTime: []
  };
  getAccount();
};
// 新增
const open = ref(false);
const rules = {
  userName: [
    {
      required: true,
      message: "请输入用户名称"
    }
  ],
  nickName: [
    {
      required: true,
      message: "请输入昵称"
    }
  ],
  sex: [
    {
      required: true,
      message: "请选择性别"
    }
  ],
  deptId: [
    {
      required: true,
      message: "请选择所属部门"
    }
  ],
  roles: [
    {
      required: true,
      message: "请选择角色"
    }
  ],
  status: [
    {
      required: true,
      message: "请选择状态"
    }
  ]
};
const addFrom = ref<any>({
  userName: "",
  nickName: "",
  phone: "",
  email: "",
  sex: 2,
  deptId: null,
  roles: [],
  status: 1,
  description: ""
});
const formType = ref(0); // 0新增 1修改
const title = ref("");
const formRef = ref();
const onAdd = () => {
  title.value = "新增账号";
  formType.value = 0;
  open.value = true;
};
const handleOk = async () => {
  let state = await formRef.value.validate();
  if (state) return (open.value = true); // 校验不通过
  arcoMessage("success", "模拟提交成功");
  getAccount();
};
// 关闭对话框动画结束后触发
const afterClose = () => {
  formRef.value.resetFields();
  addFrom.value = {
    userName: "",
    nickName: "",
    phone: "",
    email: "",
    sex: 2,
    deptId: null,
    roles: [],
    status: 1,
    description: ""
  };
};
const onUpdate = (row: any) => {
  title.value = "修改账号";
  formType.value = 1;
  addFrom.value = deepClone(row);
  open.value = true;
};

const onDetail = (row: any) => {
  console.log(row);

  router.push({
    path: "/system/userinfo",
    query: {
      id: row.id,
      userName: row.userName
    }
  });
};

const loading = ref(false);
const pagination = ref({
  pageSize: 10,
  showPageSize: true
});

// 账户
const accountList = ref();
const getAccount = async () => {
  loading.value = true;
  let res = await getAccountAPI();
  res.data.forEach((item: any) => item.admin && (item.disabled = true));
  accountList.value = res.data;
  loading.value = false;
};
const selectedKeys = ref([]);
const select = (list: []) => {
  selectedKeys.value = list;
};
const selectAll = (state: boolean) => {
  selectedKeys.value = state ? (accountList.value.map((el: any) => el.id) as []) : [];
};

// 部门树
const fieldNames = ref({
  key: "id",
  title: "name",
  children: "children"
});
const treeData = ref();
const treeRef = ref();
const getDivision = async () => {
  let res = await getDivisionAPI();
  treeData.value = res.data;
  setTimeout(() => {
    treeRef.value.expandAll();
  }, 0);
};
const onSelectTree = () => {
  getAccount();
};

// 角色列表
const roleList = ref<any>([]);
const getRole = async () => {
  let res = await getRoleAPI();
  roleList.value = res.data;
};

onMounted(() => {
  getDivision();
  getAccount();
  getRole();
});
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: row;
  column-gap: $padding;
  .left-box {
    display: flex;
    flex-direction: column;
    width: 260px;
    height: 100%;
    .tree-box {
      flex: 1;
      margin-top: $padding;
      overflow: auto;
    }
  }
  .right-box {
    flex: 1;
  }
}
</style>
