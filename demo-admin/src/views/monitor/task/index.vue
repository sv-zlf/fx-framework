<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.taskName" placeholder="请输入任务名称" allow-clear />
            <a-select placeholder="任务分组" v-model="searchForm.taskGroup" style="width: 120px" allow-clear>
              <a-option value="DEFAULT">默认分组</a-option>
              <a-option value="SYSTEM">系统分组</a-option>
              <a-option value="BUSINESS">业务分组</a-option>
            </a-select>
            <a-select placeholder="状态" v-model="searchForm.status" style="width: 120px" allow-clear>
              <a-option :value="0">正常</a-option>
              <a-option :value="1">暂停</a-option>
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
          </a-space>
        </template>
      </s-layout-tools>

      <a-table
        row-key="id"
        :data="taskList"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1600 }"
        :pagination="pagination"
      >
        <template #columns>
          <a-table-column title="序号" :width="64" :fixed="'left'">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="任务名称" data-index="taskName" :width="150" ellipsis tooltip></a-table-column>
          <a-table-column title="任务分组" data-index="taskGroup" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="Cron表达式" data-index="cronExpression" :width="150" ellipsis tooltip></a-table-column>
          <a-table-column title="调用目标" data-index="invokeTarget" :width="200" ellipsis tooltip></a-table-column>
          <a-table-column title="描述" data-index="description" :width="200" ellipsis tooltip></a-table-column>
          <a-table-column title="状态" :width="80" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="arcoblue" v-if="record.status === 0">正常</a-tag>
              <a-tag bordered size="small" color="red" v-else>暂停</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="并发" :width="80" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="green" v-if="record.concurrent === 1">允许</a-tag>
              <a-tag bordered size="small" color="orange" v-else>禁止</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="执行次数" data-index="executionCount" :width="100" align="center"></a-table-column>
          <a-table-column title="失败次数" data-index="failureCount" :width="100" align="center"></a-table-column>
          <a-table-column title="上次执行时间" data-index="lastExecutionTime" :width="180" ellipsis tooltip></a-table-column>
          <a-table-column title="上次执行结果" data-index="lastExecutionResult" :width="200" ellipsis tooltip></a-table-column>
          <a-table-column title="操作" :width="280" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" status="success" size="mini" @click="onExecute(record)" :disabled="record.status === 1">
                  <template #icon><icon-play-circle /></template>
                  <span>执行</span>
                </a-button>
                <a-button type="primary" size="mini" @click="onPause(record)" :disabled="record.status === 1">
                  <template #icon><icon-pause-circle /></template>
                  <span>暂停</span>
                </a-button>
                <a-button type="primary" status="success" size="mini" @click="onResume(record)" :disabled="record.status === 0">
                  <template #icon><icon-check-circle /></template>
                  <span>恢复</span>
                </a-button>
                <a-button type="primary" size="mini" @click="onEdit(record)">
                  <template #icon><icon-edit /></template>
                  <span>修改</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该任务吗？" @ok="onDelete(record)">
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

    <!-- 新增/编辑弹窗 -->
    <a-modal :width="dialogWidth('50%')" v-model:visible="open" @close="afterClose" @ok="handleOk" @cancel="afterClose">
      <template #title> {{ dialogTitle }} </template>
      <div>
        <a-form ref="formRef" auto-label-width :layout="formLayout" :rules="rules" :model="form">
          <a-form-item field="taskName" label="任务名称" validate-trigger="blur">
            <a-input v-model="form.taskName" placeholder="请输入任务名称" allow-clear />
          </a-form-item>
          <a-form-item field="taskGroup" label="任务分组" validate-trigger="blur">
            <a-input v-model="form.taskGroup" placeholder="请输入任务分组" allow-clear />
          </a-form-item>
          <a-form-item field="cronExpression" label="Cron表达式" validate-trigger="blur">
            <a-input v-model="form.cronExpression" placeholder="例如: 0/5 * * * * ?" allow-clear />
            <a-link href="https://cron.qqe2.com/" target="_blank" style="margin-left: 8px;">Cron表达式生成器</a-link>
          </a-form-item>
          <a-form-item field="invokeTarget" label="调用目标" validate-trigger="blur">
            <a-input v-model="form.invokeTarget" placeholder="例如: com.fxly.demo.api.core.task.SampleTask" allow-clear />
          </a-form-item>
          <a-form-item field="description" label="任务描述" validate-trigger="blur">
            <a-textarea v-model="form.description" placeholder="请输入任务描述" allow-clear />
          </a-form-item>
          <a-form-item field="status" label="任务状态">
            <a-radio-group v-model="form.status">
              <a-radio :value="0">正常</a-radio>
              <a-radio :value="1">暂停</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item field="concurrent" label="并发执行">
            <a-radio-group v-model="form.concurrent">
              <a-radio :value="1">允许</a-radio>
              <a-radio :value="0">禁止</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item field="remark" label="备注">
            <a-textarea v-model="form.remark" placeholder="请输入备注" allow-clear />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { getPageList, saveOrUpdate, deleteTask, executeTask, pauseTask, resumeTask } from "@/api/system/task";
