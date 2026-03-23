@echo off
REM ========================================
REM   Native Image Builder for Windows
REM   必須在 x64 Native Tools Command Prompt 中運行
REM ========================================

echo.
echo ========================================
echo   GraalVM Native Image Builder
echo   Windows Edition
echo ========================================
echo.

REM 檢查是否在正確的環境中
where cl >nul 2>&1
if %errorlevel% neq 0 (
    echo [錯誤] 未檢測到 Visual C++ 編譯器！
    echo.
    echo 請在以下環境中運行此腳本：
    echo   "x64 Native Tools Command Prompt for VS"
    echo.
    echo 步驟：
    echo   1. 打開開始選單
    echo   2. 搜尋 "x64 Native Tools Command Prompt"
    echo   3. 以系統管理員身份執行
    echo   4. cd 到專案目錄
    echo   5. 執行 build-native-image.bat
    echo.
    pause
    exit /b 1
)

echo [OK] Visual C++ 編譯器已就緒
echo.

REM 切換到專案目錄
cd /d D:\Projects\jpeg2pdf-ofd-nospring
echo [OK] 專案目錄: %cd%
echo.

REM 設置 GraalVM 環境
echo [1/3] 設置 GraalVM 環境...
set JAVA_HOME=C:\graalvm\graalvm-community-openjdk-17.0.9+9.1
set Path=%JAVA_HOME%\bin;%Path%
echo [OK] JAVA_HOME: %JAVA_HOME%
echo.

REM 驗證 Java 版本
echo [2/3] 驗證 GraalVM...
java -version
echo.

REM 驗證 Native Image
if not exist "%JAVA_HOME%\lib\svm\bin\native-image.exe" (
    echo [錯誤] Native Image 未安裝
    echo.
    echo 請執行: gu install native-image
    echo.
    pause
    exit /b 1
)

echo [OK] Native Image 已就緒
echo.

REM 顯示警告
echo ========================================
echo   重要提醒
echo ========================================
echo.
echo ⚠️  Native Image 編譯過程會消耗大量 CPU 和記憶體
echo ⚠️  編譯時間約 5-10 分鐘
echo ⚠️  請耐心等待 BUILD SUCCESS 的出現
echo ⚠️  建議至少 8GB 可用記憶體
echo.
pause

REM 開始構建
echo.
echo ========================================
echo   開始編譯 Native Image
echo ========================================
echo.
echo [3/3] 執行 Maven 構建...
echo.

mvn clean package

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   編譯完成！
    echo ========================================
    echo.
    
    if exist "target\jpeg2pdf-ofd-native.exe" (
        echo [成功] Native Image 已生成
        echo 位置: target\jpeg2pdf-ofd-native.exe
        
        REM 計算大小
        for %%F in ("target\jpeg2pdf-ofd-native.exe") do (
            set size=%%~zF
            set /a sizeMB=!size! / 1048576
            echo 大小: !sizeMB! MB
        )
        
        echo.
        echo ========================================
        echo   使用方法
        echo ========================================
        echo.
        echo cd target
        echo jpeg2pdf-ofd-native.exe -i image.jpg -o output/
        echo.
        echo 或查看幫助:
        echo jpeg2pdf-ofd-native.exe --help
        echo.
    ) else (
        echo [警告] 執行檔未找到
        echo 請檢查構建日誌
    )
) else (
    echo.
    echo ========================================
    echo   編譯失敗
    echo ========================================
    echo.
    echo 請檢查錯誤訊息並修復問題
    echo.
)

echo.
pause
