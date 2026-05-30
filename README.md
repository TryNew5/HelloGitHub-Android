# HelloGitHub Android

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blueviolet?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Comose-1.7-blue?logo=jetpackcompose)](https://developer.android.com/compose)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Android 客户端 for [HelloGitHub](https://hellogithub.com/) —— 发现面向初学者的优质开源项目。

---

## 功能特性

| 模块 | 说明 |
|------|------|
| **首页** | 开源项目推荐流，支持按标签/时间筛选，无限滚动加载 |
| **榜单** | 周榜 / 月榜 / 年榜，月榜支持按月份筛选，年榜支持按年份筛选 |
| **月刊** | HelloGitHub 月刊列表，支持选择期数查看每期推荐项目 |
| **搜索** | 300ms 防抖搜索，实时检索开源项目 |
| **设置** | 浅色 / 深色 / 跟随系统 主题切换 |
| **项目详情** | Star 增长趋势图、Markdown 项目介绍、项目基础信息 |

## 截图

> TODO: 添加应用截图

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Repository |
| 依赖注入 | Koin |
| 网络 | Retrofit 2.11 + OkHttp 4.12 + Kotlin Serialization |
| 图片加载 | Coil |
| 导航 | Navigation Compose |
| 本地存储 | DataStore Preferences |

## 开始使用

### 环境要求

- Android Studio (最新稳定版)
- JDK 21
- Android SDK 36
- Gradle 8.9+

### 构建 & 安装

```bash
# 设置 JDK 21
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"

# 编译 Debug APK
./gradlew assembleDebug

# 安装到设备
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 连接物理设备

```bash
# 通过 USB 连接 Android 设备，确认 ADB 识别
adb devices
```

## 项目结构

```
app/src/main/java/com/hellogithub/app/
├── HelloGitHubApp.kt              # Application 入口
├── MainActivity.kt                # 主 Activity
├── di/
│   ├── AppModule.kt               # ViewModel & Repository 注入
│   └── NetworkModule.kt           # 网络层注入 (Retrofit, OkHttp, Coil)
├── data/
│   ├── remote/
│   │   ├── ApiService.kt          # Retrofit API 接口定义
│   │   ├── PeriodicalWebService.kt # 月刊详情 HTML 抓取
│   │   └── dto/                   # 网络响应 DTO
│   └── repository/                # 数据仓库层
├── ui/
│   ├── theme/                     # Material 3 黑白极简主题
│   ├── navigation/                # 导航图 & 底部导航栏
│   ├── components/                # 共享 UI 组件
│   ├── feed/                      # 首页 (推荐流)
│   ├── rank/                      # 榜单 (周/月/年)
│   ├── periodical/                # 月刊
│   ├── search/                    # 搜索
│   ├── detail/                    # 项目详情
│   └── settings/                  # 设置
└── util/
    └── CoroutineUtils.kt          # 协程安全工具
```

## 架构

```
UI (Compose) → ViewModel → Repository → ApiService (Retrofit)
                                       → PeriodicalWebService (OkHttp)
```

- **MVVM + Repository** 模式，通过 Koin 实现依赖注入
- **snapshot state** 安全取值，避免 Compose 并发 Crash
- **safeApiCall** 封装 `runCatching`，正确传播 `CancellationException`
- OkHttp 全局注入 `Referer` 头，绕过图片防盗链

## API

数据来源于 [HelloGitHub API](https://api.hellogithub.com/v1/)，包括：

- 首页推荐流 (`/v1/`)
- 项目详情 (`/v1/repository/detail/{rid}`)
- 搜索 (`/v1/search/`)
- 月刊列表 (`/v1/periodical/`)
- 标签 (`/v1/tag/`)

月刊详情通过解析 HelloGitHub 网页 HTML 中的 `__NEXT_DATA__` JSON 获取。

## 开源协议

[MIT License](LICENSE)
