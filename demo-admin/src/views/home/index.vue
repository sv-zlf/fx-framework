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
              <a-button type="primary" size="large" @click="goToDocs">
                <template #icon>
                  <icon-book />                </template>
                查看文档
              </a-button>
              <a-button size="large" @click="goToGithub">
                <template #icon>
                  <icon-github />                </template>
                GitHub
              </a-button>
            </a-space>
          </div>
        </div>
        <div class="banner-illustration">
          <div class="illustration-circle">
            <div class="illustration-icon">
              <icon-code-square :size="120" style="color: #fff;" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <a-row :gutter="20">
        <a-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.key">
          <a-card :bordered="false" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" :style="{ background: stat.color }">
                <component :is="stat.icon" :size="32" style="color: #fff;" />
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
        <h2>核心特性</h2>
        <p>开箱即用的企业级功能模块</p>
      </div>
      <a-row :gutter="20">
        <a-col :xs="24" :sm="12" :md="8" v-for="feature in features" :key="feature.title">
          <a-card :bordered="false" class="feature-card">
            <div class="feature-icon" :style="{ color: feature.color }">
              <component :is="feature.icon" :size="48" />
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 技术栈 -->
    <div class="tech-stack-section">
      <div class="section-header">
        <h2>技术栈</h2>
        <p>现代化技术，打造卓越开发体验</p>
      </div>
      <a-row :gutter="20">
        <a-col :xs="24" :sm="12" :md="12" v-for="stack in techStack" :key="stack.title">
          <a-card :bordered="false" class="tech-card">
            <div class="tech-header">
              <component :is="stack.icon" :size="32" :style="{ color: stack.color }" />
              <h3>{{ stack.title }}</h3>
            </div>
            <div class="tech-tags">
              <a-tag v-for="tag in stack.tags" :key="tag" color="blue" size="small">
                {{ tag }}
              </a-tag>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 快速开始 -->
    <div class="quickstart-section">
      <div class="section-header">
        <h2>快速开始</h2>
        <p>三步即可启动你的项目</p>
      </div>
      <a-steps :current="-1" line-less>
        <a-step v-for="step in quickstartSteps" :key="step.title">
          <template #title>
            {{ step.title }}
          </template>
          <template #description>
            {{ step.description }}
          </template>
          <div class="step-code">
            <a-card :bordered="false" class="code-card">
              <code>{{ step.code }}</code>
            </a-card>
          </div>
        </a-step>
      </a-steps>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import {
  IconBook,
  IconGithub,
  IconUser,
  IconFile,
  IconSettings,
  IconThunderbolt,
  IconMobile,
  IconCloud,
  IconCode,
  IconBug,
  IconLock,
  IconRobot
} from "@arco-design/web-vue/es/icon";

const router = useRouter();

// 统计数据
const stats = ref([
  {
    key: "users",
    label: "用户管理",
    value: "RBAC",
    icon: IconUser,
    color: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
  },
  {
    key: "modules",
    label: "功能模块",
    value: "8+",
    icon: IconFile,
    color: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
  },
  {
    key: "api",
    label: "接口文档",
    value: "Auto",
    icon: IconSettings,
    color: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
  },
  {
    key: "performance",
    label: "高性能",
    value: "Redis",
    icon: IconThunderbolt,
    color: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
  }
]);

// 核心特性
const features = ref([
  {
    title: "极速开发",
    description: "内置代码生成器，一键生成前后端 CRUD 代码，大幅提升开发效率",
    icon: IconThunderbolt,
    color: "#ff7d00"
  },
  {
    title: "模块化设计",
    description: "权限、字典、参数、日志等核心模块开箱即用，无需从零搭建",
    icon: IconFile,
    color: "#165dff"
  },
  {
    title: "RBAC 权限",
    description: "用户/角色/菜单三级权限控制，支持按钮级权限和数据权限",
    icon: IconLock,
    color: "#00b42a"
  },
  {
    title: "全端支持",
    description: "Web 管理端 + Web 网站端 + 移动端，一套代码多端运行",
    icon: IconMobile,
    color: "#ff7d00"
  },
  {
    title: "接口文档",
    description: "Knife4j 自动生成 OpenAPI3 接口文档，支持在线调试和接口导出",
    icon: IconRobot,
    color: "#165dff"
  },
  {
    title: "代码质量",
    description: "TypeScript + ESLint + Prettier，代码规范统一，易于维护",
    icon: IconCode,
    color: "#00b42a"
  }
]);

