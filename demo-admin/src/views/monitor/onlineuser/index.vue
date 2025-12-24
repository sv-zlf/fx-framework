<template>
  <div class="snow-page">
    <div class="snow-inner">
      <a-space wrap>
        <a-input v-model="form.loginLocation" placeholder="请输入登录地址" allow-clear />
        <a-input v-model="form.loginName" placeholder="请输入账户名称" allow-clear />
        <a-range-picker v-model="form.loginTime" show-time format="YYYY-MM-DD HH:mm" allow-clear />
        <a-button type="primary" @click="search">
          <template #icon><icon-search /></template>
          <span>查询</span>
        </a-button>
        <a-button @click="reset">
          <template #icon><icon-refresh /></template>
          <span>重置</span>
        </a-button>
      </a-space>

      <a-table
        row-key="id"
        :data="list"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1000 }"
        :pagination="pagination"
      >
        <template #columns>
          <a-table-column title="序号" :width="64">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="会话编号" data-index="sessionId" ellipsis tooltip></a-table-column>
          <a-table-column title="登录账户" data-index="loginName" ellipsis tooltip></a-table-column>
          <a-table-column title="IP地址" data-index="host" ellipsis tooltip></a-table-column>
          <a-table-column title="登录地址" data-index="loginLocation" ellipsis tooltip></a-table-column>
          <a-table-column title="状态" data-index="sessionStatus" align="center" :width="80">
            <template #cell="{ record }">
              <a-space>
                <a-badge status="success" text="在线" v-if="record.sessionStatus == 1" />
                <a-badge status="normal" text="离线" v-else />
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="浏览器" data-index="browser" ellipsis tooltip></a-table-column>
          <a-table-column title="操作系统" data-index="os" ellipsis tooltip></a-table-column>
          <a-table-column title="登录时间" data-index="loginTime" ellipsis tooltip></a-table-column>
          <a-table-column title="最后访问时间" data-index="lastAccessTime" ellipsis tooltip></a-table-column>
          <a-table-column title="操作" :width="100" align="center" :fixed="isMobile ? '' : 'right'">
            <template #cell="{ record }">
              <a-space>
                <a-popconfirm type="warning" content="确定强制退出该账号吗?" @ok="onLogout(record)">
                  <a-button type="primary" status="danger" size="mini">
                    <template #icon><icon-export /></template>
                    <span>强退</span>
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
import { useDevicesSize } from "@/hooks/useDevicesSize";
import { getPageList } from "@/api/system/session/index";

defineOptions({ name: "onlineuser" });

const { isMobile } = useDevicesSize();

const form = ref({
  loginLocation: "",
  loginName: "",
  loginTime: []
});
const search = () => {
  getOnlineUser();
};
const reset = () => {
  form.value = {
    loginLocation: "",
    loginName: "",
    loginTime: []
  };
  getOnlineUser();
};

const onLogout = (row: any) => {
  console.log("退出", row);
  arcoMessage("success", "模拟退出成功");
  getOnlineUser();
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
    getOnlineUser();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getOnlineUser();
  }
});
const list = ref([]);
const getOnlineUser = async () => {
  try {
    loading.value = true;
    const params = {
      ...form.value,
      pageIndex: pagination.value.current,
      pageSize: pagination.value.pageSize,
      startLoginTime: form.value.loginTime[0] || null,
      endLoginTime: form.value.loginTime[1] || null
    };
    let res = await getPageList(params);
    list.value = res.data.records;
    pagination.value.total = res.data.total;
  } finally {
    loading.value = false;
  }
};

getOnlineUser();
</script>

<style lang="scss" scoped>
.text-right-gap {
  margin-right: $margin;
}
</style>
