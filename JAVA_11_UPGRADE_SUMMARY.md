# Java 11 升级总结

## 主要更改

### 1. 根 pom.xml 文件更改
- **Java 版本**: 从 1.8 升级到 11
- **Maven 编译器插件**: 更新 source 和 target 从 1.8 到 11
- **Spring Boot 版本**: 从 2.2.4.RELEASE 升级到 2.7.18
- **Spring Cloud 版本**: 从 Hoxton.SR1 升级到 2021.0.8

### 2. 依赖版本更新
- **MyBatis**: 从 2.1.1 升级到 2.3.1
- **Druid**: 从 1.1.20 升级到 1.2.20
- **Lombok**: 从 1.18.20 升级到 1.18.30
- **JUnit 5**: 从 5.9.3 升级到 5.10.1
- **Mockito**: 从 5.1.1 升级到 5.8.0
- **TestContainers**: 保持 1.19.3

### 3. 新增配置属性
- `maven.compiler.source`: 11
- `maven.compiler.target`: 11

## 升级的原因

1. **解决 Mockito 兼容性问题**: Mockito 5.x 需要 Java 11 或更高版本
2. **获得更好的性能**: Java 11 相比 Java 8 有性能提升
3. **长期支持**: Java 11 是 LTS 版本，支持到 2026 年

## 兼容性检查

- ✅ Spring Boot 2.7.18 完全支持 Java 11
- ✅ Spring Cloud 2021.0.8 完全支持 Java 11
- ✅ MyBatis 2.3.1 完全支持 Java 11
- ✅ 所有测试框架都支持 Java 11

## 后续步骤

1. 确保开发环境和 CI/CD 环境都使用 Java 11
2. 运行完整的测试套件验证升级成功
3. 更新部署文档中的 Java 版本要求

## 注意事项

- 确保所有开发者本地环境都升级到 Java 11
- 检查 Docker 镜像是否使用正确的 Java 11 基础镜像
- 验证所有第三方库都与 Java 11 兼容 