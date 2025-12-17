<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.dictTypeName" placeholder="请输入字典名称" allow-clear />
            <a-input v-model="searchForm.dictTypeCode" placeholder="请输入字典编码" allow-clear />
            <a-select placeholder="启用状态" v-model="searchForm.status" style="width: 120px" allow-clear>
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
                <a-popconfirm type="warning" content="确定删除该字典吗?" @ok="onDelete(record.id)">
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

    <a-modal :width="dialogWidth('30%')" v-model:visible="open" @close="afterClose" @ok="handleOk" @cancel="afterClose">
      <template #title> {{ title }} </template>
      <div>
        <a-form ref="formRef" auto-label-width :layout="formLayout" :rules="rules" :model="form">
          <a-form-item field="dictTypeName" label="字典名称" validate-trigger="blur">
            <a-input v-model="form.dictTypeName" placeholder="请输入字典名称" allow-clear />
          </a-form-item>
          <a-form-item field="dictTypeCode" label="字典编码" validate-trigger="blur">
            <a-input v-model="form.dictTypeCode" placeholder="请输入字典编码" allow-clear />
          </a-form-item>
          <a-form-item field="remark" label="备注" validate-trigger="blur">
            <a-textarea v-model="form.remark" placeholder="请输入字典备注" allow-clear />
          </a-form-item>
          <a-form-item field=" " label="状态" validate-trigger="blur">
            <a-switch type="round" :checked-value="1" :unchecked-value="0" v-model="form.status">
              <template #checked> 启用 </template>
              <template #unchecked> 禁用 </template>
            </a-switch>
          </a-form-item>
        </a-form>
      </div>
    </a-modal>

    <a-modal :width="dialogWidth('50%')" v-model:visible="detailOpen" @ok="detailOk" ok-text="关闭" :hide-cancel="true">
      <template #title> 字典详情 </template>
      <div>
        <a-row>
          <a-space wrap>
            <a-button type="primary" status="success" @click="onAddDetail">
              <template #icon><icon-plus /></template>
              <span>新增</span>
            </a-button>
<!--            <a-button type="primary" status="danger" @click="onDeleteDetailBatch">-->
<!--              <template #icon><icon-delete /></template>-->
<!--              <span>删除</span>-->
<!--            </a-button>-->
          </a-space>
        </a-row>

        <a-table
          row-key="id"
          :data="dictItemList"
          :bordered="{ cell: true }"
          :loading="detailLoading"
          :scroll="{ x: '100%', y: '100%' }"
          :pagination="dictItemPage"
          :row-selection="{ type: 'checkbox', showCheckedAll: true }"
          :selected-keys="selectedKeysDetail"
          @select="selectDetail"
          @select-all="selectAllDetail"
        >
          <template #columns>
            <a-table-column title="序号" :width="64">
              <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
            </a-table-column>
            <a-table-column title="字典名" data-index="dictItemName" :width="200" ellipsis tooltip></a-table-column>
            <a-table-column title="字典值" data-index="dictItemCode" :width="200" ellipsis tooltip></a-table-column>
            <a-table-column title="状态" :width="100" align="center" ellipsis tooltip>
              <template #cell="{ record }">
                <a-tag bordered size="small" color="arcoblue" v-if="record.status === 1">启用</a-tag>
                <a-tag bordered size="small" color="red" v-else>禁用</a-tag>
              </template>
            </a-table-column>
            <a-table-column title="操作" align="center" :width="200" :fixed="tableFixed">
              <template #cell="{ record }">
                <a-space>
                  <a-button type="primary" size="mini" @click="onDetailUpdate(record)">
                    <template #icon><icon-edit /></template>
                    <span>修改</span>
                  </a-button>
                  <a-popconfirm type="warning" content="确定删除该字典吗?" @ok="onDeleteDetail(record.id)">
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
    </a-modal>

    <a-modal
      :width="dialogWidth('30%')"
      v-model:visible="detailCaseOpen"
      @close="afterCloseDetail"
      @ok="handleOkDetail"
      @cancel="afterCloseDetail"
    >
      <template #title> {{ detailTitle }} </template>
      <div>
        <a-form ref="detailFormRef" auto-label-width :layout="formLayout" :rules="detaulRules" :model="deatilForm">
          <a-form-item field="dictItemName" label="字典名称" validate-trigger="blur">
            <a-input v-model="deatilForm.dictItemName" placeholder="请输入字典名称" allow-clear />
          </a-form-item>
          <a-form-item field="dictItemCode" label="字典值" validate-trigger="blur">
            <a-input v-model="deatilForm.dictItemCode" placeholder="请输入字典值" allow-clear />
          </a-form-item>
          <a-form-item field="remark" label="状态" validate-trigger="blur">
            <a-switch type="round" :checked-value="1" :unchecked-value="0" v-model="deatilForm.status">
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
import {
  addDictItem,
  addDictType, deleteDictItem, deleteDictType,
  getDictItemPageList,
  getListByCode,
  getPageList,
  updateDictItem,
  updateDictType
} from "@/api/system/dict/index";
import { deepClone } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";

const { dialogWidth, formLayout, tableFixed } = useLayoutModel();
const openState = ref(dictFilter("STATUS"));
const searchForm = ref({
  dictTypeName: "",
  dictTypeCode: "",
  status: null
});
const search = () => {
  getDict();
};
const reset = () => {
  searchForm.value = {
    dictTypeName: "",
    dictTypeCode: "",
    status: null
  };
  getDict();
};

