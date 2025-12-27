<p align="center">
   <img 
  alt="logo" src="https://gitee.com/heihiesdf/img/raw/master/fx2.png"  style="width: 80px; height: 40px; object-fit: contain;"
/>
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Fx-Framework</h1>
<p align="center">
    <a href="https://github.com/sv-zlf/fx-framework/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 项目简介

**Fx-Framework**（风行框架）是一个功能完善、架构清晰的JAVA快速开发框架，基于 **Spring Boot 3 + Vue 3** 架构设计，整合了前后端分离的组件和代码生成功能。框架专注于为中小型项目提供完整的全栈快速开发解决方案，帮助开发团队快速搭建业务系统，大大降低全栈开发成本。

当然这种类似的快速开发的框架很多，比如大名鼎鼎的[若依框架](https://gitee.com/y_project/RuoYi)（So Strong）。自己写代码这么久，搞过后台、小程序、网站，心里一直有个念头：想自己动手搭一套趁手的开发脚手架（说框架其实有点太夸张了，但是好听）。把平时踩的坑、攒的经验都揉进去，做点真正实用的东西。如果做出来的东西大家觉得好用，愿意用，那对我来说就是最大的鼓励了。”

## 项目特性

### 1. 架构设计

- **前后端分离**：采用成熟的前后端分离架构，便于团队协作和敏捷开发
- **全栈覆盖**：后端API + Web管理端 + Web网站端 + 移动端，一套代码多端运行
- **微服务就绪**：模块化设计，易于扩展为微服务架构

### 2. 核心能力

- **权限控制**：基于 RBAC 的用户角色-菜单权限体系，支持按钮权限和数据权限
- **系统管理**：提供数据字典、系统参数、菜单管理、用户管理、角色管理、部门管理等完整功能
- **代码生成**：支持单表、主子表等代码一键生成，一键生成完整的 CRUD 代码
- **系统监控**：集成 Druid SQL 监控、系统日志、在线用户监控、定时任务管理
- **操作日志**：通过 AOP 面向切面自动记录用户操作，支持模块化管理
- **接口文档**：集成 Knife4j 自动生成 OpenAPI3 接口文档，方便在线调试和导出
- **缓存支持**：集成 Redis 缓存，提升系统响应速度

### 3. 技术选型

- **后端技术**：Spring Boot 3.5.0 + MyBatis-Plus 3.5.8 + Spring Security + Redis + MySQL 8.0
- **前端技术**：Vue 3.5.15 + Arco Design Vue 2.57.0 + Vite 6.3.5 + Pinia 2.3.0
- **网站端**：基于 Nuxt 3，支持 SSR 静态化
- **移动端**：UniApp + Vue 3，支持 H5、微信小程序、APP

### 4. 开发规范

- **代码风格**：统一使用 Lombok 简化代码，保持优雅的 Java 开发风格
- **类型安全**：TypeScript 全面覆盖，编译类型检查更安全
- **代码规范**：统一使用 ESLint + Prettier + Stylelint，统一代码格式
- **调试友好**：完善的接口文档和调试步骤，开发体验更佳

## 适用场景

Fx-Framework 适用于以下场景：

- **企业管理系统**：企业用户管理、流程管理、权限管理等
- **SaaS 平台**：多租户管理、平台管理 + 租户业务
- **内部管理系统**：内部发帖、咨询反馈、内部业务管理
- **政务系统**：新闻发布、文档管理、发文管理、统计报表
- **电商系统**：商品管理、订单管理、库存管理
- **商务系统**：系统管理、日志管理、数据统计、数据导出

## 技术架构

```
┌─────────────────────────────────────────────────────┐
│                    客户端层                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │Web管理端 │  │ Web网站  │  │ 移动APP  │  │
│  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────┘
                          ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────┐
│                     网关层                            │
│              Nginx / API Gateway                       │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                    应用层                              │
│  ┌──────────────────────────────────────┐  │
│  │     Spring Boot 3.5 应用            │  │
│  │  ┌──────────┐  ┌──────────────┐   │  │
│  │  │Security  │  │Controller层   │   │  │
│  │  └──────────┘  └──────────────┘   │  │
│  │  ┌──────────┐  ┌──────────────┐   │  │
│  │  │Service层 │  │AOP切面      │   │  │
│  │  └──────────┘  └──────────────┘   │  │
│  └──────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                     数据层                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  MySQL   │  │  Redis   │  │ MyBatis  │  │
│  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────┘
```

## 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **pnpm**: 8.0+

### 后端启动

```bash
# 进入后端目录
cd demo

# 配置数据库
# 修改 src/main/resources/application.yml 中的数据库配置

# 启动后端服务
mvn spring-boot:run
```

访问地址：`http://localhost:8080`

### 前端管理端启动

```bash
# 进入前端管理端目录
cd demo-admin

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问地址：`http://localhost:5173`

### 前端Web网站启动

```bash
# 进入Web网站目录
cd demo-web

# 安装依赖
pnpm install

# 启动开发服务器 (SSR 模式)
pnpm dev

# 静态站点生成
pnpm generate
```

访问地址：`http://localhost:3000`

### 移动端启动

```bash
# 进入移动端目录
cd demo-app

# 安装依赖
pnpm install

# H5 开发
pnpm dev

# 微信小程序开发
pnpm dev:mp

# 构建生产版本
pnpm build
```

## 项目结构

```
ndemo/
├── demo/              # 后端服务 (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/  # Java 源代码
│   │   │   └── resources/
│   │   └── test/      # 测试代码
├── demo-admin/        # Web 管理端 (Vue 3 + Vite)
│   ├── src/           # 源代码
│   ├── public/        # 静态资源
│   └── build/         # 构建输出
├── demo-web/          # Web 网站端 (Nuxt 3)
│   ├── assets/        # 静态资源
│   ├── components/    # Vue 组件
│   ├── composables/   # 组合式函数
│   ├── layouts/       # 页面布局
│   ├── pages/         # 页面路由
│   ├── server/        # 服务端 API
│   └── stores/        # 状态管理
└── demo-app/          # 移动端 (UniApp + Vue 3)
    ├── src/           # 源代码
    └── uni_modules/   # UniApp 插件
```

## 框架对比

| 特性     | FxFramework         | 从零构建  | 其他优秀框架 |
| ------ | ------------------- | ----- | ------ |
| 启动时间   | 1 分钟                | 1-2 天 | 3-5 天  |
| 权限模型   | 完整RBAC              | 需手动开发 | ❌ 无完整  |
| 代码生成   | 完整CRUD 全套           | 需手动开发 | ❌ 简易版  |
| 多端支持   | 完整Admin + Web + App | 仅Web端 | ❌ 仅管理端 |
| SSR 支持 | 完整Nuxt 3            | 需手动开发 | ❌ 仅客户端 |
| 接口文档   | 完整自动生成              | 需手动开发 | 部分手动   |
| 监控步骤   | 完整Druid SQL         | ❌ 无   | ❌ 简易   |
| 缓存集成   | 完整Redis             | 需手动开发 | 部分支持   |
| 系统日志   | 完整AOP 自动记录          | 需手动开发 | ❌ 无    |

## 开源协议

MIT License

## 贡献指南

欢迎通过 Issue 和 Pull Request 来帮助改进项目。

## 联系方式

- 项目地址：[GitHub]([sv-zlf/fx-framework](https://github.com/sv-zlf/fx-framework?tab=readme-ov-file)) / [Gitee]([zlf/fx-framework](https://gitee.com/heihiesdf/fx-framework))
- 问题反馈：[Issues]([GitHub · Where software is built](https://github.com/sv-zlf/fx-framework/issues))

## 致谢

Fx-Framework 基于以下开源项目构建，感谢原作者的贡献：

**uniest** - uniapp开发

- 原项目地址：[GitHub](https://github.com/unibest-tech/unibest)

- demo-app 模块基于此项目进行深度定制和优化

**SnowAdmin** - Vue 3 + Arco Design 管理后台框架

- 原项目地址：[GitHub](https://github.com/WangFan-io/SnowAdmin)
- demo-admin 模块基于此项目进行深度定制和优化

**Spring Boot** - Java 企业级开发框架

**Vue 3** - 渐进式 JavaScript 框架

**Arco Design** - 字节跳动企业级 UI 组件库

**MyBatis-Plus** - MyBatis 增强工具

---

**让开发更快速，让项目更精彩！** 🚀
