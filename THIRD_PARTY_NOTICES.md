# 第三方说明

构建器会按锁定版本下载以下工具，工具本身不提交到仓库：

- [APKEditor 1.4.9](https://github.com/REAndroid/APKEditor) — REAndroid
- [Apktool 3.0.2](https://github.com/iBotPeaches/Apktool) — iBotPeaches
- [Android SDK Platform / Build Tools / Platform Tools](https://developer.android.com/tools/releases/platform-tools) — Google
- [Eclipse Temurin JDK 21](https://adoptium.net/) — Eclipse Adoptium

下载地址和 SHA-256 固定在 `tools.lock.json`。二进制只下载到使用者本机的 `.local`，不随源码或 Release ZIP 再分发；各工具继续受其自身许可约束。

Apple Music、Apple 标志、专辑图和相关商标属于 Apple Inc. 或各自权利人。本项目与 Apple Inc. 无隶属、授权或背书关系。
