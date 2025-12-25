<template>
  <snow-page>

  <div class="about-container">
    <!-- 头部 Banner -->
    <div class="header-banner">
      <div class="banner-content">
        <div class="banner-left">
          <h1 class="banner-title">FxAdmin</h1>
          <p class="banner-subtitle">全栈快速开发框架</p>
          <p class="banner-desc">Spring Boot 3 + Vue 3 + Nuxt 3 + UniApp</p>
        </div>
        <div class="banner-right">
          <div class="version-badge">
            <span class="version-label">版本</span>
            <span class="version-number">v1.0.0</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
<!--    <div class="stats-row">-->
<!--      <div v-for="stat in stats" :key="stat.key" class="stat-card">-->
<!--        <div class="stat-icon" :style="{ background: stat.color }">-->
<!--          <component :is="stat.icon" :size="24" style="color: #fff" />-->
<!--        </div>-->
<!--        <div class="stat-info">-->
<!--          <div class="stat-value">{{ stat.value }}</div>-->
<!--          <div class="stat-label">{{ stat.label }}</div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

    <!-- 技术栈分类 -->
    <div class="tech-sections">
      <div v-for="section in techSections" :key="section.key" class="tech-section">
        <div class="section-header" :style="{ background: section.color }">
          <component :is="section.icon" :size="28" style="color: #fff" />
          <h2>{{ section.title }}</h2>
        </div>
        <div class="section-content">
          <div v-for="group in section.groups" :key="group.title" class="tech-group">
            <h3 class="group-title">{{ group.title }}</h3>
            <div class="tech-tags">
              <a-tag v-for="tech in group.techs" :key="tech.name" 
                     :color="tech.color" class="tech-tag">
                <component :is="tech.icon" :size="14" style="margin-right: 4px" />
                {{ tech.name }}
                <span v-if="tech.version" class="tech-version">{{ tech.version }}</span>
              </a-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 项目信息 -->
    <div class="project-info-section">
      <a-card title="项目信息" :bordered="false" class="info-card">
        <a-descriptions :column="3" bordered>
          <a-descriptions-item label="项目名称">FxAdmin 快速开发框架</a-descriptions-item>
          <a-descriptions-item label="当前版本">v1.0.0</a-descriptions-item>
          <a-descriptions-item label="开源协议">MIT License</a-descriptions-item>
          <a-descriptions-item label="后端框架">Spring Boot 3.5.0</a-descriptions-item>
          <a-descriptions-item label="前端框架">Vue 3.5.15</a-descriptions-item>
          <a-descriptions-item label="数据库">MySQL 8.0+</a-descriptions-item>
          <a-descriptions-item label="缓存服务">Redis 5.0+</a-descriptions-item>
          <a-descriptions-item label="构建工具">Maven 3.6+ / Vite 6.3+</a-descriptions-item>
        </a-descriptions>
      </a-card>
    </div>

    <!-- 官方链接 -->
    <div class="links-section">
      <a-card title="官方链接" :bordered="false" class="links-card">
        <a-space :size="20">
          <a-button type="primary" @click="goToDocs">
            <template #icon>
              <icon-file />            </template>
            开发文档
          </a-button>
          <a-button @click="goToGithub">
            <template #icon>
              <icon-github />            </template>
            GitHub
          </a-button>
          <a-button @click="goToGitee">
            <template #icon>
              <icon-cloud />            </template>
            Gitee
          </a-button>
        </a-space>
      </a-card>
    </div>

    <!-- 核心特性 -->
    <div class="features-section">
      <a-card title="核心特性" :bordered="false" class="features-card">
        <a-row :gutter="16">
          <a-col :xs="24" :sm="12" :md="8" v-for="feature in features" :key="feature.title"
>
            <div class="feature-item">
              <div class="feature-icon" :style="{ color: feature.color }">
                <component :is="feature.icon" :size="32" />
              </div>
              <div class="feature-content">
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </div>

    <!-- 页脚 -->
    <div class="footer">
      <p>&copy; 2024 FxAdmin Team. All Rights Reserved.</p>
    </div>
  </div>

  </snow-page>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Message } from "@arco-design/web-vue";
