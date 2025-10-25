#!/bin/bash

echo "=== 诊断依赖下载问题 ==="

echo "1. 检查网络连接到 Maven Central:"
ping -c 2 repo1.maven.org

echo ""
echo "2. 测试直接下载 Shizuku POM 文件:"
curl -I https://repo1.maven.org/maven2/dev/rikka/shizuku/api/13.1.5/api-13.1.5.pom

echo ""
echo "3. 检查 Gradle 包装器版本:"
./gradlew --version

echo ""
echo "4. 尝试下载依赖（显示详细日志）:"
./gradlew --refresh-dependencies --info 2>&1 | grep -i "shizuku\|download" | head -20

echo ""
echo "5. 检查项目 Gradle 配置:"
cat gradle/wrapper/gradle-wrapper.properties
