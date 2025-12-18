<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.tableName" placeholder="请输入表名称" allow-clear />
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
<!--            <a-button type="primary" status="danger" @click="onDeleteBatch">-->
<!--              <template #icon><icon-delete /></template>-->
<!--              <span>删除</span>-->
<!--            </a-button>-->
          </a-space>
        </template>
      </s-layout-tools>

      <a-table
        row-key="id"
        :data="accountList"
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
          <a-table-column title="表名" data-index="tableName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="类名" data-index="className" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="表注释" data-index="tableComment" :width="180" ellipsis tooltip></a-table-column>
          <a-table-column title="生成方式" data-index="generateType" :width="180"></a-table-column>
          <a-table-column title="模块名" data-index="moudleName" :width="180"></a-table-column>
          <a-table-column title="作者" data-index="author" :width="180"></a-table-column>
          <a-table-column title="操作" :width="200" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" size="mini" @click="onUpdate(record)">
                  <template #icon><icon-edit /></template>
                  <span>修改</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该用户吗?" @ok="onDelete(record)">
                  <a-button type="primary" status="danger" size="mini" :disabled="record.admin">
                    <template #icon><icon-delete /></template>
                    <span>删除</span>
                  </a-button>
                </a-popconfirm>
<!--                <a-tooltip content="用户详情">-->
<!--                  <a-button type="primary" status="success" size="mini" @click="onDetail(record)">-->
<!--                    <template #icon>-->
<!--                      <icon-more />-->
<!--                    </template>-->
<!--                  </a-button>-->
<!--                </a-tooltip>-->
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
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item field="userName" label="用户名称" validate-trigger="blur">
                <a-input v-model="from.userName" placeholder="请输入用户名称" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item field="nickName" label="昵称" validate-trigger="blur">
                <a-input v-model="from.nickName" placeholder="请输入昵称" allow-clear />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item field="phone" label="手机号码" validate-trigger="blur">
                <a-input v-model="from.phone" placeholder="请输入手机号码" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item field="email" label="邮箱" validate-trigger="blur">
                <a-input v-model="from.email" placeholder="请输入邮箱" allow-clear />
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item  label="角色" validate-trigger="blur">
            <a-select v-model="from.roles" multiple placeholder="请选择角色" >
              <a-option
                v-for="item in roleList"
                :key="item.id"
                :value="item.id"
                :label="item.roleName"
              ></a-option>
            </a-select>
          </a-form-item>
          <a-form-item field="status" label="状态" validate-trigger="blur">
            <a-switch type="round" :checked-value="1" :unchecked-value="0" v-model="from.status">
              <template #checked> 启用 </template>
              <template #unchecked> 禁用 </template>
            </a-switch>
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { deepClone } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";
import { getPageList} from "@/api/tool/gen";

const router = useRouter();
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();

const searchForm = ref({
  tableName: "",
});
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
const from = ref<any>({
  userName: "",
  nickName: "",
  phone: "",
  email: "",
  roles: [],
  status: 1,
});
const title = ref("");
const formRef = ref();

//新增
const onAdd = () => {
  title.value = "新增用户";
  open.value = true;
};
// 更新
const onUpdate = (row: any) => {
  title.value = "修改用户";
  from.value = deepClone(row);
  open.value = true;
};
// 提交
const handleOk = async () => {
  let state = await formRef.value.validate();
  if (state) return (open.value = true); // 校验不通过
  await saveOrUpdate(from.value)
  arcoMessage("success", "提交成功");
  getAccount();
};
// 关闭对话框动画结束后触发
const afterClose = () => {
  formRef.value.resetFields();
  from.value = {
    userName: "",
    nickName: "",
    phone: "",
    email: "",
    roles: [],
    status: 1,
  };
};

// 选择
const selectedKeys = ref([]);
const select = (list: []) => {
  selectedKeys.value = list;
};
const selectAll = (state: boolean) => {
  selectedKeys.value = state ? (accountList.value.map((el: any) => el.id) as []) : [];
};
// 删除
const onDelete = async (row: any) => {
    await deleteUser(row.id);
    arcoMessage("success", "删除成功");
    getAccount();
}
// 批量删除
const onDeleteBatch = async () => {
  if (selectedKeys.value.length === 0){
    return arcoMessage("warning", "请选择要删除的用户");
  }
  await deleteBatch(selectedKeys.value);
  arcoMessage("success", "删除成功");
  getAccount();
}

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
    getAccount();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getAccount();
  }
});

const search = () => {
  getAccount();
};
const reset = () => {
  searchForm.value = {
    tableName: "",
  };
  getAccount();
};
// 账户
const accountList = ref();
const getAccount = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize,
  };
  let res = await getPageList(params);
  accountList.value = res.data.records;
  pagination.value.total = res.data.total;
  loading.value = false;
};

// 角色列表
const roleList = ref<any>([]);
const getRole = async () => {
  const roleName = "";
  let res = await getRoleList(roleName);
  roleList.value = res.data;
};

onMounted(() => {
  getAccount();
  getRole();
});
</script>

<style lang="scss" scoped>
</style>