import {
  IconCode,
  IconCloud,
  IconMobile,
  IconThunderbolt,
  IconFile,
  IconSettings,
  IconBug,
  IconLock,
  IconRobot,
  IconCheckCircle,
  IconClockCircle,
  IconBook,
  IconGithub,
  IconUserGroup,
  IconStorage,
  IconDashboard,
  IconImage,
  IconEdit,
  IconTranslate,
  IconEye,
  IconVideoCamera,
  IconRecord,
  IconScan,
  IconCopy,
  IconFire,
  IconTool
} from "@arco-design/web-vue/es/icon";

// 统计数据
const stats = ref([
  {
    key: "backend",
    label: "后端模块",
    value: "12+",
    // icon: IconDatabase,
    color: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
  },
  {
    key: "frontend",
    label: "前端组件",
    value: "30+",
    icon: IconCode,
    color: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
  },
  {
    key: "api",
    label: "接口数量",
    value: "50+",
    // icon: IconApi,
    color: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
  },
  {
    key: "performance",
    label: "优化项",
    value: "20+",
    icon: IconThunderbolt,
    color: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
  }
]);

// 技术栈分类
const techSections = ref([
  {
    key: "backend",
    title: "后端技术",
    // icon: IconDatabase,
    color: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
    groups: [
      {
        title: "核心框架",
        techs: [
          { name: "Spring Boot", version: "3.5.0", icon: IconDashboard, color: "#6db33f" },
          { name: "Spring Security", version: "6.x",  color: "#6db33f" },
          { name: "MyBatis-Plus", version: "3.5.8", icon: IconEdit, color: "#6db33f" }
        ]
      },
      {
        title: "数据存储",
        techs: [
          { name: "MySQL", version: "8.0+", icon: IconStorage, color: "#00758f" },
          { name: "Redis", version: "5.0+", icon: IconThunderbolt, color: "#dc382d" },
          { name: "Druid", version: "1.2.23", icon: IconBug, color: "#00758f" }
        ]
      },
      {
        title: "工具库",
        techs: [
          { name: "Hutool", version: "5.8.38", icon: IconTool, color: "#ff7d00" },
          { name: "FastJson", version: "2.0.32", icon: IconFire, color: "#ff7d00" },
          { name: "Lombok", version: "-", icon: IconCheckCircle, color: "#28a745" },
          { name: "Knife4j", version: "4.5.0", icon: IconBook, color: "#17a2b8" }
        ]
      },
      {
        title: "其他组件",
        techs: [
          { name: "IP2Region", version: "2.7.0", icon: IconCloud, color: "#6c757d" },
          { name: "UserAgent", version: "1.21", icon: IconMobile, color: "#6c757d" },
          { name: "Freemarker", version: "-", icon: IconFile, color: "#007bff" }
        ]
      }
    ]
  },
  {
    key: "frontend",
    title: "前端技术",
    icon: IconCode,
    color: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
    groups: [
      {
        title: "核心框架",
        techs: [
          { name: "Vue", version: "3.5.15", color: "#42b883" },
          { name: "Vite", version: "6.3.5", icon: IconThunderbolt, color: "#646cff" },
          { name: "Pinia", version: "2.3.0", icon: IconStorage, color: "#e54d26" }
        ]
      },
      {
        title: "UI 框架",
        techs: [
          { name: "Arco Design", version: "2.57.0", icon: IconImage, color: "#165dff" },
          { name: "VChart", version: "1.11.0",  color: "#165dff" },
          { name: "WangEditor", version: "5", icon: IconEdit, color: "#165dff" }
        ]
      },
      {
        title: "工具库",
        techs: [
          { name: "Axios", version: "1.6.8", icon: IconCloud, color: "#007bff" },
          { name: "vue-i18n", version: "10.0.0", icon: IconTranslate, color: "#28c76f" },
          { name: "@vueuse/core", version: "12.4.0", icon: IconSettings, color: "#6c757d" }
        ]
      },
      {
        title: "编辑器 & 多媒体",
        techs: [
          { name: "CodeMirror", version: "6", icon: IconCode, color: "#f5de19" },
          { name: "xgplayer", version: "3", icon: IconVideoCamera, color: "#fa534c" },
          { name: "recorder-core", version: "-", icon: IconRecord, color: "#17a2b8" }
        ]
      }
    ]
  },
  {
    key: "website",
    title: "网站端",
    icon: IconCloud,
    color: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
    groups: [
      {
        title: "核心框架",
        techs: [
          { name: "Nuxt", version: "3.15.4", color: "#00dc82" },
          { name: "Vue", version: "3.5.13", icon: IconMobile, color: "#42b883" },
          { name: "@nuxtjs/i18n", version: "9.1.1", icon: IconTranslate, color: "#28c76f" }
        ]
      },
      {
        title: "特性",
        techs: [
          { name: "SSR", version: "服务端渲染", icon: IconEye, color: "#007bff" },
          { name: "静态导出", version: "Static", icon: IconCopy, color: "#6c757d" },
          { name: "SEO 优化", version: "-",  color: "#17a2b8" }
        ]
      },
      {
        title: "工具库",
        techs: [
          { name: "@vueuse/nuxt", version: "12.0.1", icon: IconSettings, color: "#6c757d" },
          { name: "Pinia", version: "2.2.6", icon: IconStorage, color: "#e54d26" }
        ]
      }
    ]
  },
  {
    key: "mobile",
    title: "移动端",
    icon: IconMobile,
    color: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
    groups: [
      {
        title: "核心框架",
        techs: [
          { name: "UniApp", version: "-", icon: IconMobile, color: "#2b85e4" },
          { name: "Vue", version: "3", color: "#42b883" },
          { name: "Pinia", version: "2", icon: IconStorage, color: "#e54d26" }
        ]
      },
      {
        title: "支持平台",
        techs: [
          { name: "H5", version: "浏览器", icon: IconEye, color: "#007bff" },
          { name: "微信小程序", version: "WeChat", icon: IconRobot, color: "#07c160" },
          { name: "APP", version: "Android/iOS", icon: IconMobile, color: "#007bff" }
        ]
      },
      {
        title: "工具库",
        techs: [
          { name: "uview-pro", version: "-", icon: IconImage, color: "#ff7d00" },
          { name: "alova", version: "3.3.3", icon: IconCloud, color: "#007bff" },
          { name: "z-paging", version: "-", color: "#6c757d" }
        ]
      }
    ]
  },
  {
    key: "devtools",
    title: "开发工具",
    icon: IconTool,
    color: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
    groups: [
      {
        title: "构建工具",
        techs: [
          { name: "Maven", version: "3.6+", icon: IconCheckCircle, color: "#f5de19" },
          { name: "Vite", version: "6.3+", icon: IconThunderbolt, color: "#646cff" },
          { name: "pnpm", version: "8+", icon: IconClockCircle, color: "#6c757d" }
        ]
      },
      {
        title: "代码质量",
        techs: [
          { name: "TypeScript", version: "5.2+", icon: IconCode, color: "#3178c6" },
          { name: "ESLint", version: "9+", icon: IconBug, color: "#f5de19" },
          { name: "Prettier", version: "3+", icon: IconEdit, color: "#28a745" }
        ]
      },
      {
        title: "API 文档",
        techs: [
          { name: "Knife4j", version: "4.5.0", icon: IconBook, color: "#17a2b8" },
          { name: "OpenAPI", version: "3.0", color: "#2cbe4e" }
        ]
      }
    ]
  }
]);

