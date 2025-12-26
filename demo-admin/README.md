# Fx Admin - 管理后台

<div>
    <p>
    <a href="https://github.com/WangFan-io/SnowAdmin" target="_blank">
          <img src="https://img.shields.io/badge/License-MIT-%2343aefc" alt="License">
        </a>
        <a href="https://github.com/WangFan-io/SnowAdmin" target="_blank">
          <img src="https://img.shields.io/badge/vue_.js-vue3_.x-%2300b42a" alt="Vue">
        </a>
    <a href="https://github.com/WangFan-io/SnowAdmin" target="_blank">
          <img src="https://img.shields.io/badge/Vite-6x-%2311B337" alt="Vite">
        </a>
     <a href="https://github.com/WangFan-io/SnowAdmin" target="_blank">
          <img alt="arco.design" src="https://img.shields.io/badge/arco.design-2.57.0-%23%2028%2C175%2C%2063">
        </a>
    </p>
</div>

## 项目说明

**Fx Admin** 是基于 [SnowAdmin](https://github.com/WangFan-io/SnowAdmin) 进行深度定制和优化的后台管理端，为 Fx-Framework 项目提供完整的管理后台解决方案。

### 基础来源

本项目基于开源项目 **SnowAdmin** 进行修改和扩展，感谢原作者的贡献。原始项目地址：[SnowAdmin GitHub](https://github.com/WangFan-io/SnowAdmin)

### 主要改动

相较于原始 SnowAdmin 项目，本项目中进行了以下优化和定制：

1. **业务模块优化**
   - 系统日志查询页面：实现完整的日志查询、详情查看、删除功能
   - 在线用户管理：添加强制退出功能（仅管理员可用）
   - 优化登录和注册界面，提升用户体验
   2. **API对接优化**
   - 统一API接口路径和参数格式
   - 优化登录认证流程，使用 Spring Security + JWT
   - 完善错误处理和提示信息
   3. **代码生成集成**
   - 集成后端代码生成功能
   - 支持配置化代码生成
   4. **系统监控**
   - 集成系统日志监控
   - 在线用户监控和管理
   5. **样式优化**
   - 统一UI风格和配色方案
   - 优化移动端适配

## 技术栈

- **核心框架**：Vue 3.5.15 + TypeScript 5.3.0
- **构建工具**：Vite 6.3.5
- **状态管理**：Pinia 2.3.0
- **UI组件库**：Arco Design Vue 2.57.0
- **路由管理**：Vue Router 4.x
- **HTTP请求**：Axios
- **代码规范**：ESLint + Prettier + Stylelint
- **提交规范**：Commitlint + Husky + lint-staged

## 功能特性

### 基础功能

- **登录注册**：完整的登录和注册功能
- **动态路由**：基于权限的动态路由和菜单
- **多标签页**：支持多标签页和标签页操作
- **面包屑导航**：自动生成面包屑导航
- **主题切换**：支持明暗主题切换（待完善）
- **全屏模式**：支持全屏显示切换

### 系统管理

- **用户管理**：用户CRUD、角色分配、密码重置
- **角色管理**：角色CRUD、权限分配、数据权限
- **菜单管理**：菜单CRUD、权限配置、图标配置
- **字典管理**：数据字典类型和字典项管理
- **部门管理**：组织架构管理（待完善）

### 系统监控

- **在线用户**：实时查看在线用户，支持强制退出
- **系统日志**：完整的操作日志查询，支持条件过滤
- **定时任务**：定时任务管理（待完善）

### 工具功能

- **代码生成**：从数据库导入表结构，一键生成前后端代码
- **表单构建**：可视化表单设计（待完善）

## 项目结构

```
demo-admin/
├── public/            # 静态资源
├── src/
│   ├── api/          # API接口
│   │   ├── modules/   # 按模块分组的API
│   │   └── index.ts  # API配置
│   ├── assets/       # 资源文件
│   ├── components/   # 公共组件
│   ├── config/       # 配置文件
│   ├── directives/   # 自定义指令
│   ├── globals/      # 全局变量
│   ├── hooks/        # 组合式函数
│   ├── lang/         # 国际化
│   ├── layout/       # 布局组件
│   ├── mock/         # Mock数据
│   ├── router/       # 路由配置
│   ├── store/        # Pinia状态管理
│   ├── style/        # 样式文件
│   ├── typings/      # TypeScript类型定义
│   ├── utils/        # 工具函数
│   └── views/        # 页面组件
│       ├── about/
│       ├── component/
│       ├── dashboard/
│       ├── error/
│       ├── home/
│       ├── link/
│       ├── login/     # 登录注册
│       ├── monitor/   # 系统监控
│       ├── personal/  # 个人中心
│       ├── system/    # 系统管理
│       └── tool/      # 工具功能
├── .editorconfig     # 编辑器配置
├── .env.*            # 环境变量
├── eslint.config.js  # ESLint配置
├── package.json      # 项目依赖
├── tsconfig.json     # TypeScript配置
├── vite.config.ts    # Vite配置
└── README.md         # 项目说明
```

## 快速开始

### 环境要求

- **Node.js**: 18.0.0+
- **pnpm**: 8.0.0+

### 安装依赖

```bash
# 安装pnpm（如果未安装）
npm install -g pnpm

# 安装项目依赖
pnpm install
```

### 开发模式

```bash
# 启动开发服务器
pnpm dev
```

访问地址：http://localhost:5173

### 生产构建

```bash
# 构建生产版本
pnpm build:prod

# 预览构建结果
pnpm preview
```

### 代码检查

```bash
# ESLint检查并自动修复
pnpm lint:eslint

# Prettier格式化
pnpm lint:prettier

# 预提交检查
pnpm lint:lint-staged
```

## 开发指南

### 1. 添加新页面

1. 在 `src/views/` 对应模块下创建页面文件
2. 在路由配置中添加路由（后端控制）或静态路由
3. 在菜单管理中配置菜单权限

### 2. 添加新API

1. 在 `src/api/modules/` 对应模块下创建API文件
2. 导出API方法
3. 在页面中import并使用

### 3. 使用公共组件

项目提供了丰富的公共组件，位于 `src/components/` 下，按需引入使用。

### 4. 状态管理

使用 Pinia 进行状态管理，Store文件位于 `src/store/` 下。

```typescript
import { useUserInfoStore } from "@/store/modules/user-info";
const userStore = useUserInfoStore();
```

### 5. 路由守卫

路由守卫配置在 `src/router/index.ts`，实现了权限控制和登录状态检查。

## 环境变量

| 变量名                | 说明       | 默认值        |
| ------------------ | -------- | ---------- |
| VITE_APP_TITLE     | 应用标题     | Demo Admin |
| VITE_APP_BASE_API  | API基础路径  | /api       |
| VITE_APP_OPEN_MOCK | 是否开启Mock | false      |

## 代码规范

项目使用 ESLint + Prettier + Stylelint 进行代码规范检查：

- **ESLint**: JavaScript/TypeScript代码检查
- **Prettier**: 代码格式化
- **Stylelint**: CSS/SCSS代码检查

提交代码前会自动执行 `lint-staged` 检查，确保代码质量。

## 提交规范

项目使用 Commitlint 规范提交信息，格式为：

```
type(scope): description
```

- **type**: feat, fix, refactor, test, chore, docs, style, perf, build, ci, revert, wip, release, deps, merge, sync
- **scope**: auth, menu, gen, session, project, ui, web, nuxt 等
- **description**: 提交描述

## 浏览器支持

| Chrome | Edge | Firefox | Safari |
| ------ | ---- | ------- | ------ |
| 最新版    | 最新版  | 最新版     | 最新版    |

## 常见问题

### 1. 依赖安装失败

使用 pnpm 安装，如遇到网络问题，可尝试使用淘宝镜像：

```bash
pnpm config set registry https://registry.npmmirror.com
```

### 2. 开发服务器启动失败

确保端口 5173 未被占用，或修改 `vite.config.ts` 中的端口配置。

### 3. API请求失败

检查后端服务是否启动，确认 API 基础路径配置是否正确。

## 开源协议

MIT License

## 参考项目

- [SnowAdmin](https://github.com/WangFan-io/SnowAdmin) - 本项目基于此项目进行定制
- [Arco Design Vue](https://arco.design/) - 字节跳动企业级UI组件库
- [Vue 3](https://vuejs.org/) - 渐进式JavaScript框架
- [Vite](https://vitejs.dev/) - 下一代前端构建工具

## 联系方式

- 项目地址：[GitHub](https://github.com) / [Gitee](https://gitee.com)
- 问题反馈：[Issues](https://github.com/xxx/issues)

---

**让开发更快速，让项目更精彩！** 🚀
