<template>
  <div class="data-screen-container">
    <div class="screen-header">
      <div class="header-title">实时数据监控大屏</div>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-content">
      <div class="cards-grid">
        <div v-for="(card, index) in overviewCards" :key="index" class="data-card" :class="card.className">
          <div class="card-icon"></div>
          <div class="card-content">
            <div class="card-title">{{ card.title }}</div>
            <div class="card-value">{{ card.value }}</div>
            <div class="card-trend" :class="card.trendClass">
              <span>{{ card.trend }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="charts-section">
        <div class="chart-row">
          <div class="chart-container">
            <div class="chart-title">用户活跃趋势</div>
            <div class="chart-body">
              <v-chart ref="barChartRef" :spec="barChartSpec" :init-options="{ renderer: 'canvas'}" :theme="vchartTheme" />
            </div>
          </div>

          <div class="chart-container">
            <div class="chart-title">销售数据占比</div>
            <div class="chart-body">
              <v-chart ref="pieChartRef" :spec="pieChartSpec" :init-options="{ renderer:'canvas' }" :theme="vchartTheme" />
            </div>
          </div>
        </div>

        <div class="chart-row">
          <div class="chart-container full-width">
            <div class="chart-title">实时交易记录</div>
            <div class="chart-body">
              <div class="data-table">
                <div class="table-header">
                  <div class="table-cell">订单编号</div>
                  <div class="table-cell">用户</div>
                  <div class="table-cell">金额</div>
                  <div class="table-cell">状态</div>
                  <div class="table-cell">时间</div>
                </div>
                <div v-for="(item, index) in transactionData" :key="index" class="table-row">
                  <div class="table-cell">{{ item.orderNo }}</div>
                  <div class="table-cell">{{ item.user }}</div>
                  <div class="table-cell">￥{{ item.amount }}</div>
                  <div class="table-cell">
                    <span class="status-badge" :class="item.statusClass">{{ item.status }}</span>
                  </div>
                  <div class="table-cell">{{ item.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="realtime-section">
        <div class="realtime-item" v-for="(item, index) in realtimeData" :key="index">
          <div class="realtime-label">{{ item.label }}</div>
          <div class="realtime-value" :style="{ color: item.color }">{{ item.value }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import VChart from "@visactor/vchart";
import "@visactor/vchart-arco-theme";

const vchartTheme = "arcoTheme";

const currentTime = ref("");
const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
  });
};
setInterval(updateTime, 1000);
updateTime();

const overviewCards = ref([
  { title: "总用户数", value: "128,456", trend: "12.5%", trendClass: "trend-up", className: "card-blue" },
  { title: "今日访问", value: "8,932", trend: "8.3%", trendClass: "trend-up", className: "card-green" },
  { title: "成交订单", value: "1,245", trend: "-2.1%", trendClass: "trend-down", className: "card-orange" },
  { title: "好评率", value: "98.5%", trend: "0.5%", trendClass: "trend-up", className: "card-purple" }
]);

const barChartRef = ref();
const barChartSpec = computed(() => ({
  type: "bar",
  data: [
    {
      id: "barData",
      values: [
        { day: "周一", value: 60 },
        { day: "周二", value: 75 },
        { day: "周三", value: 85 },
        { day: "周四", value: 70 },
        { day: "周五", value: 90 },
        { day: "周六", value: 65 },
        { day: "周日", value: 80 }
      ]
    }
  ],
  xField: "day",
  yField: "value",
  axes: [
    {
      orient: "left",
      title: {
        visible: false
      },
      label: {
        format: (v) => v + "%"
      }
    },
    {
      orient: "bottom",
      title: {
        visible: false
      }
    }
  ],
  bar: {
    style: {
      fill: (datum) => {
        const days = ["周一", "周二", "周三", "周四", "周五"];
        return days.includes(datum.day) ? "#165DFF" : "#722ED1";
      },
      cornerRadius: 8
    }
  },
  tooltip: {
    dimension: {
      title: {
        key: "day"
      }
    },
    value: {
      content: (datum) => {
        return [{
          key: "活跃度",
          value: datum.value + "%"
        }];
      }
    }
  }
}));

const pieChartRef = ref();
const pieChartSpec = computed(() => ({
  type: "pie",
  data: [
    {
      id: "pieData",
      values: [
        { type: "线上", value: 45 },
        { type: "门店", value: 30 },
        { type: "分销", value: 15 },
        { type: "其他", value: 10 }
      ]
    }
  ],
  valueField: "value",
  categoryField: "type",
  title: {
    visible: false
  },
  legends: {
    visible: true,
    orient: "right",
    position: "middle"
  },
  pie: {
    outerRadius: 0.8,
    innerRadius: 0,
    label: {
      visible: true,
      format: (datum) => {
        return datum.type + " " + datum.value + "%";
      }
    },
    state: {
      hover: {
        outerRadius: 0.85,
        stroke: "#000",
        lineWidth: 1
      }
    }
  },
  color: ["#165DFF", "#00B42A", "#FF7D00", "#F53F3F"],
  tooltip: {
    dimension: {
      title: {
        key: "type"
      }
    },
    value: {
      content: (datum) => {
        return [{
          key: "占比",
          value: datum.value + "%"
        }];
      }
    }
  }
}));

const transactionData = ref([
  { orderNo: "ORD001", user: "张三", amount: "299.00", status: "已完成", statusClass: "status-success", time: "10:23:45" },
  { orderNo: "ORD002", user: "李四", amount: "158.50", status: "处理中", statusClass: "status-processing", time: "10:21:32" },
  { orderNo: "ORD003", user: "王五", amount: "588.00", status: "已完成", statusClass: "status-success", time: "10:18:21" },
  { orderNo: "ORD004", user: "赵六", amount: "99.00", status: "已取消", statusClass: "status-cancelled", time: "10:15:08" },
  { orderNo: "ORD005", user: "孙七", amount: "368.00", status: "已完成", statusClass: "status-success", time: "10:12:55" }
]);

const realtimeData = ref([
  { label: "实时在线", value: "2,845 人", color: "#00B42A" },
  { label: "今日成交", value: "￥456,780", color: "#165DFF" },
  { label: "新增订单", value: "89 单", color: "#FF7D00" },
  { label: "待处理", value: "23 单", color: "#F53F3F" }
]);
</script>

<style lang="scss" scoped>
.data-screen-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  max-height: 100vh;
  overflow-y: auto;
  padding: 0 16px;
  overflow-x: hidden;

  .screen-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 30px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    margin-bottom: 20px;

    .header-title {
      font-size: 28px;
      font-weight: bold;
      color: white;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .header-time {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 500;
    }
  }

  .screen-content {
    .cards-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 20px;

      .data-card {
        display: flex;
        align-items: center;
        padding: 24px;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .card-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 28px;
          margin-right: 16px;
        }

        &.card-blue .card-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
        &.card-green .card-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
        &.card-orange .card-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; }
        &.card-purple .card-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }

        .card-content {
          flex: 1;

          .card-title {
            font-size: 14px;
            color: #86909c;
            margin-bottom: 8px;
          }

          .card-value {
            font-size: 24px;
            font-weight: bold;
            color: #1d2129;
            margin-bottom: 8px;
          }

          .card-trend {
            display: flex;
            align-items: center;
            font-size: 13px;

            &.trend-up { color: #00b42a; }
            &.trend-down { color: #f53f3f; }
          }
        }
      }
    }

    .charts-section {
      display: flex;
      flex-direction: column;
      gap: 20px;
      margin-bottom: 20px;

      .chart-row {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
        gap: 20px;

        .chart-container {
          background: rgba(255, 255, 255, 0.95);
          border-radius: 12px;
          padding: 24px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

          &.full-width {
            grid-column: 1 / -1;
          }

          .chart-title {
            font-size: 18px;
            font-weight: 600;
            color: #1d2129;
            margin-bottom: 20px;
          }

          .chart-body {
            min-height: 280px;

            .data-table {
              width: 100%;

              .table-header,
              .table-row {
                display: grid;
                grid-template-columns: 1.5fr 1fr 1fr 1fr 1.2fr;
                padding: 12px 16px;

                .table-cell {
                  font-size: 14px;
                  color: #4e5969;
                }
              }

              .table-header {
                background: #f2f3f5;
                font-weight: 600;
                border-radius: 8px 8px 0 0;
              }

              .table-row {
                border-bottom: 1px solid #e5e6eb;

                &:hover {
                  background: #f7f8fa;
                }

                .status-badge {
                  padding: 4px 12px;
                  border-radius: 4px;
                  font-size: 12px;

                  &.status-success {
                    background: #e8ffea;
                    color: #00b42a;
                  }

                  &.status-processing {
                    background: #e8f3ff;
                    color: #165dff;
                  }

                  &.status-cancelled {
                    background: #ffece8;
                    color: #f53f3f;
                  }
                }
              }
            }
          }
        }
      }
    }

    .realtime-section {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      .realtime-item {
        text-align: center;
        padding: 16px;

        .realtime-label {
          font-size: 13px;
          color: #86909c;
          margin-bottom: 8px;
        }

        .realtime-value {
          font-size: 20px;
          font-weight: bold;
        }
      }
    }
  }
}
</style>
