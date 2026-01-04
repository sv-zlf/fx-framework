<template>
  <snow-page>
    <div class="about-container">
      <!-- 项目信息卡片 -->
      <a-card title="项目信息" :bordered="false" class="project-info-card">
        <a-descriptions :column="{ xs: 1, sm: 2, md: 3 }" bordered>
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

      <!-- 项目简介 -->
      <a-card title="项目简介" :bordered="false" class="intro-card">
        <div class="intro-content">
          <p>
            FxAdmin 是一个基于 Spring Boot 3 + Vue 3 的全栈快速开发框架，集成了 RBAC
            权限管理、代码生成、文件管理等企业级功能模块。
          </p>
          <p>
            项目采用前后端分离架构，支持 Web 管理后台、Nuxt 3 Web 前端和 UniApp 移动端，实现一套代码多端运行，大幅提升开发效率。
          </p>
          <div class="intro-highlights">
            <div class="highlight-item">
              <icon-check-circle class="highlight-icon" />
              <span>开箱即用，快速搭建企业级应用</span>
            </div>
            <div class="highlight-item">
              <icon-check-circle class="highlight-icon" />
              <span>前后端分离，支持多端部署</span>
            </div>
            <div class="highlight-item">
              <icon-check-circle class="highlight-icon" />
              <span>代码生成，提升开发效率 80%</span>
            </div>
            <div class="highlight-item">
              <icon-check-circle class="highlight-icon" />
              <span>完善的文档，降低学习成本</span>
            </div>
          </div>
        </div>
      </a-card>

      <!-- 官方链接 -->
      <a-card title="官方链接" :bordered="false" class="links-card">
        <a-space :size="16" wrap>
          <a-button class="link-btn-primary" size="large" @click="goToDocs">
            <template #icon>
              <icon-file />
            </template>
            开发文档
          </a-button>
          <a-button class="link-btn-secondary" size="large" @click="goToGithub">
            <template #icon>
              <icon-github />
            </template>
            GitHub
          </a-button>
          <a-button class="link-btn-secondary" size="large" @click="goToGitee">
            <template #icon>
              <icon-cloud />
            </template>
            Gitee
          </a-button>
        </a-space>
      </a-card>

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
                <a-tag v-for="tech in group.techs" :key="tech.name" class="tech-tag">
                  <component :is="tech.icon" :size="14" style="margin-right: 4px" />
                  {{ tech.name }}
                  <span v-if="tech.version" class="tech-version">{{ tech.version }}</span>
                </a-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 更新日志 -->
      <a-card title="更新日志" :bordered="false" class="changelog-card">
        <a-timeline>
          <a-timeline-item dot-color="#3B82F6">
            <div class="changelog-item">
              <div class="changelog-header">
                <span class="changelog-version">v1.0.0</span>
                <span class="changelog-date">2024-01-01</span>
              </div>
              <div class="changelog-content">
                <p>✨ 初始版本发布</p>
                <p>• 集成 RBAC 权限管理</p>
                <p>• 支持代码生成功能</p>
                <p>• 完善的文件管理系统</p>
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </a-card>

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
import { IconCode, IconCloud, IconFile, IconGithub, IconCheckCircle } from "@arco-design/web-vue/es/icon";

const techSections = ref([
  {
    key: "backend",
    title: "后端技术栈",
    icon: IconCode,
    color: "linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)",
    groups: [
      {
        title: "核心框架",
        techs: [
          { name: "Spring Boot", version: "3.5.0", icon: IconCode, color: "green" },
          { name: "MyBatis Plus", icon: IconCode, color: "blue" },
          { name: "Knife4j", icon: IconFile, color: "purple" },
          { name: "JWT", icon: IconFile, color: "orange" }
        ]
      },
      {
        title: "数据库 & 缓存",
        techs: [
          { name: "MySQL", version: "8.0+", icon: IconCloud, color: "blue" },
          { name: "Redis", version: "5.0+", icon: IconCloud, color: "red" }
        ]
      }
    ]
  },
  {
    key: "frontend",
    title: "前端技术栈",
    icon: IconCode,
    color: "linear-gradient(135deg, #F97316 0%, #EA580C 100%)",
    groups: [
      {
        title: "管理后台",
        techs: [
          { name: "Vue", version: "3.5.15", icon: IconCode, color: "green" },
          { name: "Vite", version: "6.3", icon: IconCode, color: "blue" },
          { name: "Arco Design", icon: IconCloud, color: "blue" },
          { name: "TypeScript", icon: IconCode, color: "blue" }
        ]
      },
      {
        title: "Web 前端",
        techs: [
          { name: "Nuxt", version: "3.x", icon: IconCode, color: "green" },
          { name: "SSR", icon: IconCode, color: "blue" },
          { name: "静态导出", icon: IconCloud, color: "purple" }
        ]
      },
      {
        title: "移动端",
        techs: [
          { name: "UniApp", icon: IconCloud, color: "blue" },
          { name: "Vue", version: "3", icon: IconCode, color: "green" },
          { name: "多端适配", icon: IconCloud, color: "purple" }
        ]
      }
    ]
  }
]);

