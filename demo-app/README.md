# Fx App - 移动端

基于 [unbest](https://github.com/feige996/uniest) 进行深度定制和优化的移动端应用，为 Fx-Framework 项目提供移动端解决方案。

## 项目说明

**Fx App** 是基于开源项目 **uniest** 进行修改和扩展的移动端应用，为 Fx-Framework 项目提供完整的移动端解决方案。

### 基础来源

本项目基于开源项目 **uniest** 进行修改和扩展，感谢原作者的贡献。原始项目地址：[uniest GitHub](https://github.com/feige996/uniest)

### 主要改动

相较于原始 uniest 项目，本项目中进行了以下优化和定制：

1. **业务对接**
   - 与后端 Spring Boot 服务完整对接
   - 实现登录、注册等核心功能
   - 集成系统权限管理
   2. **API优化**
   - 统一API接口路径和参数格式
   - 优化认证流程，使用 JWT Token
   - 完善错误处理和提示信息
   3. **UI优化**
   - 统一UI风格与Web管理端保持一致
   - 优化移动端适配和交互体验
   4. **功能定制**
   - 根据业务需求定制特定功能模块

## 技术栈

- **核心框架**：Vue 3.5.15 + TypeScript 5.3.0
- **跨平台框架**：UniApp
- **UI组件库**：wot-ui
- **构建工具**：Vite 6.3.5
- **状态管理**：Pinia 2.3.0
- **HTTP请求**：uni.request (封装axios)
- **样式框架**：UnoCSS
- **代码规范**：ESLint + Prettier
- **提交规范**：Commitlint + Husky

## 支持平台

| H5  | iOS | 安卓  | 微信小程序 |
| --- | --- | --- | ----- |
| ✅   | ✅   | ✅   | ✅     |

## 功能特性

### 基础功能

- **用户认证**：完整的登录、注册、登出功能
- **权限管理**：基于Token的权限控制
- **路由管理**：完整的页面路由系统
- **状态管理**：使用Pinia进行全局状态管理
- **主题支持**：支持明暗主题切换
- **多语言**：国际化支持（i18n）

### 业务功能

- **个人中心**：用户信息管理、设置
- **系统管理**：与后台管理系统同步的业务功能

## 项目结构

```
demo-app/
├── src/                    # 源代码
│   ├── api/               # API接口
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── config/            # 配置文件
│   ├── locales/           # 国际化文件
│   ├── pages/             # 页面
│   ├── static/            # 静态文件
│   ├── store/             # 状态管理
│   ├── uni_modules/       # UniApp插件
│   ├── utils/             # 工具函数
│   ├── App.vue            # 应用入口
│   ├── main.ts            # 主入口
│   └── manifest.json      # 应用配置
├── public/                 # 公共资源
├── .editorconfig          # 编辑器配置
├── eslint.config.mjs       # ESLint配置
├── package.json            # 项目依赖
├── pages.config.ts         # 页面配置
├── pnpm-lock.yaml          # 依赖锁文件
├── tsconfig.json           # TypeScript配置
├── uno.config.ts           # UnoCSS配置
├── vite.config.ts           # Vite配置
└── README.md               # 项目说明
```

## 快速开始

### 环境要求

- **Node.js**: 18.0.0+
- **pnpm**: 8.0.0+
- **HBuilderX**: 最新版（推荐，开发体验更好）
- **VS Code**: 最新版（可选）

### 安装依赖

```bash
# 安装pnpm（如果未安装）
npm install -g pnpm

# 安装项目依赖
pnpm install
```

### 开发模式

```bash
# H5 开发
pnpm dev:h5

# 微信小程序开发
pnpm dev:mp

# APP 开发（需要HBuilderX）
# 1. 使用HBuilderX打开项目
# 2. 运行到手机模拟器或真机
```

### 生产构建

```bash
# H5 构建
pnpm build:h5

# 微信小程序构建
pnpm build:mp

# APP 构建（需要HBuilderX）
# 1. 使用HBuilderX打开项目
# 2. 发行 -> APP-云打包/原生App-云打包
```

## 开发指南

### 1. 添加新页面

1. 在 `src/pages/` 对应模块下创建页面文件
2. 在 `pages.config.ts` 中配置页面路由
3. 在菜单中配置页面入口（如需要）

### 2. 添加新API

1. 在 `src/api/` 对应模块下创建API文件
2. 导出API方法
3. 在页面中import并使用

### 3. 使用公共组件

项目提供了丰富的公共组件，位于 `src/components/` 下，按需引入使用。

### 4. 状态管理

使用Pinia进行状态管理，Store文件位于 `src/store/` 下。

```typescript
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
```

## 环境变量

| 变量名                | 说明       | 默认值      |
| ------------------ | -------- | -------- |
| VITE_APP_TITLE     | 应用标题     | Demo App |
| VITE_APP_BASE_API  | API基础路径  | /api     |
| VITE_APP_OPEN_MOCK | 是否开启Mock | false    |

## 代码规范

项目使用 ESLint + Prettier 进行代码规范检查：

- **ESLint**: JavaScript/TypeScript代码检查
- **Prettier**: 代码格式化

提交代码前会自动执行 lint-staged 检查，确保代码质量。

## 提交规范

项目使用 Commitlint 规范提交信息，格式为：

```
type(scope): description
```

- **type**: feat, fix, refactor, test, chore, docs, style, perf, build, ci, revert, wip, release, deps, merge, sync
- **scope**: auth, user, home, etc.
- **description**: 提交描述

## 开源协议

MIT License

## 参考项目

- [uniest](https://github.com/feige996/uniest) - 本项目基于此项目进行定制
- [UniApp](https://uniapp.dcloud.net.cn/) - DCloud推出的跨平台开发框架
- [Vue 3](https://vuejs.org/) - 渐进式JavaScript框架
- [wot-ui](https://wot-design-uni.netlify.app/) - 高性能的uni-app组件库

## 联系方式

- 项目地址：[GitHub](https://github.com) / [Gitee](https://gitee.com)
- 问题反馈：[Issues](https://github.com/xxx/issues)

---

**让开发更快速，让项目更精彩！** 🚀
