#!/bin/bash

echo "=== 检查 Java 文件导入 ==="

echo "1. 检查 MainActivity.java 导入:"
grep "^import" app/src/main/java/com/testuse/motofm/MainActivity.java
echo ""

echo "2. 检查 FMRadioClient.java 导入:"
grep "^import" app/src/main/java/com/testuse/motofm/FMRadioClient.java
echo ""

echo "3. 检查 AIDL 文件:"
find app/src/main/aidl -name "*.aidl" -exec echo "找到: {}" \;
echo ""

echo "4. 检查 Gradle 依赖:"
grep "implementation" app/build.gradle
echo ""

echo "5. 检查包名一致性:"
echo "AndroidManifest.xml 包名: $(grep 'package=' app/src/main/AndroidManifest.xml | head -1)"
echo "build.gradle namespace: $(grep 'namespace' app/build.gradle | head -1)"
echo "MainActivity 包名: $(grep '^package' app/src/main/java/com/testuse/motofm/MainActivity.java)"
