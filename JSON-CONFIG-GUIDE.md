# JSON 配置文件完整指南

## 📋 目錄

- [完整配置結構](#完整配置結構)
- [input 配置](#input-配置)
- [output 配置](#output-配置)
- [ocr 配置](#ocr-配置)
- [font 配置](#font-配置)
- [完整示例](#完整示例)
- [注意事項](#注意事項)

---

## 完整配置結構

```json
{
  "input": {
    "folder": "C:/OCR/Input",
    "pattern": "*.jpg",
    "extensions": ["jpg", "jpeg", "png"]
  },
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf", "ofd", "txt"],
    "multiPage": false
  },
  "ocr": {
    "language": "chinese_cht",
    "useGpu": false,
    "cpuThreads": 4
  },
  "font": {
    "path": "C:/Windows/Fonts/msyh.ttc"
  }
}
```

---

## input 配置（輸入設置）

### 參數說明

| 參數 | 類型 | 必填 | 默認值 | 說明 |
|------|------|------|--------|------|
| `folder` | String | ✅ | - | 輸入圖片所在的資料夾路徑 |
| `file` | String | ⚪ | - | 單個圖片文件路徑（與 `folder` 二選一） |
| `pattern` | String | ⚪ | `*.jpg` | 文件過濾模式（支持 `*.jpg`, `*.png`, `*.*`） |
| `extensions` | Array | ⚪ | `["jpg", "jpeg", "png"]` | 支持的圖片擴展名列表 |

### 配置示例

#### 示例 1：處理整個資料夾

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.jpg"
  }
}
```

**說明：**
- 處理 `C:/OCR/Watch/` 資料夾下所有 `.jpg` 文件
- 不包括 `.jpeg`, `.png` 等其他格式

---

#### 示例 2：處理單個文件

```json
{
  "input": {
    "file": "C:/Documents/scan001.jpg"
  }
}
```

**說明：**
- 只處理指定的單個文件
- `file` 和 `folder` 不能同時使用

---

#### 示例 3：支持多種格式

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "extensions": ["jpg", "jpeg", "png", "bmp", "gif"]
  }
}
```

**說明：**
- 處理多種圖片格式
- 包括 `.jpg`, `.jpeg`, `.png`, `.bmp`, `.gif`

---

#### 示例 4：處理所有文件

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.*"
  }
}
```

**說明：**
- 處理資料夾下所有文件
- 程序會自動過濾非圖片文件

---

## output 配置（輸出設置）

### 參數說明

| 參數 | 類型 | 必填 | 默認值 | 說明 |
|------|------|------|--------|------|
| `folder` | String | ✅ | - | 輸出文件存放的資料夾路徑 |
| `formats` | Array | ⚪ | `["pdf"]` | 輸出格式列表 |
| `multiPage` | Boolean | ⚪ | `false` | 是否合併為多頁文檔 |

### formats 可選值

| 值 | 說明 | 輸出文件 |
|----|------|---------|
| `"pdf"` | 只生成 PDF | `output.pdf` |
| `"ofd"` | 只生成 OFD（中國國家標準格式） | `output.ofd` |
| `"txt"` | 只生成 TXT（純文字） | `output.txt` |
| `["pdf", "ofd"]` | 生成 PDF 和 OFD | `output.pdf`, `output.ofd` |
| `["pdf", "ofd", "txt"]` | 生成所有格式 | `output.pdf`, `output.ofd`, `output.txt` |

### 配置示例

#### 示例 1：單頁模式（默認）

```json
{
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf", "ofd", "txt"],
    "multiPage": false
  }
}
```

**輸出：**
```
image1_20260323_130000.pdf
image1_20260323_130000.ofd
image1_20260323_130000.txt

image2_20260323_130001.pdf
image2_20260323_130001.ofd
image2_20260323_130001.txt

image3_20260323_130002.pdf
image3_20260323_130002.ofd
image3_20260323_130002.txt
```

**適用場景：**
- 每個圖片需要單獨處理
- 靈活的文件管理
- 可以選擇性處理單個文件

---

#### 示例 2：多頁模式（合併所有圖片）

```json
{
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf", "ofd", "txt"],
    "multiPage": true
  }
}
```

**輸出：**
```
multipage_20260323_130000.pdf  (包含所有頁面)
multipage_20260323_130000.ofd  (包含所有頁面)
multipage_20260323_130000.txt  (包含所有文字)
```

**適用場景：**
- 處理掃描文檔（多頁文檔）
- 批量報告生成
- 文檔歸檔

---

#### 示例 3：只生成 PDF

```json
{
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf"],
    "multiPage": false
  }
}
```

**說明：**
- 只生成 PDF 文件
- 不生成 OFD 和 TXT

---

#### 示例 4：PDF + OFD（無 TXT）

```json
{
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf", "ofd"],
    "multiPage": true
  }
}
```

**說明：**
- 生成 PDF 和 OFD 兩種格式
- 不生成 TXT 文件

---

## ocr 配置（OCR 引擎設置）

### 參數說明

| 參數 | 類型 | 必填 | 默認值 | 說明 |
|------|------|------|--------|------|
| `language` | String | ⚪ | `chinese_cht` | OCR 識別語言 |
| `useGpu` | Boolean | ⚪ | `false` | 是否使用 GPU 加速 |
| `cpuThreads` | Integer | ⚪ | `4` | CPU 線程數 |

### 支持語言（80+ 種）

| 語言代碼 | 語言名稱 | 語言代碼 | 語言名稱 |
|---------|---------|---------|---------|
| `chinese_cht` | 繁體中文（默認） | `ch` | 簡體中文 |
| `en` | 英文 | `japan` | 日文 |
| `korean` | 韓文 | `french` | 法文 |
| `german` | 德文 | `spanish` | 西班牙文 |
| `portuguese` | 葡萄牙文 | `russian` | 俄文 |
| `arabic` | 阿拉伯文 | `hindi` | 印地文 |
| `thai` | 泰文 | `vietnamese` | 越南文 |
| `italian` | 意大利文 | `dutch` | 荷蘭文 |

**提示：** `chinese_cht` 可以識別繁體中文 + 英文混合文檔

### 配置示例

#### 示例 1：英文文檔

```json
{
  "ocr": {
    "language": "en"
  }
}
```

**適用場景：**
- 純英文文檔
- 英文表單、合同

---

#### 示例 2：簡體中文

```json
{
  "ocr": {
    "language": "ch"
  }
}
```

**適用場景：**
- 簡體中文文檔
- 中國大陸文件

---

#### 示例 3：日文文檔

```json
{
  "ocr": {
    "language": "japan",
    "cpuThreads": 6
  }
}
```

**適用場景：**
- 日文文檔
- 日本進口文件

---

#### 示例 4：高性能配置（有 GPU）

```json
{
  "ocr": {
    "language": "en",
    "useGpu": true,
    "cpuThreads": 8
  }
}
```

**適用場景：**
- 高性能機器（有 GPU）
- 大批量處理
- 需要快速處理

---

## font 配置（字體設置）

### 參數說明

| 參數 | 類型 | 必填 | 默認值 | 說明 |
|------|------|------|--------|------|
| `path` | String | ⚪ | 系統默認 | TrueType 字體文件路徑 |

### 默認字體（按優先級自動選擇）

| 系統 | 字體路徑 | 字體名稱 |
|------|---------|---------|
| Windows | `C:/Windows/Fonts/msjh.ttc` | 微軟正黑體 |
| Windows | `C:/Windows/Fonts/msyh.ttc` | 微軟雅黑 |
| Windows | `C:/Windows/Fonts/simhei.ttf` | 黑體 |
| macOS | `/System/Library/Fonts/PingFang.ttc` | 蘋方字體 |
| Linux | `/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc` | 文泉驛 |

### 配置示例

#### 示例 1：使用 Arial 字體

```json
{
  "font": {
    "path": "C:/Windows/Fonts/arial.ttf"
  }
}
```

**說明：**
- 使用 Arial 字體
- 適合英文文檔

---

#### 示例 2：使用系統默認（不指定）

```json
{
  // 不需要 font 配置，會自動選擇系統字體
}
```

**說明：**
- 程序會自動選擇合適的字體
- 推薦使用此方式

---

## 完整示例

### 示例 1：處理繁體中文文檔（多頁）

```json
{
  "input": {
    "folder": "C:/Documents/Chinese",
    "pattern": "*.jpg"
  },
  "output": {
    "folder": "C:/Output/Chinese",
    "formats": ["pdf", "ofd", "txt"],
    "multiPage": true
  },
  "ocr": {
    "language": "chinese_cht",
    "cpuThreads": 4
  }
}
```

**適用場景：**
- ✅ 處理繁體中文文檔
- ✅ 合併多頁為一個文件
- ✅ 生成 PDF + OFD + TXT 三種格式

**預期輸出：**
```
C:/Output/Chinese/
  multipage_20260323_130000.pdf
  multipage_20260323_130000.ofd
  multipage_20260323_130000.txt
