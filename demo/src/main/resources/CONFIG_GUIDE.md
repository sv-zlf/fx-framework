# 配置文件说明

## 环境配置
本项目使用Spring Profile机制实现多环境配置，支持以下环境：

- `dev`: 开发环境（默认）
- `test`: 测试环境
- `prod`: 生产环境

### 切换环境方式

1. **修改 application.yml**
   ```yaml
   spring:
     profiles:
       active: prod  # 修改为需要的环境
   ```

2. **命令行参数**
   ```bash
   # 开发环境
   java -jar demo.jar --spring.profiles.active=dev
   
   # 生产环境
   java -jar demo.jar --spring.profiles.active=prod
   ```

3. **环境变量**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   ```

4. **Maven启动**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Log4j2日志配置

### 配置文件

- `log4j2-spring.xml`: 开发、测试环境日志配置
- `log4j2-spring-prod.xml`: 生产环境专用日志配置（使用 `--spring.profiles.active=prod` 自动加载）
- `log4j2.component.properties`: Log4j2组件配置（异步日志相关）

### 日志归档策略

| 级别 | 文件名 | 归档目录 | 单文件大小 | 保留天数 | 备注 |
|------|--------|----------|------------|----------|------|
| ALL  | demo-all.log | logs/all/ | 100MB(200MB生产) | 30 | 所有级别日志 |
| ERROR| demo-error.log | logs/error/ | 50MB(100MB生产) | 90 | 仅ERROR |
| WARN | demo-warn.log | logs/warn/ | 50MB(100MB生产) | 60 | 仅WARN及以上 |
| INFO | demo-info.log | logs/info/ | 100MB(200MB生产) | 30 | 仅INFO及以上 |
| DEBUG| demo-debug.log | logs/debug/ | 50MB | 7 | 仅DEBUG及以上 |

### 异步日志特性

- **高性能**: 使用Disruptor无锁队列，支持高并发场景
- **低延迟**: Timeout等待策略，平衡延迟和CPU使用
- **RingBuffer大小**: 262144（2^18），可根据实际负载调整
- **生产环境**: 自动禁用location信息（调用位置），提升性能

### 日志级别配置

#### 开发环境 (dev)
- Root: INFO
- com.fxly.demo: DEBUG
- 控制台输出: 全部级别

#### 测试环境 (test)
- Root: INFO
- com.fxly.demo: INFO
- 控制台输出: 全部级别

#### 生产环境 (prod)
- Root: WARN
- com.fxly.demo: INFO
- 控制台输出: 仅ERROR
- Swagger/Knife4j: 禁用

## 生产环境敏感配置

<arg_value>  生产环境支持通过环境变量覆盖敏感配置：

| 配置项 | 环境变量 | 默认值 | 说明 |
|--------|----------|--------|------|
| DB_USERNAME | DB_USERNAME | prod_user | 数据库用户名 |
| DB_PASSWORD | DB_PASSWORD | - | 数据库密码（必填） |
| REDIS_PASSWORD | REDIS_PASSWORD | - | Redis密码 |
| JWT_SECRET | JWT_SECRET | - | JWT密钥（必填） |

### Docker/K8s部署示例

```yaml
# docker-compose.yml
version: "3"
services:
  demo:
    image: demo:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=prod_user
      - DB_PASSWORD=your_db_password
      - REDIS_PASSWORD=your_redis_password
      - JWT_SECRET=your_jwt_secret
    ports:
      - "8080:8080"
```

```yaml
# Kubernetes deployment
apiVersion: v1
kind: Secret
metadata:
  name: demo-secrets
type: Opaque
data:
  db-password: <base64-encoded>
  redis-password: <base64-encoded>
  jwt-secret: <base64-encoded>
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo
spec:
  template:
    spec:
      containers:
      - name: demo
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: demo-secrets
              key: db-password
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: demo-secrets
              key: redis-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: demo-secrets
              key: jwt-secret
```

## 功能验证

1. **多环境配置验证**
   ```bash
   # 启动开发环境
   mvn spring-boot:run
   
   # 启动生产环境
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

2. **日志输出验证**
   ```bash
   # 查看日志文件
   ls -l logs/
   tail -f logs/demo-all.log
   ```

3. **API文档验证**
   - 开发环境: http://localhost:8080/doc.html (Knife4j)
   - 生产环境: Swagger自动禁用

## 性能优化建议

1. **异步日志调优**
   - 如遇日志丢失，可增大 `RingBufferSize`
   - 高并发场景可使用 `AsyncLoggerContextSelector`

2. **数据库连接池**
   - 根据实际负载调整 `max-active`
   - 生产环境建议初始值与最小值相同

3. **日志级别调整**
   - 生产环境避免使用DEBUG级别
   - 第三方框架使用WARN或ERROR级别
