#!/bin/bash
set -e

# 等待MySQL服务启动
echo "等待MySQL服务启动..."
while ! mysqladmin ping -h mysql -u root -pmotipassword --silent; do
  sleep 1
done
echo "MySQL服务已启动"

# 等待FTP服务启动
echo "等待FTP服务启动..."
while ! nc -z ftp 21; do
  sleep 1
done
echo "FTP服务已启动"

# 替换配置文件中的环境变量
JAVA_OPTS="-Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
           -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
           -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
           -Dcom.moti.utils.FtpUtil.HOST=${FTP_HOST} \
           -Dcom.moti.utils.FtpUtil.PORT=${FTP_PORT} \
           -Dcom.moti.utils.FtpUtil.USERNAME=${FTP_USERNAME} \
           -Dcom.moti.utils.FtpUtil.PASSWORD=${FTP_PASSWORD} \
           -Dspring.mail.username=${MAIL_USERNAME} \
           -Dspring.mail.password=${MAIL_PASSWORD} \
           -Dcom.moti.utils.MailUtils.from=${MAIL_USERNAME}"

# 启动应用
exec java ${JAVA_OPTS} -jar app.jar 