param([switch]$Force)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
Add-Type -AssemblyName System.IO.Compression.FileSystem
$root = Split-Path -Parent $PSScriptRoot
$local = Join-Path $root '.local'
$downloads = Join-Path $local 'downloads'
$toolchain = Join-Path $local 'toolchain'
$tools = Join-Path $local 'tools'
New-Item -ItemType Directory -Path $downloads,$toolchain,$tools -Force | Out-Null
$lock = Get-Content (Join-Path $root 'tools.lock.json') -Raw | ConvertFrom-Json

function Get-LockedFile($item, [string]$name, [string]$destination) {
    if ($Force -or -not (Test-Path -LiteralPath $destination)) {
        Write-Host "下载 $name ..."
        Invoke-WebRequest -Uri $item.url -OutFile $destination
    }
    $actual = (Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash
    if ($actual -ne $item.sha256) {
        throw "$name 的 SHA-256 不匹配。期望 $($item.sha256)，实际 $actual"
    }
    Write-Host "$name 校验通过。"
}

Get-LockedFile $lock.tools.apkeditor 'APKEditor' (Join-Path $tools 'APKEditor.jar')
Get-LockedFile $lock.tools.apktool 'Apktool' (Join-Path $tools 'apktool.jar')

$archives = @(
    @{ Item = $lock.tools.temurinJdk; Name = 'Temurin JDK'; File = 'temurin-jdk.zip'; Folder = 'jdk' },
    @{ Item = $lock.tools.androidPlatform; Name = 'Android Platform 35'; File = 'platform-35.zip'; Folder = 'platform' },
    @{ Item = $lock.tools.androidBuildTools; Name = 'Android Build Tools 36.1'; File = 'build-tools.zip'; Folder = 'build-tools' }
)
foreach ($archive in $archives) {
    $zip = Join-Path $downloads $archive.File
    $folder = Join-Path $toolchain $archive.Folder
    Get-LockedFile $archive.Item $archive.Name $zip
    if ($Force -or -not (Test-Path -LiteralPath $folder)) {
        if (Test-Path -LiteralPath $folder) {
            $resolved = [IO.Path]::GetFullPath($folder)
            $allowed = [IO.Path]::GetFullPath($toolchain) + [IO.Path]::DirectorySeparatorChar
            if (-not $resolved.StartsWith($allowed, [StringComparison]::OrdinalIgnoreCase)) {
                throw "拒绝清理工具链目录之外的路径：$resolved"
            }
            Remove-Item -LiteralPath $resolved -Recurse -Force
        }
        New-Item -ItemType Directory -Path $folder -Force | Out-Null
        [IO.Compression.ZipFile]::ExtractToDirectory($zip, $folder)
    }
}

$javac = Get-ChildItem (Join-Path $toolchain 'jdk') -Filter javac.exe -Recurse | Select-Object -First 1
$androidJar = Get-ChildItem (Join-Path $toolchain 'platform') -Filter android.jar -Recurse | Select-Object -First 1
$apksigner = Get-ChildItem (Join-Path $toolchain 'build-tools') -Filter apksigner.bat -Recurse | Select-Object -First 1
if (-not $javac -or -not $androidJar -or -not $apksigner) {
    throw '工具链解压后缺少 javac、android.jar 或 apksigner。'
}

Write-Host ''
Write-Host '工具链准备完成：'
Write-Host "  JDK: $($javac.Directory.Parent.FullName)"
Write-Host "  Android API: $($androidJar.FullName)"
Write-Host "  Build Tools: $($apksigner.Directory.FullName)"
