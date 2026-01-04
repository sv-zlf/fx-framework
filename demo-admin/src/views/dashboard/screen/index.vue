<template>
  <div class="cyberpunk-screen">
    <!-- 扫描线 -->
    <div class="scan-line"></div>
    <div class="scan-line-vertical"></div>

    <!-- 网格背景 -->
    <div class="grid-background"></div>
    <div class="grid-background-vertical"></div>

    <!-- 头部 -->
    <div class="screen-header">
      <div class="header-left">
        <div class="logo-container">
          <div class="logo-icon">FX</div>
          <div class="logo-text">ADMIN</div>
        </div>
        <div class="header-divider"></div>
        <div class="header-title">实时数据监控系统 v2.0</div>
      </div>
      <div class="header-right">
        <div class="system-status">
          <span class="status-dot status-online"></span>
          <span class="status-text">系统在线</span>
        </div>
        <div class="header-time">{{ currentTime }}</div>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="screen-content">
      <!-- HUD 角落 -->
      <div class="hud-corner hud-corner-tl"></div>
      <div class="hud-corner hud-corner-tr"></div>
      <div class="hud-corner hud-corner-bl"></div>
      <div class="hud-corner hud-corner-br"></div>

      <!-- 概览卡片 -->
      <div class="overview-section">
        <div v-for="(card, index) in overviewCards" :key="index" class="cyber-card" :class="card.className">
          <div class="card-header">
            <div class="card-icon" :style="{ color: card.color }">
              <component :is="card.icon" :size="32" />
            </div>
            <span class="card-title">{{ card.title }}</span>
          </div>
          <div class="card-body">
            <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
            <div class="card-trend" :class="card.trendClass">
              <span class="trend-icon">{{ card.trendIcon }}</span>
              <span class="trend-text">{{ card.trend }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <div class="chart-row">
          <!-- 主图表 -->
          <div class="cyber-container chart-full">
            <div class="container-header">
              <span class="header-label">数据流量趋势</span>
              <span class="header-subtitle">实时监控</span>
            </div>
            <div class="chart-body">
              <div class="simple-line-chart">
                <svg viewBox="0 0 800 200" class="chart-svg">
                  <defs>
                    <linearGradient id="lineGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                      <stop offset="0%" stop-color="#00FF00" stop-opacity="0.5" />
                      <stop offset="100%" stop-color="#00FF00" stop-opacity="0" />
                    </linearGradient>
                    <filter id="glow">
                      <feGaussianBlur stdDeviation="3" result="coloredBlur" />
                      <feMerge>
                        <feMergeNode in="coloredBlur" />
                        <feMergeNode in="SourceGraphic" />
                      </feMerge>
                    </filter>
                  </defs>
                  <path
                    d="M 50 150 L 150 100 L 250 120 L 350 80 L 450 100 L 550 60 L 650 90 L 750 70"
                    stroke="#00FF00"
                    stroke-width="2"
                    fill="url(#lineGradient)"
                    filter="url(#glow)"
                  />
                  <circle
                    v-for="(point, idx) in chartPoints"
                    :key="idx"
                    :cx="point.x"
                    :cy="point.y"
                    r="6"
                    fill="#0D0D0D"
                    stroke="#00FF00"
                    stroke-width="2"
                    filter="url(#glow)"
                  />
                </svg>
                <div class="chart-labels">
                  <div v-for="(label, idx) in chartLabels" :key="idx" class="chart-label">{{ label }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-row">
          <!-- 柱状图 -->
          <div class="cyber-container">
            <div class="container-header">
              <span class="header-label">用户活跃度</span>
              <span class="header-subtitle">7天数据</span>
            </div>
            <div class="chart-body">
              <div class="bar-chart">
                <div v-for="(item, index) in barData" :key="index" class="bar-item">
                  <div class="bar-wrapper">
                    <div
                      class="bar"
                      :style="{ height: item.height, background: item.color, boxShadow: `0 0 20px ${item.color}` }"
                    ></div>
                    <div class="bar-value">{{ item.value }}</div>
                  </div>
                  <div class="bar-label">{{ item.label }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 饼图 -->
          <div class="cyber-container">
            <div class="container-header">
              <span class="header-label">数据分布</span>
              <span class="header-subtitle">类型占比</span>
            </div>
            <div class="chart-body">
              <div class="simple-pie">
                <svg viewBox="0 0 200 200" class="pie-svg">
                  <circle cx="100" cy="100" r="80" fill="none" stroke="#333" stroke-width="2" />
                  <circle
                    v-for="(slice, idx) in pieSlices"
                    :key="idx"
                    cx="100"
                    cy="100"
                    r="80"
                    fill="none"
                    :stroke="slice.color"
                    stroke-width="24"
                    :stroke-dasharray="slice.dash"
                    :stroke-dashoffset="slice.offset"
                    style="transition: stroke-dashoffset 1s ease"
                  />
                </svg>
              </div>
              <div class="pie-legend">
                <div v-for="(item, index) in pieData" :key="index" class="legend-item">
                  <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
                  <span class="legend-text">{{ item.label }} {{ item.percent }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 实时数据 -->
      <div class="realtime-section">
        <div v-for="(item, index) in realtimeData" :key="index" class="cyber-realtime" :class="item.className">
          <div class="realtime-header">
            <div class="realtime-icon" :style="{ color: item.color }">
              <component :is="item.icon" :size="24" />
            </div>
            <span class="realtime-label">{{ item.label }}</span>
          </div>
          <div class="realtime-body">
            <span class="realtime-value" :style="{ color: item.color }">{{ item.value }}</span>
            <span class="realtime-unit">{{ item.unit }}</span>
          </div>
          <div class="realtime-footer">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: item.progress, background: item.color, boxShadow: `0 0 15px ${item.color}` }"
              ></div>
            </div>
            <span class="progress-text">{{ item.progress }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import {
  IconUser,
  IconEye,
  IconFile,
  IconCloud,
  IconMobile,
  IconCheckCircle,
  IconArrowUp,
  IconArrowDown
} from "@arco-design/web-vue/es/icon";

const currentTime = ref("");
let timeInterval: number;

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

const overviewCards = [
  {
    title: "总用户数",
    value: "128,456",
    trend: "+12.5%",
    trendClass: "trend-up",
    trendIcon: IconArrowUp,
    icon: IconUser,
    color: "#00FF00",
    className: "card-green"
  },
  {
    title: "今日访问",
    value: "8,932",
    trend: "+8.3%",
    trendClass: "trend-up",
    trendIcon: IconArrowUp,
    icon: IconEye,
    color: "#00FFFF",
    className: "card-cyan"
  },
  {
    title: "文件总数",
    value: "15,234",
    trend: "-2.1%",
    trendClass: "trend-down",
    trendIcon: IconArrowDown,
    icon: IconFile,
    color: "#FF00FF",
    className: "card-magenta"
  },
  {
    title: "系统状态",
    value: "正常",
    trend: "+0.0%",
    trendClass: "trend-stable",
    trendIcon: IconCheckCircle,
    icon: IconCheckCircle,
    color: "#FFFF00",
    className: "card-yellow"
  }
];

const realtimeData = [
  {
    label: "在线用户",
    value: "2,456",
    unit: "人",
    progress: "76%",
    icon: IconUser,
    color: "#00FF00",
    className: "realtime-green"
  },
  {
    label: "请求次数",
    value: "45,231",
    unit: "次",
    progress: "85%",
    icon: IconCloud,
    color: "#00FFFF",
    className: "realtime-cyan"
  },
  {
    label: "错误率",
    value: "0.12",
    unit: "%",
    progress: "12%",
    icon: IconCheckCircle,
    color: "#FF00FF",
    className: "realtime-magenta"
  },
  {
    label: "响应时间",
    value: "125",
    unit: "ms",
    progress: "45%",
    icon: IconMobile,
    color: "#FFFF00",
    className: "realtime-yellow"
  }
];

const chartPoints = [
  { x: 50, y: 150 },
  { x: 150, y: 100 },
  { x: 250, y: 120 },
  { x: 350, y: 80 },
  { x: 450, y: 100 },
  { x: 550, y: 60 },
  { x: 650, y: 90 },
  { x: 750, y: 70 }
];

const chartLabels = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];

const barData = [
  { label: "周一", value: "60", height: "60%", color: "#00FF00" },
  { label: "周二", value: "75", height: "75%", color: "#00FFFF" },
  { label: "周三", value: "85", height: "85%", color: "#FF00FF" },
  { label: "周四", value: "70", height: "70%", color: "#FFFF00" },
  { label: "周五", value: "90", height: "90%", color: "#00FF00" },
  { label: "周六", value: "65", height: "65%", color: "#00FFFF" },
  { label: "周日", value: "80", height: "80%", color: "#FF00FF" }
];

const pieData = [
  { label: "图片", percent: "35", color: "#00FF00" },
  { label: "视频", percent: "25", color: "#00FFFF" },
  { label: "文档", percent: "20", color: "#FF00FF" },
  { label: "其他", percent: "20", color: "#FFFF00" }
];

const pieSlices = [
  { color: "#00FF00", dash: "502", offset: "0" },
  { color: "#00FFFF", dash: "314", offset: "-251" },
  { color: "#FF00FF", dash: "251", offset: "-565" },
  { color: "#FFFF00", dash: "251", offset: "-816" }
];

onMounted(() => {
  updateTime();
  timeInterval = window.setInterval(updateTime, 1000);
});

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
});
</script>

<style lang="scss" scoped>
.cyberpunk-screen {
  //min-height: 100vh;
  background: #0d0d0d;
  padding: 20px;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
  font-family: "Courier New", "Consolas", monospace;
  max-height: 100vh;
}

// 扫描线
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00ff00, transparent);
  opacity: 0.5;
  animation: scan 4s linear infinite;
  z-index: 10;
}

