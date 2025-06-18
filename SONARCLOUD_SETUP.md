# SonarCloud + GitHub Actions 配置说明

## 配置信息
- **组织键 (Organization Key)**: `sofia-hjq`
- **项目键 (Project Key)**: `Sofia-hjq_cloud`
- **SonarCloud Token**: `e1ff75412f36034de655a6f18efdeba1db144b23`

## GitHub Secrets 配置

请在GitHub仓库设置中添加以下Secrets：

1. 进入GitHub仓库 → Settings → Secrets and variables → Actions
2. 添加以下Repository secrets：

### 必需的Secrets：
- **SONAR_TOKEN**: `e1ff75412f36034de655a6f18efdeba1db144b23`

### 说明：
- `GITHUB_TOKEN` 是自动提供的，无需手动配置
- SonarCloud工作流会在以下情况下触发：
  - 推送到 `main`、`master` 或 `develop` 分支
  - 向 `main` 或 `master` 分支创建Pull Request

## 项目配置

### sonar-project.properties
项目配置文件已更新为：
```properties
sonar.organization=sofia-hjq
sonar.projectKey=Sofia-hjq_cloud
sonar.projectName=Moti Cloud Project
```

### 工作流文件
- 位置: `.github/workflows/sonarcloud.yml`
- 使用JDK 17 (兼容SonarCloud最新版本)
- 包含Maven依赖缓存
- 支持代码覆盖率分析

## 使用说明

1. 配置好GitHub Secrets后，每次推送代码到指定分支时会自动触发SonarCloud分析
2. 分析结果可在 [SonarCloud控制台](https://sonarcloud.io/projects) 查看
3. 项目地址：https://sonarcloud.io/project/overview?id=Sofia-hjq_cloud

## 注意事项

- 确保SonarCloud项目已正确创建并与GitHub仓库关联
- 如果是私有仓库，请确保SonarCloud有访问权限
- 建议在SonarCloud项目设置中配置质量门限和通知规则 