// 核心特性
const features = ref([
  {
    title: "RBAC 权限",
    desc: "用户/角色/菜单三级权限控制，支持按钮级权限和数据权限",
    // icon: IconSafety,
    color: "#1890ff"
  },
  {
    title: "代码生成",
    desc: "支持单表、树表、主子表的代码生成，一键生成 CRUD 代码",
    icon: IconThunderbolt,
    color: "#52c41a"
  },
  {
    title: "系统日志",
    desc: "AOP 切面自动记录操作日志，支持模块化管理和参数追踪",
    icon: IconFile,
    color: "#722ed1"
  },
  {
    title: "接口文档",
    desc: "Knife4j 自动生成 OpenAPI3 接口文档，支持在线调试和导出",
    icon: IconBook,
    color: "#1890ff"
  },
  {
    title: "全端支持",
    desc: "Web 管理端 + Web 网站端 + 移动端，一套代码多端运行",
    icon: IconMobile,
    color: "#722ed1"
  },
  {
    title: "缓存优化",
    desc: "集成 Redis 缓存，支持高频数据缓存和分布式锁",
    icon: IconStorage,
    color: "#52c41a"
  },
  {
    title: "SQL 监控",
    desc: "Druid 连接池内置 SQL 监控，便于排查慢查询",
    icon: IconBug,
    color: "#f5222d"
  },
  {
    title: "国际化",
    desc: "支持多语言切换，内置中英文，可扩展更多语种",
    icon: IconTranslate,
    color: "#1890ff"
  },
  {
    title: "数据字典",
    desc: "数据字典类型管理，字典数据缓存优化，支持级联选择",
    icon: IconDashboard,
    color: "#722ed1"
  },
  {
    title: "在线用户",
    desc: "实时监控在线用户，支持强制下线和会话管理",
    icon: IconUserGroup,
    color: "#52c41a"
  },
  {
    title: "定时任务",
    desc: "基于 Quartz 的定时任务管理，支持 Cron 表达式和执行日志",
    icon: IconClockCircle,
    color: "#1890ff"
  },
  // {
  //   title: "文件管理",
  //   desc: "支持本地存储和 OSS 云存储，支持图片预览和在线编辑",
  //   icon: IconImage,
  //   color: "#722ed1"
  // },
]);

