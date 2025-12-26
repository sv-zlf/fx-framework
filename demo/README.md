# Demo 后端服务

基于 Spring Boot 3 + MyBatis-Plus + Spring Security 构建的企业级后端服务。

## 技术栈

- **核心框架**：Spring Boot 3.5.0
- **持久层**：MyBatis-Plus 3.5.8
- **安全框架**：Spring Security 6.x
- **数据库**：MySQL 8.0+
- **缓存**：Redis 6.0+
- **API文档**：Knife4j (OpenAPI 3)
- **监控工具**：Druid
- **工具库**：Hutool、Lombok

## 项目结构

```
ndemo/
├── src/main/
│   ├── java/com/fxly/demo/
│   │   ├── api/                    # API层
│   │   │   ├── core/              # 核心业务
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── service/       # 服务接口
│   │   │   │   └── mapper/        # MyBatis Mapper
│   │   │   ├── dto/               # 数据传输对象
│   │   │   └── generate/         # 代码生成
│   │   ├── system/                # 系统层
│   │   │   ├── security/          # 安全相关
│   │   │   │   ├── handler/       # 认证处理器
│   │   │   │   ├── jwt/           # JWT工具
│   │   │   │   └── ...           
│   │   │   ├── annotation/       # 自定义注解
│   │   │   ├── aspect/           # 切面
│   │   │   ├── constant/         # 常量定义
│   │   │   ├── global/           # 全局配置
│   │   │   └── utils/            # 工具类
│   │   └── generate/               # 代码生成器
│   │       ├── config/            # 代码生成配置
│   │       ├── engine/            # 模板引擎
│   │       └── template/          # 代码模板
│   └── resources/
│       ├── mapper/                  # MyBatis XML映射
│       ├── application.yml          # 主配置文件
│       └── ...                       # 其他资源
└── pom.xml                          # Maven配置
```

## 核心功能

### 1. 认证授权

- **JWT Token认证**：基于Spring Security + JWT的无状态认证
- **RBAC权限模型**：用户-角色-菜单-权限四级权限体系
- **权限控制**：支持接口级权限和按钮级权限
- **登录登出**：完整的登录登出流程，自动记录操作日志
- **会话管理**：在线用户监控，支持强制退出

### 2. 系统管理

- **用户管理**：用户的增删改查、密码重置、状态管理
- **角色管理**：角色的增删改查、权限分配
- **菜单管理**：树形菜单结构、权限配置、图标配置
- **字典管理**：数据字典类型和字典项管理
- **部门管理**：组织架构管理（待完善）

### 3. 操作日志

- **自动记录**：通过AOP切面自动记录用户操作
- **登录日志**：登录成功、失败、登出自动记录
- **日志查询**：支持按模块、操作人、状态等条件查询
- **日志详情**：查看完整的请求参数和响应结果

### 4. 代码生成

- **数据库表导入**：支持从数据库导入表结构
- **配置灵活**：可配置字段类型、显示类型、查询方式等
- **多种模板**：支持单表、主子表等代码生成
- **一键生成**：生成Controller、Service、Mapper、Entity、Vue页面等

### 5. 系统监控

- **SQL监控**：集成Druid SQL监控
- **在线用户**：实时查看在线用户，支持强制退出
- **操作日志**：完整的用户操作审计

## 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 数据库配置

修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/fx_framework?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

### 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run

# 或打包后启动
mvn clean package
java -jar target/demo-1.0.0.jar
```

### 访问地址

- **应用地址**：http://localhost:8080
- **API文档**：http://localhost:8080/doc.html
- **Druid监控**：http://localhost:8080/druid

## 开发规范

### 1. 包结构规范

- `controller`：控制器层，处理HTTP请求
- `service`：业务逻辑层，实现具体业务
- `mapper`：数据访问层，操作数据库
- `entity`：实体类，对应数据库表
- `dto`：数据传输对象，用于接口交互

### 2. 命名规范

- **类名**：大驼峰命名，如 `UserController`
- **方法名**：小驼峰命名，如 `getUserList`
- **变量名**：小驼峰命名，如 `userName`
- **常量名**：全大写下划线分隔，如 `MAX_SIZE`

### 3. 注解使用

- `@RestController`：标记为REST控制器
- `@Service`：标记为服务层组件
- `@Resource`：依赖注入（JDK标准）
- `@LogOperation`：系统日志注解，自动记录操作
- `@Operation`：Swagger接口文档注解

### 4. 异常处理

统一使用 `GlobalException` 进行业务异常处理：

```java
if (user == null) {
    throw new GlobalException(404, "用户不存在");
}
```

### 5. 统一响应

所有接口统一返回 `HttpResult` 格式：

```java
// 成功
return HttpResult.success(data);

// 失败
return HttpResult.error(400, "参数错误");
```

## 核心模块说明

### 安全模块 (system.security)

负责系统的认证和授权：

- `SecurityConfig`：Spring Security配置
- `CustomUserDetailsService`：用户认证服务
- `JwtAuthenticationFilter`：JWT过滤器
- `LoginSuccessHandler`：登录成功处理
- `LoginFailureHandler`：登录失败处理
- `LogoutSuccessHandler`：登出处理

### 日志模块 (system.aspect)

负责系统操作日志的记录：

- `LogOperation`：日志注解
- `LogOperationAspect`：日志切面
- 日志记录内容：用户信息、IP地址、请求参数、响应结果等

### 代码生成模块 (generate)

负责代码生成功能：

- `CodeGenerator`：代码生成器主类
- `template/`：代码模板目录
- 支持生成：Controller、Service、Mapper、Entity、Vue页面

## 联系方式

- 项目地址：[GitHub]([sv-zlf/fx-framework](https://github.com/sv-zlf/fx-framework))
- 问题反馈：[Issues]([sv-zlf/fx-framework](https://github.com/sv-zlf/fx-framework/issues))

## 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Spring Security](https://spring.io/projects/spring-security)
- [Knife4j](https://doc.xiaominfo.com/)
