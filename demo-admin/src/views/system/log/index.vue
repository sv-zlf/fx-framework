<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.moduleName" placeholder="请输入模块名称" allow-clear />
            <a-input v-model="searchForm.userName" placeholder="请输入操作人" allow-clear />
            <a-select placeholder="操作类型" v-model="searchForm.operationType" style="width: 120px" allow-clear>
              <a-option value="CREATE">新增</a-option>
              <a-option value="UPDATE">修改</a-option>
              <a-option value="DELETE">删除</a-option>
              <a-option value="QUERY">查询</a-option>
              <a-option value="OTHER">其他</a-option>
            </a-select>
            <a-select placeholder="状态" v-model="searchForm.status" style="width: 120px" allow-clear>
              <a-option :value="1">成功</a-option>
              <a-option :value="0">失败</a-option>
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
      </s-layout-tools>

      <a-table
        row-key="id"
        :data="logList"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1800 }"
        :pagination="pagination"
      >
        <template #columns>
          <a-table-column title="序号" :width="64" :fixed="'left'">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="模块名称" data-index="moduleName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="操作类型" data-index="operationType" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag v-if="record.operationType === 'INSERT'" color="green">新增</a-tag>
              <a-tag v-else-if="record.operationType === 'UPDATE'" color="arcoblue">修改</a-tag>
              <a-tag v-else-if="record.operationType === 'DELETE'" color="red">删除</a-tag>
              <a-tag v-else-if="record.operationType === 'QUERY'" color="orangered">查询</a-tag>
              <a-tag v-else>其他</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作描述" data-index="description" :width="200" ellipsis tooltip></a-table-column>
          <a-table-column title="操作人" data-index="userName" :width="100"></a-table-column>
          <a-table-column title="IP地址" data-index="ipAddress" :width="140"></a-table-column>
          <a-table-column title="请求URL" data-index="requestUrl" :width="220" ellipsis tooltip></a-table-column>
          <a-table-column title="请求方法" data-index="requestMethod" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag v-if="record.requestMethod === 'GET'" color="blue">GET</a-tag>
              <a-tag v-else-if="record.requestMethod === 'POST'" color="green">POST</a-tag>
              <a-tag v-else-if="record.requestMethod === 'PUT'" color="arcoblue">PUT</a-tag>
              <a-tag v-else-if="record.requestMethod === 'DELETE'" color="red">DELETE</a-tag>
              <a-tag v-else>{{ record.requestMethod }}</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="状态" :width="80" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="arcoblue" v-if="record.status === 1">成功</a-tag>
              <a-tag bordered size="small" color="red" v-else>失败</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="执行时间(ms)" data-index="executionTime" :width="100" align="right"></a-table-column>
          <a-table-column title="浏览器" data-index="browser" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="操作系统" data-index="os" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="创建时间" data-index="createTime" :width="180"></a-table-column>
          <a-table-column title="操作" :width="180" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" size="mini" @click="onDetail(record)">
                  <template #icon><icon-eye /></template>
                  <span>详情</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该日志吗？" @ok="onDelete(record)">
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

    <!-- 详情弹窗 -->
    <a-modal v-model:visible="detailOpen" :width="'70%'" title="日志详情" @ok="detailOpen = false" @cancel="detailOpen = false">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="模块名称">{{ detail.moduleName }}</a-descriptions-item>
        <a-descriptions-item label="操作类型">{{ detail.operationType }}</a-descriptions-item>
        <a-descriptions-item label="操作描述">{{ detail.description }}</a-descriptions-item>
        <a-descriptions-item label="操作人">{{ detail.userName }}</a-descriptions-item>
        <a-descriptions-item label="IP地址">{{ detail.ipAddress }}</a-descriptions-item>
        <a-descriptions-item label="请求URL">{{ detail.requestUrl }}</a-descriptions-item>
        <a-descriptions-item label="请求方法">{{ detail.requestMethod }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag v-if="detail.status === 1" color="arcoblue">成功</a-tag>
          <a-tag v-else color="red">失败</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="执行时间">{{ detail.executionTime }}ms</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detail.createTime }}</a-descriptions-item>
        <a-descriptions-item label="浏览器">{{ detail.browser }}</a-descriptions-item>
        <a-descriptions-item label="操作系统">{{ detail.os }}</a-descriptions-item>
        <a-descriptions-item label="请求参数" :span="2">
          <div style="max-height: 200px; overflow-y: auto; white-space: pre-wrap;">{{ detail.requestParams }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="响应结果" :span="2">
          <div style="max-height: 200px; overflow-y: auto; white-space: pre-wrap;">{{ detail.responseData }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="错误信息" :span="2" v-if="detail.errorMsg">
          <div style="max-height: 200px; overflow-y: auto; white-space: pre-wrap;">{{ detail.errorMsg }}</div>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { getLogList, deleteLog } from "@/api/system/log";
import { useLayoutModel } from "@/hooks/useLayoutModel";

const { tableFixed } = useLayoutModel();

const searchForm = ref({
  moduleName: "",
  userName: "",
  operationType: null,
  status: null,
});

const loading = ref(false);
const logList = ref([]);
const pagination = ref({
  total: null,
  current: 1,
  pageSize: 10,
  showPageSize: true,
  showTotal: true,
  onChange: (current: number) => {
    pagination.value.current = current;
    getLog();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getLog();
  }
});

const getLog = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize,
  };
  let res = await getLogList(params);
  logList.value = res.data.records || [];
  pagination.value.total = res.data.total;
  loading.value = false;
};

const search = () => {
  pagination.value.current = 1;
  getLog();
};

const reset = () => {
  searchForm.value = {
    moduleName: "",
    userName: "",
    operationType: null,
    status: null,
  };
  pagination.value.current = 1;
  getLog();
};

const onDelete = async (record: any) => {
  await deleteLog(record.id);
  arcoMessage("success", "删除成功");
  getLog();
};

const detailOpen = ref(false);
const detail = ref({});
const onDetail = (record: any) => {
  detail.value = record;
  detailOpen.value = true;
};

onMounted(() => {
  getLog();
});
</script>

<style lang="scss" scoped>
</style>