const goToDocs = () => {
  Message.success("正在跳转到开发文档...");
  // TODO: 跳转到文档页面
};

const goToGithub = () => {
  window.open("https://github.com", "_blank");
};

const goToGitee = () => {
  window.open("https://gitee.com", "_blank");
};
</script>

<style lang="scss" scoped>
.about-container {
  background: #f5f5f5;
  padding: 20px;
  max-height: 80vh;
  /* 垂直方向内容溢出时显示滚动条，允许下滑 */
  overflow-y: auto;
  /* 可选：添加内边距，避免内容贴边 */
  padding: 0 16px;
  /* 可选：隐藏横向滚动条（防止内容横向溢出） */
  overflow-x: hidden;
}

// 头部 Banner
.header-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 50px 40px;
  margin-bottom: 24px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);

  &::before {
    content: "";
    position: absolute;
    top: -50px;
    right: -50px;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  &::after {
    content: "";
    position: absolute;
    bottom: -30px;
    left: -30px;
    width: 150px;
    height: 150px;
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

.banner-left {
  flex: 1;
}

.banner-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.banner-subtitle {
  font-size: 24px;
  margin: 0 0 8px 0;
  opacity: 0.95;
  font-weight: 500;
}

.banner-desc {
  font-size: 18px;
  margin: 0 0 24px 0;
  opacity: 0.9;
}

.version-badge {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 16px 24px;
  text-align: center;
}

.version-label {
  display: block;
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.version-number {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 2px;
}

// 统计卡片
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1d2129;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #86909c;
  margin-top: 4px;
}

// 技术栈分类
.tech-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 24px;
}

.tech-section {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.section-header {
  padding: 24px;
  color: white;
  display: flex;
  align-items: center;
  gap: 12px;

  h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }
}

.section-content {
  padding: 24px;
  background: #fafafa;
}

.tech-group {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.group-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tech-tag {
  padding: 8px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  color: #1d2129;
  transition: all 0.2s ease;

  &:hover {
    border-color: #1890ff;
    box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
    transform: translateY(-2px);
  }
}

.tech-version {
  font-size: 12px;
  color: #86909c;
  margin-left: 8px;
  padding: 2px 6px;
  background: #f2f3f5;
  border-radius: 4px;
}

// 项目信息
.project-info-section,
.links-section,
.features-section {
  margin-bottom: 24px;
}

.info-card,
.links-card,
.features-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

// 核心特性
.feature-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.feature-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #f7f8fa;
}

.feature-content {
  flex: 1;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #1d2129;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: #86909c;
    margin: 0;
    line-height: 1.6;
  }
}

// 页脚
.footer {
  text-align: center;
  padding: 32px 0;
  color: #86909c;
  font-size: 14px;

  p {
    margin: 0;
  }
}

// 响应式
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .about-container {
    padding: 12px;
  }

  .header-banner {
    padding: 30px 20px;

    .banner-content {
      flex-direction: column;
      text-align: center;
    }

    .banner-title {
      font-size: 32px;
    }

    .banner-subtitle {
      font-size: 20px;
    }

    .banner-desc {
      font-size: 16px;
      margin: 16px 0 0;
    }

    .version-badge {
      margin-top: 24px;
    }
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .section-header {
    justify-content: center;
  }
}
</style>