const open = ref<boolean>(false);
const title = ref<string>("");
const rules = {
  dictTypeName: [
    {
      required: true,
      message: "请输入字典名称"
    }
  ],
  dictTypeCode: [
    {
      required: true,
      message: "请输入字典编码"
    }
  ],
  status: [
    {
      required: true,
      message: "请选择状态"
    }
  ]
};
const form = ref({
  dictTypeName: "",
  dictTypeCode: "",
  remark: "",
  status: 1
});
const formRef = ref();
const formType = ref(0); // 0新增 1修改

// 新增字典
const onAdd = () => {
  open.value = true;
  title.value = "新增字典";
  formType.value = 0;
};

// 更新字典
const onUpdate = (record: any) => {
  title.value = "修改字典";
  form.value = deepClone(record);
  open.value = true;
  formType.value = 1;
};

// 删除字典
const onDelete = async (id: any) => {
  await deleteDictType(id);
  arcoMessage("success", "删除成功");
  getDict();
}

// 提交
const handleOk = async () => {
  let state = await formRef.value.validate();
  if (state) return (open.value = true); // 校验不通过
  if (formType.value === 0){
    await addDictType(form.value);
    arcoMessage("success", "新增成功");
  }
  else if (formType.value === 1){
    await updateDictType(form.value);
    arcoMessage("success", "更新成功");
  }
  getDict();
};


const selectedKeys = ref([]);
const select = (list: []) => {
  selectedKeys.value = list;
};
const selectAll = (state: boolean) => {
  selectedKeys.value = state ? (dictList.value.map((el: any) => el.id) as []) : [];
};
// 关闭对话框动画结束后触发
const afterClose = () => {
  formRef.value.resetFields();
  form.value = {
    dictTypeName: "",
    dictTypeCode: "",
    remark: "",
    status: 1
  };
};


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
    getDict();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getDict();
  }
});
const dictList = ref();
const getDict = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize
  };
  let res = await getPageList(params);
  dictList.value = res.data.records || [];
  pagination.value.total = res.data.total;
  loading.value = false;
};

// ========================= 字典详情 ====================

//  字典详情分页
const dictItemPage = ref({
  total: null,
  current:1,
  pageSize: 10,
  showPageSize:true,
  showTotal: true,
  onChange: (current: number) => {
    dictItemPage.value.current = current;
    getDictItem();
  },
  onPageSizeChange: (pageSize: number) => {
    dictItemPage.value.current = 1;
    dictItemPage.value.pageSize = pageSize;
    getDictItem();
  }
});
const detailFormType = ref(0);
const dictTypeCode = ref("");
const detailLoading = ref(false);
const detailOpen = ref(false);
const dictItemList = ref([]);
const deatilForm = ref({
  dictItemName: "",
  dictItemCode: "",
  status: 1,
  dictTypeCode:""
});
const detaulRules = ref({
  dictItemName: [
    {
      required: true,
      message: "请输入字典名称"
    }
  ],
  dictItemCode: [
    {
      required: true,
      message: "请输入字典值"
    }
  ],
  status: [
    {
      required: true,
      message: "请选择状态"
    }
  ]
});

// 获取字典详情
const getDictItem = async () => {
  detailLoading.value = true;
  const params = {
    dictTypeCode: dictTypeCode.value,
    pageIndex: dictItemPage.value.current,
    pageSize: dictItemPage.value.pageSize
  };
  let res = await getDictItemPageList(params);
  dictItemList.value = res.data.records || [];
  dictItemPage.value.total = res.data.total;
  detailLoading.value = false;
};

const onDictData = async (record: any) => {
  dictTypeCode.value = record.dictTypeCode;
  deatilForm.value.dictTypeCode = record.dictTypeCode;
  await getDictItem();
  detailOpen.value = true;
};

const detailOk = () => {
  detailOpen.value = false;
};
const detailFormRef = ref();
const detailTitle = ref("");
const detailCaseOpen = ref(false);

// 新增字典数据
const onAddDetail = () => {
  detailTitle.value = "新增字典数据";
  detailCaseOpen.value = true;
  detailFormType.value = 0;
};

// 修改字典数据
const onDetailUpdate = (record: any) => {
  detailTitle.value = "修改字典数据";
  let detail = deepClone(record);
  detail.value = String(detail.value);
  deatilForm.value = detail;
  detailFormType.value = 1;
  detailCaseOpen.value = true;
};

// 删除字典数据
const onDeleteDetail = async (id: any) => {
  await deleteDictItem(id);
  arcoMessage("success", "删除成功");
  getDictItem();
}
// 提交字典数据
const handleOkDetail = async () => {
  let state = await detailFormRef.value.validate();
  if (state) return (detailCaseOpen.value = true); // 校验不通过
  if (detailFormType.value === 0){
    await addDictItem(deatilForm.value);
    arcoMessage("success", "新增成功");
  }
  else if (detailFormType.value === 1){
    await updateDictItem(deatilForm.value);
    arcoMessage("success", "更新成功");
  }
  getDictItem();
};

// 关闭对话框动画结束后触发
const afterCloseDetail = () => {
  detailFormRef.value.resetFields();
  deatilForm.value = {
    dictItemName: "",
    dictItemCode: "",
    status: 1,
    dictTypeCode:""
  };
};
const selectedKeysDetail = ref([]);
const selectDetail = (list: []) => {
  selectedKeysDetail.value = list;
};
const selectAllDetail = (state: boolean) => {
  selectedKeysDetail.value = state ? (dictItemList.value.map((el: any) => el.id) as []) : [];
};

getDict();
</script>
