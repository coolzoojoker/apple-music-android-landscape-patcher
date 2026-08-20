param(
    [string]$Version = '0.2.0-preview',
    [string]$OutputDirectory
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot
if (-not $OutputDirectory) { $OutputDirectory = Join-Path $root 'release' }
$OutputDirectory = [IO.Path]::GetFullPath($OutputDirectory)
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null

$stageRoot = Join-Path $root 'build\release-package'
$stage = Join-Path $stageRoot "AppleMusic-Landscape-Patcher-Windows-$Version"
if (Test-Path -LiteralPath $stageRoot) {
    $resolved = [IO.Path]::GetFullPath($stageRoot)
    $allowed = [IO.Path]::GetFullPath((Join-Path $root 'build')) + [IO.Path]::DirectorySeparatorChar
    if (-not $resolved.StartsWith($allowed, [StringComparison]::OrdinalIgnoreCase)) {
        throw "拒绝清理 build 目录之外的路径：$resolved"
    }
    Remove-Item -LiteralPath $resolved -Recurse -Force
}
New-Item -ItemType Directory -Path $stage -Force | Out-Null

$files = @(
    'Start-Patcher.cmd',
    'README.md',
    'SUPPORT.md',
    'LICENSE',
    'SECURITY.md',
    'COMPATIBILITY.md',
    'KNOWN_ISSUES.md',
    'THIRD_PARTY_NOTICES.md',
    'tools.lock.json'
)
$directories = @('scripts','src','patches','docs')

foreach ($relative in $files) {
    Copy-Item -LiteralPath (Join-Path $root $relative) -Destination (Join-Path $stage $relative) -Force
}
foreach ($relative in $directories) {
    $source = Join-Path $root $relative
    $destination = Join-Path $stage $relative
    $parent = Split-Path -Parent $destination
    New-Item -ItemType Directory -Path $parent -Force | Out-Null
    Copy-Item -LiteralPath $source -Destination $destination -Recurse -Force
}

& (Join-Path $PSScriptRoot 'audit-release.ps1') -Path $stage

$zip = Join-Path $OutputDirectory "AppleMusic-Landscape-Patcher-Windows-$Version.zip"
if (Test-Path -LiteralPath $zip) { Remove-Item -LiteralPath $zip -Force }
Compress-Archive -LiteralPath $stage -DestinationPath $zip -CompressionLevel Optimal
$hash = (Get-FileHash -LiteralPath $zip -Algorithm SHA256).Hash
$hashFile = "$zip.sha256"
[IO.File]::WriteAllText($hashFile, "$hash  $([IO.Path]::GetFileName($zip))`r`n", [Text.UTF8Encoding]::new($false))

Write-Host 'Release 压缩包已生成：'
Write-Host "  ZIP：$zip"
Write-Host "  SHA-256：$hash"
Write-Host "  校验文件：$hashFile"
