<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-typography-title :heading="6">{{tableName}}</a-typography-title >
          </a-space>
        </template>
        <template #right>
          <a-space wrap>
            <a-button type="primary" status="success" @click="handleUpdate">
              <span>保存</span>
            </a-button>
          </a-space>
          <a-space wrap>
            <a-button  @click="handleBack">
              <span>返回</span>
            </a-button>
          </a-space>
        </template>
      </s-layout-tools>
      <a-table
        row-key="id"
        :data="tableList"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1000 }"
      >
        <template #columns>
          <a-table-column title="序号" align="center" :width="64">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="字段名" align="center" data-index="columnName" :width="120" ellipsis tooltip></a-table-column>
          <a-table-column title="字段属性名" align="center" data-index="propertyName" :width="180" ellipsis tooltip>
            <template #cell="{ record }">
              <a-input v-model="record.propertyName" placeholder="请输入字段属性名" />
            </template>
          </a-table-column>
          <a-table-column title="字段类型" align="center" data-index="columnType" :width="180" ellipsis tooltip></a-table-column>
          <a-table-column title="Java类型" align="center"  data-index="javaType" :width="180">
            <template #cell="{ record }">
              <a-select
                v-model="record.javaType"
                style="width: 100%"
                placeholder="请选择Java类型"
              >
                <a-option
                  v-for="item in javaTypeOptions"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </a-select>
            </template>
          </a-table-column>
          <a-table-column title="是否主键" align="center" data-index="primaryKey" :width="120"></a-table-column>
          <a-table-column title="是否非空" align="center" data-index="notNull" :width="120"></a-table-column>
          <a-table-column title="是否自增" align="center" data-index="autoIncrement" :width="120">
            <template #cell="{ record }">
              <a-switch v-model:checked="record.autoIncrement" />
            </template>
          </a-table-column>
          <a-table-column title="字段注释" align="center" data-index="comment" :width="180">
            <template #cell="{ record }">
              <a-input v-model="record.comment" placeholder="请输入字段注释" />
            </template>
          </a-table-column>
          <a-table-column title="操作" :width="120" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-popconfirm type="warning" content="确定删除该用户吗?" @ok="onDelete(record)">
                  <a-button type="primary" status="danger" size="mini" >
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
import { deepClone } from "@/utils";
import { useLayoutModel } from "@/hooks/useLayoutModel";
import {batchUpdateColumn, deleteColumn, getColumnList} from "@/api/tool/gen";
const router = useRouter();
import { storeToRefs } from "pinia";
import { useRouteConfigStore } from "@/store/modules/route-config";

const route = useRoute();
const routerStore = useRouteConfigStore();
const { tabsList, currentRoute } = storeToRefs(routerStore);
const { dialogWidth, formLayout, tableFixed } = useLayoutModel();
/**
 * 列表
 */
const loading = ref(false);
const tableList = ref();
const getTable = async () => {
  loading.value = true;
  let res = await getColumnList(tableId.value);
  tableList.value = res.data;
  loading.value = false;
};

// 更新
// 定义常见的Java类型选项（按需扩展）
const javaTypeOptions = [
  // 包装类型
  { label: "Integer", value: "Integer" },
  { label: "Long", value: "Long" },
  { label: "Float", value: "Float" },
  { label: "Double", value: "Double" },
  { label: "Boolean", value: "Boolean" },
  // 常用引用类型
  { label: "String", value: "String" },
  { label: "BigDecimal", value: "BigDecimal" },
  { label: "LocalDate", value: "LocalDate" },
  { label: "LocalDateTime", value: "LocalDateTime" },
];
const handleUpdate = async () => {
  await batchUpdateColumn(deepClone(tableList.value));
  await getTable();
};


// 删除
const onDelete = async (row: any) => {
  await deleteColumn(row.id);
  arcoMessage("success", "删除成功");
  getTable();
}

// 返回
const handleBack = () => {
  const path = route.fullPath;
  routerStore.removeTabsList(path);
  routerStore.removeRouteName(path);
  router.push(tabsList.value.at(-1).path);
}

const tableName = ref("");
const tableId = ref();
onMounted(() => {
  const path = '/tool/gen-code/columnInfo';
  const columnList = routerStore.getRouteStoredParams(path);
  console.log(route.query)
  tableName.value = route.query.tableName+"（"+route.query.tableComment+"）";
  tableId.value = route.query.tableId;
  routerStore.setTabsTitle(`${route.query.tableComment}`+" - "+'字段列表');
  getTable();
  // if (columnList){
  //   tableList.value = columnList;
  // }
});
</script>

<style lang="scss" scoped>
</style>