// 技术栈
const techStack = ref([
  {
    title: "后端技术",
    // icon: IconDatabase,
    color: "#4caf50",
    tags: ["Spring Boot 3.5", "MyBatis-Plus", "Spring Security", "Redis", "MySQL"]
  },
  {
    title: "Web 管理端",
    icon: IconCode,
    color: "#2196f3",
    tags: ["Vue 3.5", "Arco Design", "Pinia", "Vite", "TypeScript"]
  },
  {
    title: "Web 网站端",
    icon: IconCloud,
    color: "#00bcd4",
    tags: ["Nuxt 3.15", "SSR", "i18n", "SEO 优化"]
  },
  {
    title: "移动端",
    icon: IconMobile,
    color: "#ff9800",
    tags: ["UniApp", "Vue 3", "多端适配", "Pinia"]
  }
]);

// 快速开始步骤
const quickstartSteps = ref([
  {
    title: "安装依赖",
    description: "使用 pnpm 安装项目依赖",
    code: "pnpm install"
  },
  {
    title: "启动后端",
    description: "启动 Spring Boot 后端服务",
    code: "cd demo && mvn spring-boot:run"
  },
  {
    title: "启动前端",
    description: "启动 Vue 3 前端开发服务器",
    code: "cd demo-admin && pnpm dev"
  }
]);

const goToDocs = () => {
  Message.success("正在打开文档...");
  // TODO: 跳转到文档页面
};

const goToGithub = () => {
  window.open("https://github.com", "_blank");
};
</script>

<style lang="scss" scoped>
.home-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
  padding: 20px;
  max-height: 80vh;
  /* 垂直方向内容溢出时显示滚动条，允许下滑 */
  overflow-y: auto;
  /* 可选：添加内边距，避免内容贴边 */
  padding: 0 16px;
  /* 可选：隐藏横向滚动条（防止内容横向溢出） */
  overflow-x: hidden;
}

// 欢迎横幅
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 20px;
  color: white;
  position: relative;
  overflow: hidden;

  &::before {
    content: "";
    position: absolute;
    top: -50%;
    right: -10%;
    width: 600px;
    height: 600px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  &::after {
    content: "";
    position: absolute;
    bottom: -30%;
    left: -5%;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
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
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 20px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.banner-subtitle {
  font-size: 24px;
  margin: 0 0 10px 0;
  opacity: 0.95;
}

.banner-desc {
  font-size: 16px;
  margin: 0 0 30px 0;
  opacity: 0.9;
}

.banner-actions {
  display: flex;
  gap: 12px;
}

.banner-illustration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-circle {
  width: 180px;
  height: 180px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 3s ease-in-out infinite;
}

.illustration-icon {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

// 统计卡片
.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1d2129;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #86909c;
}

// 核心特性
.features-section {
  margin-bottom: 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    font-size: 32px;
    font-weight: 700;
    color: #1d2129;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 16px;
    color: #86909c;
    margin: 0;
  }
}

.feature-card {
  height: 100%;
  text-align: center;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  .feature-icon {
    margin-bottom: 16px;
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #1d2129;
    margin: 0 0 12px 0;
  }

  p {
    font-size: 14px;
    color: #86909c;
    line-height: 1.6;
    margin: 0;
  }
}

// 技术栈
.tech-stack-section {
  margin-bottom: 20px;
}

.tech-card {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1d2129;
    margin: 0 0 12px 0;
  }
}

.tech-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

// 快速开始
.quickstart-section {
  .code-card {
    background: #f2f3f5;
    border-radius: 8px;
    padding: 12px;

    code {
      font-family: "Monaco", "Consolas", monospace;
      font-size: 14px;
      color: #1d2129;
      word-break: break-all;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .banner-content {
    flex-direction: column;
    text-align: center;
  }

  .banner-illustration {
    margin-top: 30px;
  }

  .banner-title {
    font-size: 32px;
  }

  .banner-subtitle {
    font-size: 20px;
  }

  .banner-actions {
    justify-content: center;
  }
}
</style>
