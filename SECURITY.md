# 安全与隐私

- 仓库不包含 Apple Music APK、DEX、资源、签名密钥或账号信息。
- 构建在用户电脑本地完成；输入 APKM 不会上传。
- 首次构建会在 `.local` 中生成用户自己的签名密钥。请备份该目录；丢失密钥后，新包不能覆盖旧的自签名包。
- 发布前运行 `scripts/audit-release.ps1`，检查常见二进制、密钥、日志、IP 和本机路径泄漏。

如发现补丁器本身存在安全问题，请通过 GitHub Security Advisory 私下报告，不要在公开 Issue 中粘贴账号或完整日志。

