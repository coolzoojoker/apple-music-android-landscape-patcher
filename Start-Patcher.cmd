@echo off
chcp 65001 >nul
title Apple Music 横屏补丁器
cd /d "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\wizard.ps1" -InputApkm "%~1"
echo.
echo 窗口即将关闭。若上方显示错误，请先截图保存。
pause