.scan-line-vertical {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 2px;
  background: linear-gradient(180deg, transparent, #00ff00, transparent);
  opacity: 0.3;
  animation: scanVertical 8s linear infinite;
  z-index: 10;
}

@keyframes scan {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100vh);
  }
}

@keyframes scanVertical {
  0%,
  100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
}

// 网格背景
.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(90deg, rgba(0, 255, 0, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(0, 255, 0, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 1;
  pointer-events: none;
}

.grid-background-vertical {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(0deg, rgba(0, 255, 0, 0.05) 1px, transparent 1px), linear-gradient(rgba(0, 255, 0, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 1;
  pointer-events: none;
}

// HUD 角落
.hud-corner {
  position: absolute;
  width: 40px;
  height: 40px;
  z-index: 5;
}

.hud-corner-tl {
  top: 20px;
  left: 20px;
  border-top: 2px solid #00ff00;
  border-left: 2px solid #00ff00;
  animation: blink 2s infinite;
}

.hud-corner-tr {
  top: 20px;
  right: 20px;
  border-top: 2px solid #00ff00;
  border-right: 2px solid #00ff00;
  animation: blink 2s infinite 0.5s;
}

.hud-corner-bl {
  bottom: 20px;
  left: 20px;
  border-bottom: 2px solid #00ff00;
  border-left: 2px solid #00ff00;
  animation: blink 2s infinite 1.5s;
}

.hud-corner-br {
  bottom: 20px;
  right: 20px;
  border-bottom: 2px solid #00ff00;
  border-right: 2px solid #00ff00;
  animation: blink 2s infinite 1s;
}

@keyframes blink {
  0%,
  100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

// 头部
.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  margin-bottom: 24px;
  background: rgba(0, 255, 0, 0.03);
  border: 1px solid rgba(0, 255, 0, 0.3);
  border-radius: 0;
  position: relative;
  z-index: 5;
}

.screen-header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: #00ff00;
  box-shadow: 0 0 10px #00ff00;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #00ff00 0%, #00cc00 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #0d0d0d;
  text-shadow: 0 0 10px #00ff00;
  box-shadow: 0 0 20px rgba(0, 255, 0, 0.3);
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #00ff00;
  letter-spacing: 2px;
  text-shadow: 0 0 10px #00ff00;
}

.header-divider {
  width: 1px;
  height: 32px;
  background: rgba(0, 255, 0, 0.5);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #00ff00;
  letter-spacing: 1px;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-online {
  background: #00ff00;
  box-shadow: 0 0 10px #00ff00;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
}

.status-text {
  font-size: 13px;
  color: #00ff00;
  letter-spacing: 1px;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

.header-time {
  font-size: 16px;
  color: #00ffff;
  font-family: "Courier New", monospace;
  letter-spacing: 1px;
  text-shadow: 0 0 10px #00ffff;
}

// 内容区域
.screen-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: relative;
  z-index: 5;
}

// 概览卡片
.overview-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.cyber-card {
  background: rgba(0, 255, 0, 0.02);
  border: 1px solid rgba(0, 255, 0, 0.3);
  padding: 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cyber-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
}

.cyber-card.card-green::before {
  background: linear-gradient(90deg, transparent, #00ff00, transparent);
}

.cyber-card.card-cyan::before {
  background: linear-gradient(90deg, transparent, #00ffff, transparent);
}

.cyber-card.card-magenta::before {
  background: linear-gradient(90deg, transparent, #ff00ff, transparent);
}

.cyber-card.card-yellow::before {
  background: linear-gradient(90deg, transparent, #ffff00, transparent);
}

.cyber-card:hover {
  box-shadow:
    0 0 20px rgba(0, 255, 0, 0.2),
    inset 0 0 20px rgba(0, 255, 0, 0.05);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 28px;
}

.card-title {
  font-size: 14px;
  color: #00ff00;
  letter-spacing: 1px;
  text-transform: uppercase;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  text-shadow: 0 0 15px currentColor;
  letter-spacing: 1px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.trend-up {
  color: #00ff00;
  text-shadow: 0 0 5px #00ff00;
}

.trend-down {
  color: #ff0066;
  text-shadow: 0 0 5px #ff0066;
}

.trend-stable {
  color: #ffff00;
  text-shadow: 0 0 5px #ffff00;
}

.trend-icon {
  font-size: 14px;
}

// 图表区域
.charts-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.cyber-container {
  background: rgba(0, 255, 0, 0.02);
  border: 1px solid rgba(0, 255, 0, 0.3);
  padding: 24px;
  position: relative;
  transition: all 0.3s ease;
}

.cyber-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00ff00, transparent);
}

.cyber-container::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00ff00, transparent);
}

.cyber-container:hover {
  box-shadow:
    0 0 20px rgba(0, 255, 0, 0.2),
    inset 0 0 20px rgba(0, 255, 0, 0.05);
}

.cyber-container.chart-full {
  grid-column: 1 / -1;
}

.container-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-label {
  font-size: 16px;
  font-weight: 600;
  color: #00ff00;
  letter-spacing: 1px;
  text-shadow: 0 0 10px #00ff00;
}

.header-subtitle {
  font-size: 12px;
  color: #00ff66;
  letter-spacing: 0.5px;
}

.chart-body {
  min-height: 280px;
}

// 简单线图
.simple-line-chart {
  position: relative;
  height: 250px;
}

.chart-svg {
  width: 100%;
  height: 100%;
}

.chart-labels {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.chart-label {
  font-size: 12px;
  color: #00ff00;
  letter-spacing: 1px;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

// 柱状图
.bar-chart {
  display: flex;
  justify-content: space-around;
  height: 200px;
  align-items: flex-end;
  padding: 0 20px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 40px;
  border-radius: 4px 4px 0 0;
  transition: height 0.5s ease;
}

.bar-value {
  font-size: 13px;
  color: #00ff00;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

.bar-label {
  font-size: 12px;
  color: #00ff66;
  letter-spacing: 0.5px;
}

// 饼图
.simple-pie {
  position: relative;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-svg {
  width: 150px;
  height: 150px;
  animation: spin 30s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.pie-legend {
  margin-left: 30px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-text {
  font-size: 13px;
  color: #00ff00;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

// 实时数据
.realtime-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.cyber-realtime {
  background: rgba(0, 255, 0, 0.02);
  border: 1px solid rgba(0, 255, 0, 0.3);
  padding: 20px;
  position: relative;
  transition: all 0.3s ease;
}

.cyber-realtime::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
}

.cyber-realtime.realtime-green::before {
  background: linear-gradient(90deg, transparent, #00ff00, transparent);
}

.cyber-realtime.realtime-cyan::before {
  background: linear-gradient(90deg, transparent, #00ffff, transparent);
}

.cyber-realtime.realtime-magenta::before {
  background: linear-gradient(90deg, transparent, #ff00ff, transparent);
}

.cyber-realtime.realtime-yellow::before {
  background: linear-gradient(90deg, transparent, #ffff00, transparent);
}

.cyber-realtime:hover {
  box-shadow:
    0 0 20px rgba(0, 255, 0, 0.2),
    inset 0 0 20px rgba(0, 255, 0, 0.05);
  transform: translateY(-2px);
}

.realtime-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.realtime-icon {
  font-size: 24px;
}

.realtime-label {
  font-size: 13px;
  color: #00ff00;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  text-shadow: 0 0 5px rgba(0, 255, 0, 0.5);
}

.realtime-body {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;
}

.realtime-value {
  font-size: 28px;
  font-weight: 700;
  text-shadow: 0 0 15px currentColor;
}

.realtime-unit {
  font-size: 14px;
  color: #00ff66;
  letter-spacing: 0.5px;
}

.realtime-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: rgba(0, 255, 0, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.5s ease;
}

.progress-text {
  font-size: 12px;
  color: #00ff66;
  min-width: 45px;
  text-align: right;
}

// 响应式
@media (max-width: 1920px) {
  .overview-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .realtime-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .cyberpunk-screen {
    padding: 12px;
  }

  .screen-header {
    padding: 16px 20px;
    flex-direction: column;
    gap: 12px;
  }

  .hud-corner {
    width: 30px;
    height: 30px;
  }

  .overview-section {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }

  .realtime-section {
    grid-template-columns: 1fr;
  }
}
</style>
