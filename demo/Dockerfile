# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

LABEL maintainer="fx-framework <admin@example.com>"
LABEL description="Fx-Framework Backend Service"

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

# 复制Maven构建产物
COPY target/*.jar /app/app.jar

# 设置JVM参数
# -Xms: 初始堆大小
# -Xmx: 最大堆大小
# -XX:+UseG1GC: 使用G1垃圾回收器
# -XX:MaxGCPauseMillis=200: 最大GC停顿时间
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
