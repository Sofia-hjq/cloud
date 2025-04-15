# 莫提网盘 Docker 部署指南

本文档提供了使用 Docker Compose 部署莫提网盘的详细步骤。

## 环境要求

- Docker Engine
- Docker Compose
- 4GB+ 内存
- 10GB+ 硬盘空间

## 部署步骤

### 1. 下载项目代码

```bash
git clone https://github.com/373675032/moti-cloud.git
cd moti-cloud
```

### 2. 配置环境

修改 `docker-compose.yml` 文件中的环境变量：

- MySQL 密码：修改 `MYSQL_ROOT_PASSWORD` 和 `SPRING_DATASOURCE_PASSWORD` 为相同的密码
- FTP 配置：修改 `FTP_USER_NAME`、`FTP_USER_PASS`、`FTP_USERNAME`、`FTP_PASSWORD` 为相同的值
- QQ邮箱配置：修改 `MAIL_USERNAME` 和 `MAIL_PASSWORD` 为您的QQ邮箱和授权码

### 3. 启动服务

```bash
docker-compose up -d
```

启动过程可能需要几分钟时间，请耐心等待。

### 4. 访问网盘

浏览器访问：`http://localhost:8080/moti-cloud`

### 5. 默认管理员账号

- 邮箱：admin@example.com
- 密码：admin123

## 服务说明

- MySQL 数据库：存储用户和文件元数据
- FTP 服务器：存储上传的文件
- Moti-Cloud 应用：提供网盘功能的 Web 应用

## 数据持久化

- MySQL 数据存储在 `./mysql-data` 目录
- FTP 文件存储在 `./ftp-data` 目录

## 常见问题

### 1. 如何修改默认端口？

在 `docker-compose.yml` 文件中修改对应服务的端口映射。

### 2. 如何备份数据？

```bash
# 备份 MySQL 数据
docker exec moti-mysql mysqldump -u root -p moti-cloud > backup.sql

# 备份 FTP 文件
cp -r ftp-data backup-ftp
```

### 3. 如何增加存储空间上限？

编辑 `init.sql` 文件中的 `max_size` 值，然后重新部署。

### 4. 如何重置所有数据？

```bash
docker-compose down
rm -rf mysql-data ftp-data
docker-compose up -d
```

## 注意事项

- 本部署方案适用于个人或小团队使用
- 对于生产环境，建议增加 HTTPS、备份策略等安全措施
- 修改默认管理员密码，保障系统安全 