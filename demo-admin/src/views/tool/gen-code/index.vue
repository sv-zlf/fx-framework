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
              <span>创建表</span>
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
        :data="tableList"
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
          <a-table-column title="序号" align="center" :width="64">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="表名" align="center" data-index="tableName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="类名" align="center" data-index="className" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="表注释" align="center" data-index="tableComment" :width="180" ellipsis tooltip></a-table-column>
          <a-table-column title="生成方式" align="center"  data-index="generateType" :width="180">
            <template #cell="{ record }">
              <a-select
                v-model="record.generateType"
                style="width: 100%"
                placeholder="请选择生成方式"
                @change="(value) => handleGenerateTypeChange(record, value)"
              >
              <!-- 选项配置：0-压缩包，1-本地工程模块 -->
              <a-option :value="0" label="压缩包"></a-option>
              <a-option :value="1" label="本地工程"></a-option>
              </a-select>
            </template>
          </a-table-column>
          <a-table-column title="模块名" align="center" data-index="moudleName" :width="180"></a-table-column>
          <a-table-column title="作者" align="center" data-index="author" :width="180"></a-table-column>
          <a-table-column title="操作" :width="200" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" status="warning" size="mini" @click="handleGenerate(record)">
                  <template #icon><icon-edit /></template>
                  <span>生成</span>
                </a-button>
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
<!--    创建表-->
    <a-modal :width="dialogWidth()" v-model:visible="openTable" @close="handleTableCancle" @ok="handleTableAdd" @cancel="handleTableCancle">
      <template #title> {{ title }} </template>
      <div>
        <a-form ref="formTableRef" auto-label-width :layout="formLayout" :rules="tableRules" :model="tableFrom">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item field="moduleName" label="模块名" validate-trigger="blur">
                <a-input v-model="tableFrom.moduleName" placeholder="请输入模块名" allow-clear />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :span="24">
              <a-form-item
                field="sql"
                validate-trigger="blur"
                label="建表语句："
                label-col-style="white-space: pre-line;"
              >
                <a-textarea
                  v-model="tableFrom.sql"
                  placeholder="请粘贴或输入建表SQL语句"
                  allow-clear
                  :auto-size="{ minRows:15,maxRows:50}"
                  style="width: 100%;"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-modal>
    
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
import {createCode, createTable, getPageList, updateTable} from "@/api/tool/gen";
import {deleteBatch, deleteUser, saveOrUpdate} from "@/api/system/user";
import {quickDownloadFile} from "@/utils/download";

const router = useRouter();
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();

/**
 * 列表
 */
const searchForm = ref({
  tableName: "",
});
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
    getTable();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getTable();
  }
});

const search = () => {
  getTable();
};
const reset = () => {
  searchForm.value = {
    tableName: "",
  };
  getTable();
};
// 账户
const tableList = ref();
const getTable = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize,
  };
  let res = await getPageList(params);
  tableList.value = res.data.records;
  pagination.value.total = res.data.total;
  loading.value = false;
};

/**
 * 创建表
 */
const openTable = ref(false);
const formTableRef = ref();
const tableRules = {
  sql: [
    {
      required: true,
      message: "请输入建表语句"
    }
  ],
  moduleName: [
    {
      required: true,
      message: "请输入模块名"
    }
  ],
};
const tableFrom = ref<any>({
  sql: "",
  moduleName: ""
});
const onAdd = () => {
  title.value = "创建表";
  openTable.value = true;
};

const handleTableAdd = async () => {
  let state = await formTableRef.value.validate();
  if (state) return (openTable.value = true); // 校验不通过
  await createTable(tableFrom.value)
  arcoMessage("success", "提交成功");
  getTable();
};

const handleTableCancle = () => {
  openTable.value = false;
  tableFrom.value = {};
};

/**
 * 修改表
 */
// 处理生成方式变化（防抖版：延迟500ms执行，避免频繁请求）
const handleGenerateTypeChange = debounce(async (record: any, value: string | number) => {
  try {
    record.updating = true;
    record.columnList = []; // 无需更新字段信息
    await updateTable(record);
    arcoMessage("success", `生成方式已更新为：${value === '0' ? '压缩包' : '本地工程'}`);
  } catch (error) {
    arcoMessage("error", "更新失败，请重试");
  } finally {
    record.updating = false;
  }
}, 500); // 防抖延迟：500ms

// 生成表代码
const handleGenerate = async (record: any) => {
  try {
    const params = {
      tableId: record.id
    };
    if (record.generateType == 0){
      await quickDownloadFile('/api/tool/gen/genCodeZip', params, 'code.zip');
    }
    else {
      await createCode(params);
      arcoMessage("success", "代码生成成功");
    }
  } catch (error) {
    arcoMessage("error", "生成失败，请重试");
  }
};

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
  getTable();
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
  selectedKeys.value = state ? (tableList.value.map((el: any) => el.id) as []) : [];
};
// 删除
const onDelete = async (row: any) => {
    await deleteUser(row.id);
    arcoMessage("success", "删除成功");
    getTable();
}
// 批量删除
const onDeleteBatch = async () => {
  if (selectedKeys.value.length === 0){
    return arcoMessage("warning", "请选择要删除的用户");
  }
  await deleteBatch(selectedKeys.value);
  arcoMessage("success", "删除成功");
  getTable();
}



onMounted(() => {
  getTable();
});
</script>

<style lang="scss" scoped>
</style>
