@echo off
setlocal
title Apple Music Landscape Patcher
cd /d "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\wizard.ps1" -InputApkm "%~1"
set "exitCode=%ERRORLEVEL%"
echo.
if not "%exitCode%"=="0" echo Patcher exited with error code %exitCode%.
pause
exit /b %exitCode%
