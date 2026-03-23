# 🚀 Native Image 手動構建指南

## ⚠️ 重要提醒

**Native Image 構建必須在特定的命令行環境中進行！**

---

## 📋 構建步驟

### 步驟 1：打開正確的命令行環境

1. 按 `Win` 鍵打開開始選單
2. 搜尋：**`x64 Native Tools Command Prompt for VS 2022`**
   - 或 `x64 Native Tools Command Prompt for VS 2019`
   - 或 `Native Tools Command Prompt for VS`
3. **右鍵 → 以系統管理員身份執行** ⚠️

### 步驟 2：切換到專案目錄

```cmd
cd D:\Projects\jpeg2pdf-ofd-nospring
```

### 步驟 3：切換到 native-image 分支

```cmd
git checkout native-image
```

### 步驟 4：設置 GraalVM 環境

```cmd
set JAVA_HOME=C:\graalvm\graalvm-community-openjdk-17.0.9+9.1
set Path=%JAVA_HOME%\bin;%Path%
```

### 步驟 5：驗證環境

```cmd
java -version
native-image --version
```

**應該看到：**
```
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment GraalVM CE 17.0.9+9.1
...
```

### 步驟 6：開始構建

```cmd
mvn clean package
```

---

## ⏱️ 預期時間

- **編譯時間：** 5-10 分鐘
- **記憶體需求：** 8GB+ 建議
- **CPU 使用率：** 會飆升到 100%（這是正常的）

---

## ✅ 成功標誌

構建成功後會看到：

```
[INFO] BUILD SUCCESS
```

生成的檔案：
```
target\jpeg2pdf-ofd-native.exe
```

大小預計：**30-50 MB**

---

## 🧪 測試

```cmd
cd target
jpeg2pdf-ofd-native.exe --help
```

---

## 📝 完整命令（複製貼上）

```cmd
REM 打開 x64 Native Tools Command Prompt for VS 後執行：

cd D:\Projects\jpeg2pdf-ofd-nospring
git checkout native-image
set JAVA_HOME=C:\graalvm\graalvm-community-openjdk-17.0.9+9.1
set Path=%JAVA_HOME%\bin;%Path%
java -version
mvn clean package
```

---

## ⚠️ 如果找不到 x64 Native Tools Command Prompt

### 方法 1：安裝 Visual Studio Build Tools

1. 下載：https://visualstudio.microsoft.com/zh-hans/downloads/
2. 選擇 **Visual Studio 2022 Build Tools**
3. 安裝時勾選：
   - ✅ **使用 C++ 的桌面開發**
   - ✅ **Windows 10/11 SDK**
4. 安裝完成後重新啟動電腦

### 方法 2：使用現成的 jpackage 版本

如果 Native Image 構建失敗，可以使用已經測試成功的版本：

**jpackage 版本（無需 Java）：**
```
位置：D:\Projects\jpeg2pdf-ofd-nospring\dist-jpackage\JPEG2PDF-OFD-NoSpring\
大小：181 MB
狀態：✅ 已測試成功
```

**JAR 版本（需要 Java）：**
```
位置：D:\Projects\jpeg2pdf-ofd-nospring\dist\jpeg2pdf-ofd-nospring.jar
大小：52 MB
狀態：✅ 已測試成功
```

---

## 📊 版本對比

| 版本 | 大小 | Java | 單文件 | 狀態 |
|------|------|------|--------|------|
| **Native Image** | ~30-50 MB | ❌ 否 | ✅ 是 | ⏳ 待構建 |
| **jpackage** | 181 MB | ❌ 否 | ❌ 資料夾 | ✅ 已測試 |
| **JAR** | 52 MB | ✅ 是 | ✅ 是 | ✅ 已測試 |

---

## 🆘 常見問題

### Q1: 找不到 cl.exe 編譯器

**A:** 確保在 **x64 Native Tools Command Prompt** 中運行，不是普通的 cmd 或 PowerShell

### Q2: JAVA_HOME 錯誤

**A:** 確保路徑正確：
```cmd
dir C:\graalvm\graalvm-community-openjdk-17.0.9+9.1\bin\java.exe
```

### Q3: Native Image 組件未安裝

**A:** 安裝 Native Image：
```cmd
gu install native-image
```

### Q4: 記憶體不足

**A:** 關閉其他應用程式，確保有 8GB+ 可用記憶體

---

## 🎯 推薦路徑

**第一次嘗試：** 使用 **jpackage 版本（181 MB）**
- ✅ 無需 Java
- ✅ 已測試成功
- ✅ 立即可用

**進階嘗試：** 構建 **Native Image（30-50 MB）**
- ⏳ 需要正確環境
- ⏳ 需要等待 5-10 分鐘
- ✅ 如果成功，最小的單文件 EXE

---

**祝構建順利！** 🚀
