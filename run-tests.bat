@echo off
echo "======================================"
echo "正在运行完整的质量保证流程..."
echo "======================================"

REM 步骤1: 清理之前的构建
echo "步骤1: 清理项目..."
call mvn clean
if %errorlevel% neq 0 (
    echo "清理失败！"
    pause
    exit /b 1
)

REM 步骤2: 编译项目
echo "步骤2: 编译项目..."
call mvn compile test-compile
if %errorlevel% neq 0 (
    echo "编译失败！"
    pause
    exit /b 1
)

REM 步骤3: 运行测试并生成Jacoco报告
echo "步骤3: 运行单元测试..."
call mvn test
if %errorlevel% neq 0 (
    echo "测试失败！"
    pause
    exit /b 1
)

REM 步骤4: 生成覆盖率报告
echo "步骤4: 生成代码覆盖率报告..."
call mvn jacoco:report jacoco:report-aggregate
if %errorlevel% neq 0 (
    echo "覆盖率报告生成失败！"
    pause
    exit /b 1
)

REM 步骤5: 本地测试验证（不运行SonarCloud分析）
echo "步骤5: 验证测试和覆盖率..."
echo "注意：SonarCloud分析将在GitHub Actions中自动运行"

echo "======================================"
echo "✅ 本地测试流程完成！"
echo "======================================"
echo "📊 覆盖率报告: target/site/jacoco/index.html"
echo "🔍 SonarCloud: https://sonarcloud.io/project/overview?id=Sofia-hjq_cloud"
echo ""
echo "📌 提交代码到GitHub后，SonarCloud将自动分析"
echo "📌 查看GitHub Actions运行状态和质量门结果"
echo "======================================"
pause 