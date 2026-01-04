<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1 class="banner-title">欢迎使用 FxAdmin</h1>
          <p class="banner-subtitle">Spring Boot 3 + Vue 3 快速开发框架</p>
          <p class="banner-desc">让开发更简单，让项目更快上线</p>
          <div class="banner-actions">
            <a-space>
              <a-button class="banner-btn-primary" size="large" @click="goToDocs">
                <template #icon>
                  <icon-book />
                </template>
                查看文档
              </a-button>
              <a-button class="banner-btn-secondary" size="large" @click="goToAbout">
                <template #icon>
                  <icon-file />
                </template>
                关于项目
              </a-button>
            </a-space>
          </div>
        </div>
        <div class="banner-illustration">
          <div class="illustration-circle">
            <div class="illustration-icon">
              <icon-code-square :size="100" class="icon-code" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.key">
          <a-card :bordered="false" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon-wrapper">
                <div class="stat-icon" :style="{ background: stat.color }">
                  <component :is="stat.icon" :size="28" class="stat-icon-inner" />
                </div>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 核心特性 -->
    <div class="features-section">
      <div class="section-header">
        <h2 class="section-title">核心特性</h2>
        <p class="section-subtitle">开箱即用的企业级功能模块</p>
      </div>
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="8" v-for="feature in features" :key="feature.title">
          <a-card :bordered="false" class="feature-card">
            <div class="feature-icon-wrapper">
              <div class="feature-icon" :style="{ color: feature.color }">
                <component :is="feature.icon" :size="40" />
              </div>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 快速开始 -->
    <div class="quickstart-section">
      <div class="section-header">
        <h2 class="section-title">快速开始</h2>
        <p class="section-subtitle">三步即可启动你的项目</p>
      </div>
      <a-steps :current="-1" line-less>
        <a-step v-for="step in quickstartSteps" :key="step.title">
          <template #title>
            {{ step.title }}
          </template>
          <template #description>
            {{ step.description }}
          </template>
          <div class="step-content">
            <div class="code-card">
              <code class="code-text">{{ step.code }}</code>
            </div>
          </div>
        </a-step>
      </a-steps>
    </div>

    <!-- CTA 区域 -->
    <div class="cta-section">
      <a-card :bordered="false" class="cta-card">
        <div class="cta-content">
          <div class="cta-text">
            <h3>准备好开始了吗？</h3>
            <p>立即体验 FxAdmin，开启高效开发之旅</p>
          </div>
          <a-button type="primary" size="large" class="cta-button" @click="goToGithub">
            <template #icon>
              <icon-github />
            </template>
            查看 GitHub
          </a-button>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import {
  IconBook,
  IconGithub,
  IconFile,
  IconSettings,
  IconThunderbolt,
  IconMobile,
  IconCloud,
  IconCode,
  IconLock
} from "@arco-design/web-vue/es/icon";

const router = useRouter();

const stats = [
  {
    key: "users",
    label: "权限管理",
    value: "RBAC",
    icon: IconSettings,
    color: "linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)"
  },
  {
    key: "modules",
    label: "功能模块",
    value: "8+",
    icon: IconFile,
    color: "linear-gradient(135deg, #F97316 0%, #EA580C 100%)"
  },
  {
    key: "api",
    label: "接口文档",
    value: "Swagger",
    icon: IconBook,
    color: "linear-gradient(135deg, #10B981 0%, #059669 100%)"
  },
  {
    key: "security",
    label: "安全防护",
    value: "JWT",
    icon: IconLock,
    color: "linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)"
  }
];

const features = [
  {
    title: "权限控制",
    description: "基于 RBAC 的权限管理系统，支持细粒度权限控制",
    icon: IconSettings,
    color: "#3B82F6"
  },
  {
    title: "代码生成",
    description: "智能代码生成器，一键生成前后端代码，提升开发效率",
    icon: IconCode,
    color: "#F97316"
  },
  {
    title: "系统监控",
    description: "实时监控系统运行状态，支持日志查看和性能分析",
    icon: IconThunderbolt,
    color: "#10B981"
  },
  {
    title: "多端适配",
    description: "支持 Web、H5、小程序等多端适配，一套代码多端运行",
    icon: IconMobile,
    color: "#8B5CF6"
  },
  {
    title: "文件管理",
    description: "完善的文件管理系统，支持本地存储和云存储集成",
    icon: IconCloud,
    color: "#F59E0B"
  },
  {
    title: "数据导入",
    description: "支持 Excel 数据导入导出，方便数据迁移和管理",
    icon: IconFile,
    color: "#06B6D4"
  }
];

const quickstartSteps = [
  {
    title: "克隆项目",
    description: "使用 Git 克隆项目到本地",
    code: "git clone https://github.com/your-repo/demo.git"
  },
  {
    title: "安装依赖",
    description: "使用 pnpm 安装项目依赖",
    code: "pnpm install"
  },
  {
    title: "启动开发",
    description: "启动开发服务器，开始编码",
    code: "cd demo-admin && pnpm dev"
  }
];

const goToDocs = () => {
  Message.success("正在打开文档...");
};

