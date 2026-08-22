param([string]$Path)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot
if (-not $Path) { $Path = $root }
$Path = [IO.Path]::GetFullPath($Path)

$gitDirectory = Join-Path $Path '.git'
if (Test-Path -LiteralPath $gitDirectory) {
    $relativeFiles = & git -C $Path ls-files --cached --others --exclude-standard
    if ($LASTEXITCODE -ne 0) { throw 'git ls-files 失败。' }
    $files = $relativeFiles | ForEach-Object {
        $candidate = Join-Path $Path $_
        if (Test-Path -LiteralPath $candidate -PathType Leaf) {
            Get-Item -LiteralPath $candidate
        }
    }
} else {
    $files = Get-ChildItem -LiteralPath $Path -Recurse -File -Force
}

$forbiddenExtensions = @('.apk','.apkm','.apks','.xapk','.aab','.dex','.jks','.keystore',
                         '.p12','.pfx','.pem','.key')
$badFiles = $files | Where-Object { $forbiddenExtensions -contains $_.Extension.ToLowerInvariant() }
if ($badFiles) {
    $badFiles.FullName | ForEach-Object { Write-Error "禁止发布的文件：$_" }
    throw '发现 APK、DEX 或密钥文件。'
}

$patterns = @(
    'BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY',
    'storepass\s*[=:]',
    'keypass\s*[=:]',
    'C:\\Users\\',
    '(?<![0-9])(?:10|127|169\.254|172\.(?:1[6-9]|2[0-9]|3[01])|192\.168)\.[0-9]{1,3}\.[0-9]{1,3}',
    'adb\s+connect\s+[0-9]'
)
$textExtensions = @('.md','.ps1','.java','.json','.yml','.yaml','.txt','.xml','.properties')
$textFiles = $files | Where-Object {
    $_.Length -lt 5MB -and
    ($textExtensions -contains $_.Extension.ToLowerInvariant() -or
     $_.Name -in @('.gitignore','.gitattributes'))
}
$hits = @()
foreach ($file in $textFiles) {
    foreach ($pattern in $patterns) {
        $match = Select-String -LiteralPath $file.FullName -Pattern $pattern -ErrorAction SilentlyContinue
        if ($match) { $hits += $match }
    }
}
if ($hits) {
    $hits | ForEach-Object { Write-Error "$($_.Path):$($_.LineNumber): $($_.Line.Trim())" }
    throw '发现可能的密钥、密码、本机绝对路径或内网地址。'
}

$launchers = $files | Where-Object { $_.Name -eq 'Start-Patcher.cmd' }
if (-not $launchers) { throw '缺少 Windows 启动器 Start-Patcher.cmd。' }
foreach ($launcher in $launchers) {
    $bytes = [IO.File]::ReadAllBytes($launcher.FullName)
    if ($bytes | Where-Object { $_ -gt 127 } | Select-Object -First 1) {
        throw "Windows 启动器必须只包含 ASCII 字符：$($launcher.FullName)"
    }
    $launcherText = [Text.Encoding]::ASCII.GetString($bytes)
    if ($launcherText -match "(?<!`r)`n" -or $launcherText -notmatch "`r`n") {
        throw "Windows 启动器必须使用 CRLF 换行：$($launcher.FullName)"
    }
}

Write-Host '发布审计通过：未发现 Apple 安装包、DEX、签名密钥或常见隐私泄漏。'
