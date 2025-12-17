FxAdmin 快速开发脚手架
FxAdmin 是基于 SpringBoot 3.5.0 + Vue 3.5.15 构建的企业级后台管理快速开发脚手架，集成后端核心业务组件与前端丰富交互插件，帮助开发者快速落地业务系统，减少重复开发成本。
技术栈
后端技术
技术分类	具体组件 / 版本
核心框架	SpringBoot 3.5.0
权限安全	Spring Security + JWT（jjwt 0.12.5）
ORM 框架	MyBatis-Plus 3.5.8（SpringBoot3 适配版）
数据库连接池	Druid 1.2.23
缓存技术	Spring Data Redis
接口文档	Knife4j 4.5.0 + SpringDoc OpenAPI 2.8.4
工具类库	Hutool 5.8.38、FastJson 2.0.32、Commons Codec 1.15
数据库驱动	MySQL Connector/J（适配 MySQL 8.0+）
前端技术
基于 Vue 3 生态，集成多场景交互插件：
技术分类	具体组件 / 版本
核心框架	Vue 3.5.15、Vue Router 4.3.0
状态管理	Pinia 2.3.0 + pinia-plugin-persistedstate（状态持久化）
网络请求	Axios 1.6.8
代码编辑	Codemirror 6、vue-codemirror6（支持 JS/JSON/Vue 语法）
富文本编辑	@wangeditor/editor（v5） + @wangeditor/editor-for-vue
数据可视化	@visactor/vchart 1.11.0 + @visactor/vchart-arco-theme
交互工具	sortablejs/vuedraggable（拖拽）、vue-virtual-scroller（虚拟滚动）
多媒体工具	xgplayer（视频）、recorder-core（录音）、jsbarcode/qrcode（码生成）
通用工具	@vueuse/core 12.4.0、pinyin-pro（拼音处理）、uuid 11.1.0
核心功能
系统基础能力
RBAC 权限体系：用户 / 角色 / 菜单三级权限控制，结合 JWT 实现接口认证
系统配置管理：数据字典、系统参数、用户信息维护
数据库快捷操作：MyBatis-Plus 封装 CURD、分页、逻辑删除
缓存优化：Redis 缓存高频数据（用户、字典）
接口文档：Knife4j 自动生成接口文档，支持在线调试
前端增强能力
多类型编辑：代码编辑（多语法高亮）、富文本编辑、颜色选择
数据可视化：VChart 图表 + Arco 主题适配
高效交互：拖拽排序、虚拟滚动、操作引导（driver.js）
多媒体支持：视频播放、录音、条形码 / 二维码生成
国际化：vue-i18n 多语言切换（可扩展）
快速开始
环境准备
JDK 17+ | MySQL 8.0+ | Redis 5.0+
Node.js 16+（建议 18 LTS） | Maven 3.6+
后端部署
克隆项目
bash
运行
git clone https://github.com/你的用户名/fxadmin.git
cd fxadmin
初始化数据库
创建utf8mb4编码数据库，导入根目录init.sql（含用户 / 角色 / 菜单表）
配置修改（src/main/resources/application.yml）
yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/fxadmin?useSSL=false&serverTimezone=Asia/Shanghai
      username: root
      password: 你的密码
  data:
    redis:
      host: localhost
      port: 6379
启动项目
bash
运行
mvn spring-boot:run
前端部署
进入前端目录
bash
运行
cd fxadmin-frontend
安装依赖
bash
运行
pnpm install # 或npm/yarn
配置代理（vite.config.js）
javascript
运行
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      }
    }
  }
})
启动开发环境
bash
运行
pnpm dev
访问：http://localhost:5173，初始账号admin/123456
项目目录结构
后端核心结构
plaintext
com.fxly.demo
├── api/          # 接口层（Controller）
├── system/       # 系统模块（用户/角色/权限）
│   └── security/ # 安全认证（JWT/权限拦截）
├── service/      # 业务逻辑层
├── mapper/       # MyBatis-Plus Mapper
└── DemoApplication.java # 启动类
前端核心结构
plaintext
fxadmin-frontend
├── src/
│   ├── api/      # 接口请求封装
│   ├── components/ # 公共组件（编辑器/图表）
│   ├── pinia/    # 状态管理
│   ├── router/   # 路由配置
│   └── views/    # 页面视图
└── vite.config.js # 构建配置
License
基于 MIT License 开源，可自由修改、分发与商用。