import { useLayoutModel } from "@/hooks/useLayoutModel";
import { deepClone } from "@/utils";

defineOptions({ name: "crontab" });

const { dialogWidth, formLayout, tableFixed } = useLayoutModel();

const searchForm = ref({
  taskName: "",
  taskGroup: null,
  status: null,
});

const loading = ref(false);
const taskList = ref([]);
const pagination = ref({
  total: null,
  current: 1,
  pageSize: 10,
  showPageSize: true,
  showTotal: true,
  onChange: (current: number) => {
    pagination.value.current = current;
    getTaskList();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getTaskList();
  }
});

const getTaskList = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize,
  };
  let res = await getPageList(params);
  taskList.value = res.data.records || [];
  pagination.value.total = res.data.total;
  loading.value = false;
};

const search = () => {
  pagination.value.current = 1;
  getTaskList();
};

const reset = () => {
  searchForm.value = {
    taskName: "",
    taskGroup: null,
    status: null,
  };
  pagination.value.current = 1;
  getTaskList();
};

// 新增
const open = ref(false);
const dialogTitle = ref("");
const formRef = ref();
const formType = ref(0); // 0-新增, 1-编辑
const form = ref({
  taskName: "",
  taskGroup: "DEFAULT",
  cronExpression: "",
  invokeTarget: "",
  description: "",
  status: 0,
  concurrent: 1,
  remark: ""
});
const rules = {
  taskName: [{ required: true, message: "请输入任务名称" }],
  taskGroup: [{ required: true, message: "请输入任务分组" }],
  cronExpression: [{ required: true, message: "请输入Cron表达式" }],
  invokeTarget: [{ required: true, message: "请输入调用目标" }]
};

const onAdd = () => {
  dialogTitle.value = "新增任务";
  formType.value = 0;
  open.value = true;
};

const onEdit = (record: any) => {
  dialogTitle.value = "修改任务";
  formType.value = 1;
  form.value = deepClone(record);
  open.value = true;
};

const handleOk = async () => {
  let state = await formRef.value.validate();
  if (state) return (open.value = true);
  
  let res = await saveOrUpdate(form.value);
  arcoMessage("success", res.msg || "保存成功");
  open.value = false;
  getTaskList();
};

const afterClose = () => {
  formRef.value.resetFields();
  form.value = {
    taskName: "",
    taskGroup: "DEFAULT",
    cronExpression: "",
    invokeTarget: "",
    description: "",
    status: 0,
    concurrent: 1,
    remark: ""
  };
};

const onDelete = async (record: any) => {
  await deleteTask(record.id);
  arcoMessage("success", "删除成功");
  getTaskList();
};

const onExecute = async (record: any) => {
  let res = await executeTask(record.id);
  arcoMessage("success", res.msg || "执行成功");
  getTaskList();
};

const onPause = async (record: any) => {
  let res = await pauseTask(record.id);
  arcoMessage("success", res.msg || "暂停成功");
  getTaskList();
};

const onResume = async (record: any) => {
  let res = await resumeTask(record.id);
  arcoMessage("success", res.msg || "恢复成功");
  getTaskList();
};

getTaskList();
</script>

<style lang="scss" scoped>
</style>
