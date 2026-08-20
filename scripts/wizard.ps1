param([string]$InputApkm = '')

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot
$expectedHash = '18F1B7A0296FBF059D68509506729D6A291408BE43E3CE50060E15E5E8B2941A'

function Read-Choice([string]$Prompt, [string[]]$Allowed) {
    while ($true) {
        $answer = (Read-Host $Prompt).Trim().ToUpperInvariant()
        if ($answer -in $Allowed) { return $answer }
        Write-Host "请输入：$($Allowed -join ' / ')" -ForegroundColor Yellow
    }
}

function Select-Apkm {
    Add-Type -AssemblyName System.Windows.Forms
    $dialog = [Windows.Forms.OpenFileDialog]::new()
    $dialog.Title = '选择 Apple Music 6.5.0 (1580) APKM'
    $dialog.Filter = 'APKMirror 安装包 (*.apkm)|*.apkm|所有文件 (*.*)|*.*'
    $dialog.Multiselect = $false
    try {
        if ($dialog.ShowDialog() -ne [Windows.Forms.DialogResult]::OK) {
            throw '没有选择安装包，操作已取消。'
        }
        return $dialog.FileName
    } finally {
        $dialog.Dispose()
    }
}

Clear-Host
Write-Host 'Apple Music Android 横屏补丁器' -ForegroundColor Cyan
Write-Host '================================'
Write-Host '本程序不会提供或上传 Apple Music 安装包。'
Write-Host '它只在你的电脑上处理你自行取得的 Apple Music 6.5.0 (1580) APKM。'
Write-Host '首次运行需联网下载并校验开源工具、JDK 和 Android SDK，可能需要数分钟。'
Write-Host ''

$agree = Read-Choice '确认继续？输入 Y 继续，输入 N 退出' @('Y','N')
if ($agree -eq 'N') { return }

if ([string]::IsNullOrWhiteSpace($InputApkm)) { $InputApkm = Select-Apkm }
$InputApkm = [IO.Path]::GetFullPath($InputApkm)
if (-not (Test-Path -LiteralPath $InputApkm -PathType Leaf)) { throw "找不到文件：$InputApkm" }

Write-Host '正在核对基础包...' -ForegroundColor Cyan
$actualHash = (Get-FileHash -LiteralPath $InputApkm -Algorithm SHA256).Hash
if ($actualHash -ne $expectedHash) {
    throw @"
选择的文件不是当前支持的 Apple Music 6.5.0 (1580) APKM。
期望 SHA-256：$expectedHash
实际 SHA-256：$actualHash
请勿强行继续；回到下载说明核对版本、大小和文件名。
"@
}
Write-Host '基础包版本和哈希正确。' -ForegroundColor Green
Write-Host ''
Write-Host '请选择设备：'
Write-Host '  1. 索尼 K-85XR70 / 已验证的类似电视（32 位、xhdpi）'
Write-Host '  2. 普通 64 位 Android TV / Google TV'
Write-Host '  3. 普通 32 位 Android TV'
Write-Host '  4. 64 位 Android 车机（多数较新的车机，含比亚迪 DiLink 类）'
Write-Host '  5. 32 位 Android 车机（较老车机）'
$selection = Read-Choice '输入 1-5' @('1','2','3','4','5')
$profile = @{
    '1' = 'tv-armv7-xhdpi'
    '2' = 'tv-arm64'
    '3' = 'tv-armv7'
    '4' = 'car-arm64'
    '5' = 'car-armv7'
}[$selection]

Write-Host ''
Write-Host "将构建：$profile" -ForegroundColor Cyan
& (Join-Path $PSScriptRoot 'setup-toolchain.ps1')
& (Join-Path $PSScriptRoot 'build.ps1') -InputApkm $InputApkm -Profile $profile

$output = Join-Path $root "dist\AppleMusic-6.5.0-1580-$profile-patched.apk"
if (-not (Test-Path -LiteralPath $output -PathType Leaf)) { throw '构建结束但没有找到输出 APK。' }
Write-Host ''
Write-Host '成品已经生成。' -ForegroundColor Green
Write-Host "位置：$output"
Start-Process explorer.exe -ArgumentList "/select,`"$output`""

if ($profile.StartsWith('tv-')) {
    Write-Host '也可以把生成的 APK 复制到 U 盘或电视存储，用电视文件管理器安装；这种方式不需要 ADB。'
    $install = Read-Choice '是否改用网络 ADB 立即安装？输入 Y；准备自行复制安装请输入 N' @('Y','N')
    if ($install -eq 'Y') {
        & (Join-Path $PSScriptRoot 'install-tv.ps1') -Apk $output
    }
} else {
    Write-Host '车机安装：把这个 APK 复制到 U 盘或车机允许访问的目录，再使用厂商允许的安装入口。'
    Write-Host '本工具不会绕过工程密码、系统签名、驾驶限制或厂商安全策略。'
}