```

---

### 示例 2：處理英文文檔（單頁）

```json
{
  "input": {
    "folder": "C:/Documents/English",
    "extensions": ["jpg", "jpeg", "png"]
  },
  "output": {
    "folder": "C:/Output/English",
    "formats": ["pdf", "txt"],
    "multiPage": false
  },
  "ocr": {
    "language": "en"
  }
}
```

**適用場景：**
- ✅ 處理英文文檔
- ✅ 每個圖片生成獨立的 PDF + TXT
- ✅ 支持多種圖片格式

**預期輸出：**
```
C:/Output/English/
  doc1_20260323_130000.pdf
  doc1_20260323_130000.txt
  doc2_20260323_130001.pdf
  doc2_20260323_130001.txt
```

---

### 示例 3：處理混合語言文檔

```json
{
  "input": {
    "folder": "C:/Documents/Mixed",
    "pattern": "*.*"
  },
  "output": {
    "folder": "C:/Output/Mixed",
    "formats": ["pdf", "ofd"],
    "multiPage": true
  },
  "ocr": {
    "language": "chinese_cht",
    "cpuThreads": 8
  },
  "font": {
    "path": "C:/Windows/Fonts/msyh.ttc"
  }
}
```

**適用場景：**
- ✅ 繁體中文 + 英文混合文檔
- ✅ 只生成 PDF + OFD（無 TXT）
- ✅ 自定義字體

**預期輸出：**
```
C:/Output/Mixed/
  multipage_20260323_130000.pdf
  multipage_20260323_130000.ofd
