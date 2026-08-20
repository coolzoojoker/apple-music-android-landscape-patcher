# Apple Music Android 横屏补丁器

面向 Android TV 和可安装第三方 APK 的 Android 车机，在用户电脑本地把 **Apple Music 6.5.0 (1580)** 重建为更适合横屏的版本。

它解决有线网络被误判为离线的问题，并加入横屏 HOME、歌词、待播、视频歌曲布局与触摸下滑收起；车机配置另带沉浸式系统栏处理。

> 本仓库只发布原创补丁源码和构建脚本，不包含 Apple Music APK、Apple 资源、反编译 DEX 或通用签名密钥。使用者必须自行取得正版安装包，并遵守所在地法律和 Apple 的服务条款。本项目与 Apple Inc. 无关。

## 当前输出配置

| 构建参数 | 推荐设备 | 输出形式 |
|---|---|---|
| `tv-armv7` | 32 位 Android TV | ARMv7、全密度单 APK |
| `tv-arm64` | 64 位 Android TV / Google TV | ARM64、全密度单 APK |
| `car-armv7` | 较老的 32 位 Android 车机 | ARMv7、全密度、沉浸式单 APK |
| `car-arm64` | ARM64 Android 车机、比亚迪 DiLink 类设备 | ARM64、全密度、沉浸式单 APK |
| `tv-armv7-xhdpi` | 已验证的索尼电视精简配置 | ARMv7、xhdpi 单 APK |

最低 Android 11。更多说明见 [兼容性与构建配置](COMPATIBILITY.md)。目前不会为了看起来“型号很多”而复制未经验证的 Android 11/12/13/14 包；如果某一系统或厂商确实需要不同代码，会在取得日志和测试结果后新增配置。

## Windows 一键构建

准备：Windows 10/11、PowerShell 7（Windows PowerShell 5.1 也可）、约 5 GB 空闲空间，以及指定版本的 Apple Music APKM。

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\scripts\setup-toolchain.ps1
.\scripts\build.ps1 -InputApkm "D:\Downloads\AppleMusic-6.5.0.apkm" -Profile tv-arm64
```

首次准备会从官方发布地址下载锁定版本的 Temurin JDK、Android SDK 组件、APKEditor 和 Apktool，并逐个核对 SHA-256。首次构建会在 `.local\signing` 生成你自己的签名密钥；请备份该目录。

输出位于 `dist`。由于签名不同，通常不能覆盖 Apple 官方版本；安装前请确认账号和离线下载可恢复，再卸载原版。

## 安装示例

电视已经开启网络 ADB 时：

```powershell
adb connect 电视IP:5555
adb install "dist\AppleMusic-6.5.0-1580-tv-arm64-patched.apk"
```

车机请优先使用厂商允许的 U 盘、文件管理器或调试安装入口。本项目不会提供或绕过工程密码、安全签名、驾驶限制。驾驶时不要操作播放器。

## 功能范围

- 有线网络识别：首页、歌词和在线搜索不再仅依赖 Wi-Fi / 蜂窝网络判断。
- 横屏 HOME：封面 / 视频画面、歌曲信息、进度和控制区重排。
- 歌词 / 待播：复用原播放器的数据和按钮状态，横屏左右分栏。
- 视频歌曲：播放器形态间尽量复用原视频输出，不以静态封面代替视频。
- 触摸 / 鼠标：支持页面按钮与整页下滑收起；车机配置隐藏系统栏。
- 本地签名：密钥和安装包仅保留在使用者电脑。

## 效果图

### 普通歌曲

| HOME | 歌词 |
|---|---|
| ![普通歌曲 HOME](docs/screenshots/ordinary-home.png) | ![歌词页](docs/screenshots/ordinary-lyrics.png) |

![普通歌曲待播页](docs/screenshots/ordinary-queue.png)

### 视频歌曲

| HOME | 待播 |
|---|---|
| ![视频歌曲 HOME](docs/screenshots/video-home.png) | ![视频歌曲待播页](docs/screenshots/video-queue.png) |

## 已知不足

该补丁是在闭源手机应用上做运行时重排，不是完整重写。快速连续切页、视频首帧、部分无歌词 / 队列边界状态，仍可能受原版回调、网络和厂商解码器影响。完整列表见 [已知问题](KNOWN_ISSUES.md)。

## 自愿支持

项目保持免费。赞赏完全自愿，不解锁功能、不提供修改版 APK，也不构成 Apple Music 服务或内容的销售。扫码前请自行核对收款方。

| 支付宝 | 微信支付 |
|---|---|
| <a href="docs/support/alipay.jpg"><img src="docs/support/alipay.jpg" alt="支付宝收款码" width="320"></a> | <a href="docs/support/wechat.jpg"><img src="docs/support/wechat.jpg" alt="微信支付收款码" width="320"></a> |

更多说明见 [自愿支持](SUPPORT.md)。

## 开发与发布安全

发布前运行：

```powershell
.\scripts\audit-release.ps1
```

补丁对输入文件和目标代码锚点均采用失败即停止的校验。升级 Apple Music 版本时必须重新审计，不能直接忽略校验。安全细节见 [SECURITY.md](SECURITY.md)，第三方工具见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

## 许可

本仓库原创代码使用 Apache-2.0；Apple Music 及第三方工具不在此许可范围内。
