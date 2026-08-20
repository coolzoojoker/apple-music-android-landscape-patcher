param(
    [Parameter(Mandatory = $true)]
    [string]$Apk
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot
$adb = Get-ChildItem (Join-Path $root '.local\toolchain\platform-tools') -Filter adb.exe -Recurse | Select-Object -First 1
if (-not $adb) { throw '没有找到 ADB，请先运行构建向导准备工具。' }
$Apk = [IO.Path]::GetFullPath($Apk)
if (-not (Test-Path -LiteralPath $Apk -PathType Leaf)) { throw "找不到 APK：$Apk" }

Write-Host ''
Write-Host '电视端先开启“开发者选项 / 网络调试”，并让电视和电脑处于同一局域网。'
$address = (Read-Host '输入电视设置中显示的 IP；如系统同时显示端口，也可输入 IP:端口').Trim()
if ($address -notmatch ':\d+$') { $address = "$address`:5555" }

& $adb.FullName connect $address
if ($LASTEXITCODE -ne 0) { throw 'ADB 连接失败。请检查电视 IP、调试开关和电视上的授权提示。' }
$state = (& $adb.FullName -s $address get-state 2>$null).Trim()
if ($state -ne 'device') { throw '电视尚未授权。请在电视上允许这台电脑调试，然后重试。' }

$model = (& $adb.FullName -s $address shell getprop ro.product.model).Trim()
$android = (& $adb.FullName -s $address shell getprop ro.build.version.release).Trim()
$abi = (& $adb.FullName -s $address shell getprop ro.product.cpu.abilist).Trim()
Write-Host "已连接：$model / Android $android / $abi" -ForegroundColor Green
Write-Host '安装只会尝试覆盖，不会自动卸载现有应用，也不会清除电视数据。'

$installOutput = & $adb.FullName -s $address install -r $Apk 2>&1
$installOutput | ForEach-Object { Write-Host $_ }
if ($LASTEXITCODE -ne 0) {
    if (($installOutput -join "`n") -match 'INSTALL_FAILED_UPDATE_INCOMPATIBLE') {
        Write-Warning '签名与原版不同，不能直接覆盖。请先确认账号和离线下载可恢复，再由你手动卸载原版，然后重新运行安装；本工具不会替你卸载。'
    }
    throw 'ADB 安装失败，未执行卸载或清除操作。'
}
Write-Host '电视安装完成。' -ForegroundColor Green