const goToDocs = () => {
  Message.success("正在打开文档...");
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
  background: #f8fafc;
  max-height: 80vh;
  padding: 16px 24px;
  overflow-y: auto;
  overflow-x: hidden;
}

.project-info-card,
.intro-card,
.links-card,
.changelog-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.project-info-card {
  :deep(.arco-card-header) {
    border-bottom: 1px solid #e2e8f0;
    padding: 16px 20px;

    .arco-card-header-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  :deep(.arco-card-body) {
    padding: 20px;
  }

  :deep(.arco-descriptions) {
    .arco-descriptions-item-label {
      background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
      color: #475569;
      font-weight: 500;
      font-size: 14px;
    }

    .arco-descriptions-item-value {
      color: #1e293b;
      font-weight: 400;
      font-size: 14px;
    }

    .arco-descriptions-bordered-item {
      border-color: #e2e8f0;
    }
  }
}

.intro-card {
  :deep(.arco-card-header) {
    border-bottom: 1px solid #e2e8f0;
    padding: 16px 20px;

    .arco-card-header-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  :deep(.arco-card-body) {
    padding: 20px;
  }
}

.intro-content {
  p {
    font-size: 14px;
    color: #475569;
    line-height: 1.8;
    margin: 0 0 16px 0;

    &:last-child {
      margin-bottom: 24px;
    }
  }
}

.intro-highlights {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.highlight-icon {
  color: #10b981;
  flex-shrink: 0;
}

.links-card {
  :deep(.arco-card-header) {
    border-bottom: 1px solid #e2e8f0;
    padding: 16px 20px;

    .arco-card-header-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  :deep(.arco-card-body) {
    padding: 20px;
  }
}

.link-btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: #fff;
  font-weight: 500;
}

.link-btn-primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.link-btn-secondary {
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  font-weight: 500;
}

.link-btn-secondary:hover {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-color: #cbd5e1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.tech-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 24px;
}

.tech-section {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s ease;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
}

.section-header {
  padding: 20px 24px;
  color: white;
  display: flex;
  align-items: center;
  gap: 12px;

  h2 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    letter-spacing: -0.2px;
  }
}

.section-content {
  padding: 24px;
  background: #fff;
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
  color: #1e293b;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e2e8f0;
  letter-spacing: -0.2px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tech-tag {
  padding: 8px 14px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    border-color: #3b82f6;
    background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
    box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
    transform: translateY(-2px);
  }
}

.tech-version {
  font-size: 12px;
  color: #64748b;
  margin-left: 8px;
  padding: 2px 6px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 4px;
  font-weight: 500;
}

.changelog-card {
  :deep(.arco-card-header) {
    border-bottom: 1px solid #e2e8f0;
    padding: 16px 20px;

    .arco-card-header-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  :deep(.arco-card-body) {
    padding: 20px;
  }
}

.changelog-item {
  padding: 8px 0;
}

.changelog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.changelog-version {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.changelog-date {
  font-size: 14px;
  color: #64748b;
  font-weight: 400;
}

.changelog-content {
  p {
    font-size: 14px;
    color: #475569;
    line-height: 1.6;
    margin: 0 0 8px 0;
    font-weight: 400;
  }
}

.footer {
  text-align: center;
  padding: 32px 0 16px;
  color: #64748b;
  font-size: 14px;

  p {
    margin: 0;
  }
}

@media (max-width: 768px) {
  .about-container {
    padding: 12px 16px;
  }

  .project-info-card,
  .intro-card,
  .links-card,
  .changelog-card {
    margin-bottom: 16px;
  }

  .intro-highlights {
    grid-template-columns: 1fr;
  }

  .section-header {
    padding: 16px 20px;

    h2 {
      font-size: 18px;
    }
  }

  .section-content {
    padding: 20px;
  }

  .tech-group {
    margin-bottom: 20px;
  }

  .link-btn-primary,
  .link-btn-secondary {
    width: 100%;
  }

  .changelog-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .intro-highlights {
    grid-template-columns: 1fr;
  }
}
</style>