```

---

### 示例 4：高性能配置

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.*"
  },
  "output": {
    "folder": "C:/OCR/Output",
    "formats": ["pdf", "ofd"],
    "multiPage": true
  },
  "ocr": {
    "language": "en",
    "useGpu": true,
    "cpuThreads": 16
  },
  "font": {
    "path": "C:/Windows/Fonts/arial.ttf"
  }
}
```

**適用場景：**
- ✅ 高性能機器（有 GPU）
- ✅ 大批量處理
- ✅ 使用 GPU 加速

---

### 示例 5：處理單個文件

```json
{
  "input": {
    "file": "C:/Documents/contract.jpg"
  },
  "output": {
    "folder": "C:/Output",
    "formats": ["pdf", "ofd", "txt"],
    "multiPage": false
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

**適用場景：**
- ✅ 只處理單個文件
- ✅ 生成所有格式

**預期輸出：**
```
C:/Output/
  contract_20260323_130000.pdf
  contract_20260323_130000.ofd
  contract_20260323_130000.txt
```

---

## 注意事項

### 1. 路徑格式

```json
// ✅ 推薦：正斜線（跨平台兼容）
{
  "input": {
    "folder": "C:/OCR/Watch"
  }
}

// ⚠️ 可用：雙反斜線（僅 Windows）
{
  "input": {
    "folder": "C:\\OCR\\Watch"
  }
}

// ❌ 錯誤：單反斜線（會轉義錯誤）
{
  "input": {
    "folder": "C:\OCR\Watch"  // ❌ 錯誤！
  }
}
```

### 2. JSON 語法

```json
// ✅ 正確示例
{
  "output": {
    "formats": ["pdf", "ofd"],  // 最後一項無逗號
    "multiPage": true            // 最後一個屬性無逗號
  }
}

// ❌ 錯誤示例
{
  "output": {
    "formats": ["pdf", "ofd",],  // ❌ 數組最後多逗號
    "multiPage": true,           // ❌ 對象最後多逗號
  }
}
```

### 3. 編碼問題

```
✅ 推薦：UTF-8 編碼保存配置文件
⚠️ 避免：路徑中包含中文字符（部分系統可能不支持）
⚠️ 避免：路徑中包含特殊字符（空格、符號等）
```

### 4. 性能優化建議

```
圖片大小建議配置：
- 小圖片（< 2MP）:  -Xmx1G, cpuThreads: 2
- 中等圖片（2-5MP）: -Xmx2G, cpuThreads: 4
- 大圖片（> 5MP）:   -Xmx4G, cpuThreads: 8
- 批量處理:         -Xmx4G, multiPage: true
```

### 5. 常見問題

#### Q1: 沒有生成 OFD 或 TXT 文件？

**可能原因：**
- 字體加載失敗
- 沒有包含在 `formats` 中

**解決方法：**
```json
{
  "output": {
    "formats": ["pdf", "ofd", "txt"]  // 確保包含所需格式
  },
  "font": {
    "path": "C:/Windows/Fonts/arial.ttf"  // 嘗試使用其他字體
  }
}
```

#### Q2: OCR 識別準確度低？

**可能原因：**
- 語言設置不正確
- 圖片質量差

**解決方法：**
```json
{
  "ocr": {
    "language": "chinese_cht"  // 確認語言設置正確
  }
}
```

#### Q3: 處理速度慢？

**解決方法：**
```bash
# 增加 JVM 內存
java -Xmx4G -jar jpeg2pdf-ofd.jar config.json

# 增加 CPU 線程
{
  "ocr": {
    "cpuThreads": 8
  }
}

# 啟用 GPU 加速（如果有 GPU）
{
  "ocr": {
    "useGpu": true
  }
}
```

---

**GitHub:** https://github.com/brianshih04/jpeg2pdf-ofd-jpackage

**更新時間：** 2026-03-23
