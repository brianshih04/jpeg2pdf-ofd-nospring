#!/bin/bash

# ========================================
#   Native Image Builder for macOS
#   適用於 Intel x64 和 Apple Silicon (ARM64)
# ========================================

echo ""
echo "========================================"
echo "  GraalVM Native Image Builder"
echo "  macOS Edition"
echo "========================================"
echo ""

# 檢查 Xcode Command Line Tools
if ! command -v clang &> /dev/null; then
    echo "[錯誤] Xcode Command Line Tools 未安裝"
    echo ""
    echo "請執行: xcode-select --install"
    echo ""
    exit 1
fi

echo "[OK] Clang 編譯器已就緒"
echo ""

# 檢查 GraalVM
if [ -z "$JAVA_HOME" ]; then
    echo "[錯誤] JAVA_HOME 未設定"
    echo ""
    echo "請設定 JAVA_HOME 環境變數指向 GraalVM 目錄"
    echo ""
    echo "範例（加入到 ~/.zshrc 或 ~/.bash_profile）："
    echo "  export JAVA_HOME=/path/to/graalvm"
    echo "  export PATH=\$JAVA_HOME/bin:\$PATH"
    echo ""
    exit 1
fi

echo "[OK] JAVA_HOME: $JAVA_HOME"
echo ""

# 檢查 Native Image
NATIVE_IMAGE="$JAVA_HOME/lib/svm/bin/native-image"
if [ ! -f "$NATIVE_IMAGE" ]; then
    echo "[錯誤] Native Image 未安裝"
    echo ""
    echo "請執行: gu install native-image"
    echo ""
    exit 1
fi

echo "[OK] Native Image: $NATIVE_IMAGE"
echo ""

# 檢測架構
ARCH=$(uname -m)
echo "[INFO] 系統架構: $ARCH"
echo ""

# 顯示警告
echo "========================================"
echo "  重要提醒"
echo "========================================"
echo ""
echo "⚠️  Native Image 編譯過程會消耗大量 CPU 和記憶體"
echo "⚠️  編譯時間約 5-10 分鐘，請耐心等待"
echo "⚠️  建議至少 8GB 可用記憶體"
echo ""
read -p "是否繼續？ (y/n): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "已取消"
    exit 0
fi

# 開始編譯
echo ""
echo "========================================"
echo "  開始編譯 Native Image"
echo "========================================"
echo ""

mvn clean package

if [ $? -ne 0 ]; then
    echo ""
    echo "[錯誤] Maven 編譯失敗"
    echo ""
    echo "請檢查錯誤訊息並修復問題"
    echo ""
    exit 1
fi

echo ""
echo "========================================"
echo "  編譯完成！"
echo "========================================"
echo ""

# 檢查輸出
if [ -f "target/jpeg2pdf-ofd-native" ]; then
    echo "輸出位置: target/jpeg2pdf-ofd-native"
    
    # 計算大小
    SIZE=$(stat -f%z "target/jpeg2pdf-ofd-native")
    SIZEMB=$((SIZE / 1048576))
    echo "檔案大小: $SIZEMB MB"
    
    # 設定執行權限
    chmod +x "target/jpeg2pdf-ofd-native"
    echo "已設定執行權限"
else
    echo "[警告] 執行檔未找到"
fi

echo ""
echo "========================================"
echo "  使用方法"
echo "========================================"
echo ""
echo "cd target"
echo "./jpeg2pdf-ofd-native -i image.jpg -o output/"
echo ""
echo "./jpeg2pdf-ofd-native --help"
echo ""
