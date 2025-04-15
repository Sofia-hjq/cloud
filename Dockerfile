FROM maven:3.6-jdk-8 AS builder

WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-slim

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y bash netcat-traditional default-mysql-client && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 添加环境变量处理脚本
COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"] 