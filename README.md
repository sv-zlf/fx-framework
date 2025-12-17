# FxAdmin 快速开发脚手架

## 项目概述

FxAdmin 是基于 **SpringBoot 3.5.0 + Vue 3**构建的后台管理快速开发脚手架，集成后端核心业务组件与前端丰富交互插件，帮助开发者快速落地业务系统，减少重复开发成本。

## 技术栈

### 后端技术

| 技术分类   | 具体组件/版本                                          |
| ------ | ------------------------------------------------ |
| 核心框架   | SpringBoot 3.5.0                                 |
| 权限安全   | Spring Security + JWT（jjwt 0.12.5）               |
| ORM 框架 | MyBatis-Plus 3.5.8（SpringBoot3 适配版）              |
| 数据库连接池 | Druid 1.2.23                                     |
| 缓存技术   | Spring Data Redis                                |
| 接口文档   | Knife4j 4.5.0 + SpringDoc OpenAPI 2.8.4          |
| 工具类库   | Hutool 5.8.38、FastJson 2.0.32、Commons Codec 1.15 |
| 数据库驱动  | MySQL Connector/J（适配 MySQL 8.0+）                 |
| 模板引擎   | Freemarker                                       |
| 开发效率工具 | Lombok                                           |

### 前端技术

基于 Vue 3 生态，集成多场景交互插件：
| 技术分类 | 具体组件/版本 |
|---------|--------------|
| 核心框架 | Vue 3.5.15、Vue Router 4.3.0 |
| 状态管理 | Pinia 2.3.0 + pinia-plugin-persistedstate（状态持久化） |
| 网络请求 | Axios 1.6.8 |
| 样式/颜色工具 | @arco-design/color、vue-color-kit、vue-pick-colors |
| 代码编辑 | Codemirror 6、vue-codemirror6、@codemirror/lang-javascript/JSON/Vue、@codemirror/theme-one-dark |
| 富文本编辑 | @wangeditor/editor（v5） + @wangeditor/editor-for-vue |
| 数据可视化 | @visactor/vchart 1.11.0 + @visactor/vchart-arco-theme |
| 国际化 | vue-i18n 10.0.0-alpha.3 |
| 交互工具 | sortablejs、vuedraggable（拖拽）、vue-virtual-scroller（虚拟滚动）、driver.js（操作引导） |
| 多媒体工具 | xgplayer（视频播放）、recorder-core（录音）、jsbarcode（条形码）、qrcode（二维码） |
| 设备标识 | @fingerprintjs/fingerprintjs、fingerprintjs2（设备指纹） |
| 通用工具 | @vueuse/core 12.4.0、pinyin-pro（拼音处理）、uuid 11.1.0、print-js（打印） |
| 进度提示 | nprogress |

## 核心功能

### 系统基础能力

1. **RBAC权限体系**：用户/角色/菜单三级权限控制，结合JWT实现接口认证与权限拦截
2. **系统配置管理**：数据字典维护、系统参数配置、用户个人信息管理
3. **数据库快捷操作**：MyBatis-Plus封装CURD、分页查询、逻辑删除、条件构造
4. **缓存优化**：Redis缓存高频数据（用户信息、字典数据），提升系统响应速度
5. **接口文档**：Knife4j自动生成OpenAPI3接口文档，支持在线调试、接口导出
6. **SQL监控**：Druid连接池内置SQL监控，便于排查慢查询

### 前端增强能力

1. **多类型编辑**：代码编辑（多语法高亮）、富文本编辑、颜色选择、拼音转换
2. **数据可视化**：VChart图表组件（折线图、柱状图、饼图等）+ Arco主题适配
3. **高效交互**：拖拽排序、虚拟滚动（大数据列表优化）、页面加载进度条、新手操作引导
4. **多媒体支持**：视频播放、录音、条形码/二维码生成与展示
5. **国际化**：vue-i18n多语言切换（可扩展多语种）
6. **状态持久化**：Pinia结合持久化插件，保持登录状态、用户配置等数据不丢失

## 快速开始

### 环境准备

| 环境/工具   | 版本要求                   |
| ------- | ---------------------- |
| JDK     | 17+（SpringBoot 3 强制要求） |
| MySQL   | 8.0+                   |
| Redis   | 5.0+                   |
| Maven   | 3.6+                   |
| Node.js | 16+（建议 18 LTS 版本）      |
| 包管理器    | npm/yarn/pnpm（推荐pnpm）  |
