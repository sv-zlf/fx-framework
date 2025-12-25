# FxAdmin

## 项目简介

FxAdmin 是一款全栈快速开发框架，基于 **Spring Boot 3 + Vue 3** 架构设计，集成丰富的前后端组件库和代码生成能力。框架专注于为中小型项目提供敏捷开发解决方案，帮助开发团队快速落地业务系统，大幅降低重复开发成本。

### 在线演示

> 🎨 **Web 管理端**：[点击访问](http://115.190.79.132:81) （账号：admin / 密码：admin123）
> 
> 📱 **移动端**：[扫码体验](http://115.190.79.132:81/#/login)

> 📚 **接口文档**：[Knife4j 文档](http://115.190.79.132:8080/doc.html)

### 项目特色


#### 1. 架构设计
- **前后端分离**：采用主流的前后端分离架构，便于团队协作和独立部署
- **全栈覆盖**：后端服务 + Web管理端 + Web网站端 + 移动端，一套代码多端运行
- **微服务就绪**：模块化设计，支持扩展为微服务架构

#### 2. 核心能力
- **权限控制**：基于 RBAC 的用户-角色-菜单三级权限体系，支持按钮级权限和数据权限
- **系统管理**：提供数据字典、系统参数、部门管理、岗位管理、公告管理等基础功能
- **代码生成**：支持单表、树表、主子表的代码生成，一键生成前后端 CRUD 代码
- **系统监控**：集成 Druid SQL 监控、系统日志、在线用户监控、定时任务管理
- **操作日志**：通过 AOP 切面自动记录用户操作，支持模块化管理
- **接口文档**：集成 Knife4j 自动生成 OpenAPI3 接口文档，支持在线调试和导出
- **缓存支持**：集成 Redis 缓存，提升系统响应速度

#### 3. 技术选型
- **后端技术**：Spring Boot 3.5.0 + MyBatis-Plus 3.5.8 + Spring Security + Redis + MySQL 8.0
- **前端技术**：Vue 3.5.15 + Arco Design Vue 2.57.0 + Vite 6.3.5 + Pinia 2.3.0
- **网站端**：Nuxt 3.15.4（SSR 支持）
- **移动端**：UniApp + Vue 3（支持 H5、微信小程序、APP）

#### 4. 开发体验
- **代码规范**：统一使用 Lombok 简化代码，遵循阿里巴巴 Java 开发规范
- **类型安全**：TypeScript 全覆盖，编译期类型检查
- **工具集成**：集成 ESLint + Prettier + Stylelint，统一代码风格
- **调试友好**：完善的接口文档和调试工具，开发效率显著提升

### 适用场景

FxAdmin 适用于以下场景：

- **企业管理系统**：企业内部管理、流程审批、权限控制
- **SaaS 平台**：多租户管理、平台管理 + 租户独立业务
- **内容管理系统**：内容发布、审核流程、分类管理
- **电商平台**：商品管理、订单管理、会员管理
- **政务系统**：行政审批、信息公开、政务服务平台
- **物联网平台**：设备管理、数据采集、远程控制
- **教育平台**：课程管理、在线考试、学员管理

### 内置功能

| 功能模块 | 功能说明 |
|---------|---------|
| 用户管理 | 用户信息、用户状态、用户导入导出 |
| 角色管理 | 角色分配、权限配置、数据权限 |
| 菜单管理 | 菜单配置、按钮权限、排序管理 |
| 部门管理 | 组织架构、部门人员、树形展示 |
| 字典管理 | 字典类型、字典数据、缓存优化 |
| 参数管理 | 系统参数、配置管理、动态更新 |
| 操作日志 | 操作记录、日志查询、统计分析 |
| 在线用户 | 在线监控、强制下线、会话管理 |
| 定时任务 | 任务管理、执行日志、Cron 表达式 |
| 代码生成 | 单表生成、树表生成、主子表生成 |
| 系统接口 | 接口文档、在线调试、接口测试 |

### 系统要求

| 环境/工具 | 版本要求 |
|---------|---------|
| JDK | 17+ (Spring Boot 3 强制要求) |
| MySQL | 8.0+ |
| Redis | 5.0+ |
| Maven | 3.6+ |
| Node.js | 18+ (推荐 18 LTS) |
| 包管理器 | pnpm (推荐) |
| IDE | IntelliJ IDEA / VSCode (推荐) |

### 技术栈

#### 后端技术
| 技术分类 | 组件/版本 |
|---------|----------|
| 核心框架 | Spring Boot 3.5.0 |
| 权限安全 | Spring Security + JWT (0.12.5) |
| ORM 框架 | MyBatis-Plus 3.5.8 (SpringBoot3 适配版) |
| 数据库连接池 | Druid 1.2.23 (含 SQL 监控) |
| 缓存 | Spring Data Redis |
| 接口文档 | Knife4j 4.5.0 + SpringDoc OpenAPI 2.8.4 |
| 工具库 | Hutool 5.8.38、FastJson 2.0.32 |
| 数据库驱动 | MySQL Connector/J (MySQL 8.0+) |
| 模板引擎 | Freemarker (代码生成) |
| 开发效率 | Lombok |

#### Web 管理端技术
| 技术分类 | 组件/版本 |
|---------|----------|
| 核心框架 | Vue 3.5.15 + Vite 6.3.5 |
| 状态管理 | Pinia 2.3.0 (支持持久化) |
| 网络请求 | Axios 1.6.8 |
| UI 组件库 | Arco Design Vue 2.57.0 |
| 代码编辑器 | CodeMirror 6 (JS/JSON/Vue) |
| 富文本编辑 | WangEditor 5 |
| 数据可视化 | VChart 1.11.0 |
| 国际化 | vue-i18n 10.0.0 |
| 多媒体 | xgplayer (视频)、recorder-core (录音) |

#### Web 网站端技术
| 技术分类 | 组件/版本 |
|---------|----------|
| 核心框架 | Nuxt 3.15.4 + Vue 3.5.13 |
| 状态管理 | Pinia 2.2.6 |
| 国际化 | @nuxtjs/i18n 9.1.1 |
| 工具库 | @vueuse/nuxt 12.0.1 |
| 特性 | SSR 服务端渲染、静态导出、SEO 优化 |

#### 移动端技术
| 技术分类 | 组件/版本 |
|---------|----------|
| 核心框架 | UniApp + Vue 3 |
| 状态管理 | Pinia + 持久化插件 |
| UI 组件 | uview-pro |
| HTTP 请求 | alova 3.3.3 |
| 支持平台 | H5、微信小程序、APP (Android/iOS) |

### 快速开始

#### 1. 后端启动

```bash
# 进入后端目录
cd demo

# 修改配置文件
# 编辑 src/main/resources/application.yml，配置数据库和 Redis 连接

# 启动服务
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

接口文档访问：`http://localhost:8080/doc.html`

#### 2. Web 管理端启动

```bash
# 进入前端目录
cd demo-admin

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建生产版本
pnpm build:prod
```

访问地址：`http://localhost:5173` (具体端口以控制台输出为准)

#### 3. Web 网站端启动

```bash
# 进入网站端目录
cd demo-web

# 安装依赖
pnpm install

# 启动开发服务器 (SSR 模式)
pnpm dev

# 构建生产版本
pnpm build

# 静态导出
pnpm generate
```

访问地址：`http://localhost:3000`

#### 4. 移动端启动

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

### 项目结构

```
demo/
├── demo/              # 后端服务 (Spring Boot)
│   └── src/
│       ├── main/
│       │   ├── java/  # Java 源代码
│       │   └── resources/
│       └── test/      # 测试代码
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

### 开发指南

详细开发指南请参考：
- 后端开发规范：`demo/HELP.md`
- 系统日志使用：`demo/SYSTEM_LOG_README.md`
- Web 管理端开发规范：`demo-admin/README.md`
- Web 网站端开发规范：`demo-web/README.md`
- 移动端开发规范：`demo-app/README.md`
- 贡献指南：`AGENTS.md`

### 框架对比

| 特性 | FxAdmin | 传统从零搭建 | 其他脚手架 |
|-----|---------|------------|-----------|
| 启动时间 | 1 天内 | 1-2 周 | 3-5 天 |
| 权限模块 | ✅ RBAC | ❌ 手动开发 | ⚠️ 基础 |
| 代码生成 | ✅ CRUD 全套 | ❌ 无 | ⚠️ 简单 |
| 多端支持 | ✅ Admin + Web + App | ❌ 仅 Web | ⚠️ 单端 |
| SSR 支持 | ✅ Nuxt 3 | ❌ 手动实现 | ❌ 无 |
| 接口文档 | ✅ 自动生成 | ❌ 手动维护 | ✅ |
| 监控工具 | ✅ Druid SQL | ❌ 无 | ⚠️ 部分 |
| 缓存集成 | ✅ Redis | ❌ 手动集成 | ⚠️ 可选 |
| 系统日志 | ✅ AOP 自动记录 | ❌ 手动开发 | ❌ 无 |

### 演示截图

> 由于无法在 README 中直接展示图片，请访问在线演示体验完整功能

### 开源协议

MIT License

### 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

### 联系方式

- 项目地址：[GitHub](https://github.com) / [Gitee](https://gitee.com)
- 问题反馈：[Issues](https://github.com/xxx/issues)

---

**让开发更简单，让项目更快上线！** 🚀