const goToAbout = () => {
  router.push("/about");
};

const goToGithub = () => {
  window.open("https://github.com", "_blank");
};
</script>

<style lang="scss" scoped>
.home-container {
  background: #f8fafc;
  min-height: calc(100vh - 120px);
  padding: 16px 24px;
  overflow-y: auto;
  overflow-x: hidden;
}

.welcome-banner {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 20px;
  padding: 48px 40px;
  margin-bottom: 24px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow:
    0 8px 32px rgba(37, 99, 235, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1);

  &::before {
    content: "";
    position: absolute;
    top: -40%;
    right: -20%;
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    filter: blur(40px);
  }

  &::after {
    content: "";
    position: absolute;
    bottom: -30%;
    left: -15%;
    width: 350px;
    height: 350px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
    border-radius: 50%;
    filter: blur(30px);
  }
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.banner-text {
  flex: 1;
  max-width: 600px;
}

.banner-title {
  font-size: 42px;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  letter-spacing: -0.5px;
}

.banner-subtitle {
  font-size: 20px;
  margin: 0 0 8px 0;
  opacity: 0.9;
  font-weight: 500;
}

.banner-desc {
  font-size: 15px;
  margin: 0 0 24px 0;
  opacity: 0.85;
  line-height: 1.5;
}

.banner-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.banner-btn-primary {
  background: rgba(255, 255, 255, 0.95);
  color: #2563eb !important;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.banner-btn-primary:hover {
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.banner-btn-secondary {
  background: rgba(255, 255, 255, 0.15);
  color: #fff !important;
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.banner-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.banner-illustration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-circle {
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 4s ease-in-out infinite;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.illustration-icon {
  animation: pulse 2s ease-in-out infinite;
}

.icon-code {
  color: #fff;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.85;
  }
}

.stats-section {
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e2e8f0;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e1;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 16px;
}

.stat-icon-wrapper {
  flex-shrink: 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  transition: transform 0.25s ease;
}

.stat-icon .stat-icon-inner {
  color: #fff;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.features-section {
  margin-bottom: 32px;
}

.section-header {
  text-align: center;
  margin-bottom: 32px;
}

.section-header .section-title {
  font-weight: 700;
  font-size: 28px;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: -0.3px;
}

.section-header .section-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  font-weight: 400;
}

.feature-card {
  height: 100%;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  text-align: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e1;
}

.feature-card .feature-icon-wrapper {
  margin-bottom: 16px;
  display: inline-block;
}

.feature-card .feature-icon {
  padding: 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(59, 130, 246, 0.03) 100%);
  transition: all 0.25s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.05);
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
  letter-spacing: -0.2px;
}

.feature-card p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
  font-weight: 400;
}

.quickstart-section {
  margin-bottom: 32px;
}

.step-content {
  margin-top: 16px;
}

.code-card {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: 12px;
  padding: 16px 20px;
  border: 1px solid #334155;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.3);
}

.code-card .code-text {
  font-family: "Fira Code", "Monaco", "Consolas", monospace;
  font-size: 13px;
  color: #e2e8f0;
  word-break: break-all;
  line-height: 1.6;
  font-weight: 400;
  letter-spacing: 0.3px;
}

.cta-section {
  margin-bottom: 40px;
}

.cta-card {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 20px;
  overflow: hidden;
  border: none;
}

.cta-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  gap: 24px;
}

.cta-text {
  flex: 1;
  color: white;

  h3 {
    font-size: 32px;
    font-weight: 700;
    margin: 0 0 8px 0;
    letter-spacing: -0.5px;
  }

  p {
    font-size: 16px;
    margin: 0;
    opacity: 0.9;
  }
}

.cta-button {
  background: #fff;
  color: #2563eb !important;
  border: none;
  font-weight: 600;
  padding: 12px 32px;
  white-space: nowrap;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .home-container {
    padding: 12px 16px;
  }

  .welcome-banner {
    padding: 40px 24px;
    border-radius: 16px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
  }

  .banner-illustration {
    margin-top: 32px;
  }

  .banner-title {
    font-size: 32px;
  }

  .banner-subtitle {
    font-size: 18px;
  }

  .banner-desc {
    font-size: 14px;
  }

  .banner-actions {
    justify-content: center;
    gap: 10px;
    width: 100%;
  }

  .banner-actions .banner-btn-primary,
  .banner-actions .banner-btn-secondary {
    width: 100%;
  }

  .illustration-circle {
    width: 140px;
    height: 140px;
  }

  .section-header {
    margin-bottom: 24px;
  }

  .section-header .section-title {
    font-size: 24px;
  }

  .section-header .section-subtitle {
    font-size: 14px;
  }

  .stat-content {
    padding: 16px 12px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-value {
    font-size: 24px;
  }

  .cta-content {
    flex-direction: column;
    text-align: center;
    padding: 32px 24px;
  }

  .cta-text h3 {
    font-size: 24px;
  }

  .cta-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .banner-title {
    font-size: 28px;
  }

  .banner-subtitle {
    font-size: 16px;
  }
}
</style>
