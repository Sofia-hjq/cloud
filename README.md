# 莫提网盘 (Moti Cloud)

一个基于Spring Cloud微服务架构的网盘系统，使用用户名+密码认证方式。

## 🏗️ 系统架构

```
moti-cloud/
├── moti-common/              # 公共模块
├── moti-eureka/             # Eureka注册中心
├── moti-gateway/            # 网关服务 (端口:8888)
├── moti-user/               # 用户服务 (端口:8801)
└── moti-file/               # 文件服务 (端口:8802)
```

## 🚀 技术栈

- **Spring Cloud Gateway** - API网关
- **Eureka** - 服务注册与发现
- **Spring Boot** - 微服务框架
- **MyBatis + MySQL** - 数据持久化
- **Thymeleaf** - 模板引擎
- **Bootstrap** - 前端UI框架

## ✨ 核心特性

✅ **用户系统**
- 用户名+密码注册登录
- 用户信息管理

✅ **文件管理**
- 本地文件系统存储
- 文件上传下载
- 文件分享功能

✅ **微服务架构**
- 服务注册与发现
- API网关路由
- 服务间通信

## 🎯 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0

### 启动步骤

1. **创建数据库**
```sql
CREATE DATABASE moti_cloud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
mysql -u root -p moti_cloud < moti-cloud.sql
```

2. **启动服务** (按顺序启动)
```bash
# 1. Eureka注册中心
cd moti-eureka && mvn spring-boot:run

# 2. 网关服务
cd moti-gateway && mvn spring-boot:run

# 3. 用户服务
cd moti-user && mvn spring-boot:run

# 4. 文件服务
cd moti-file && mvn spring-boot:run
```

3. **访问系统**
- 主页: http://localhost:8888/
- Eureka控制台: http://localhost:8761/

详细启动指南请参考 [start-services.md](start-services.md)

## 📁 项目结构

```
moti-cloud/
├── moti-common/                    # 公共模块
│   └── src/main/java/com/moti/common/
│       ├── entity/                # 公共实体类
│       └── utils/                 # 工具类
├── moti-eureka/                   # Eureka注册中心
│   └── src/main/java/com/moti/eureka/
├── moti-gateway/                  # 网关服务
│   └── src/main/java/com/moti/gateway/
├── moti-user/                     # 用户服务
│   └── src/main/java/com/moti/user/
│       ├── controller/           # 控制器
│       ├── service/              # 业务逻辑
│       └── mapper/               # 数据访问
└── moti-file/                     # 文件服务
    ├── src/main/java/com/moti/file/
    └── src/main/resources/
        ├── static/               # 静态资源
        └── templates/            # 页面模板
```

## 🔧 配置说明

### 数据库配置
修改各服务的 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/moti_cloud
    username: root
    password: your_password
```

### 服务端口
- Eureka注册中心: 8761
- 网关服务: 8888  
- 用户服务: 8801
- 文件服务: 8802

## 📊 功能特性

### 用户管理
- [x] 用户注册 (用户名+密码)
- [x] 用户登录
- [x] 用户信息查询和更新
- [x] 管理员功能

### 文件管理
- [x] 文件上传
- [x] 文件下载
- [x] 文件删除
- [x] 文件列表展示
- [x] 本地存储

### 系统架构
- [x] 微服务架构
- [x] 服务注册发现 (Eureka)
- [x] API网关路由
- [x] 负载均衡
- [x] 服务间通信 (OpenFeign)

## 🔗 API接口

### 用户服务
```
POST /user/register     # 用户注册
POST /user/login        # 用户登录
GET  /user/info/{id}    # 获取用户信息
PUT  /user/update       # 更新用户信息
```

### 文件服务
```
GET  /                  # 首页
POST /upload            # 文件上传
GET  /download/{id}     # 文件下载
DELETE /delete/{id}     # 文件删除
```

## 🛠️ 开发指南

### 添加新服务
1. 创建新的Maven模块
2. 继承父pom依赖管理
3. 添加Eureka客户端依赖
4. 在网关中配置路由规则

### 服务间调用
使用OpenFeign进行服务间通信：
```java
@FeignClient("moti-user")
public interface UserServiceClient {
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Integer id);
}
```

## 📝 版本历史

- **v1.0.0** - 基础功能实现
  - 用户注册登录
  - 文件上传下载
  - 微服务架构搭建

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目基于 [MIT License](LICENSE.txt) 开源。

## 👥 作者

- **moti** - 初始工作

---

**注意**: 这是一个学习项目，适用于Spring Cloud微服务架构的学习和实践。
