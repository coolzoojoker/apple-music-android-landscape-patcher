param(
    [Parameter(Mandatory = $true)]
    [string]$InputApkm,

    [ValidateSet('tv-armv7', 'tv-arm64', 'car-armv7', 'car-arm64', 'tv-armv7-xhdpi')]
    [string]$Profile = 'tv-armv7',

    [string]$OutputDirectory,
    [switch]$KeepWork
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$root = Split-Path -Parent $PSScriptRoot
if (-not $OutputDirectory) { $OutputDirectory = Join-Path $root 'dist' }
$InputApkm = [IO.Path]::GetFullPath($InputApkm)
$OutputDirectory = [IO.Path]::GetFullPath($OutputDirectory)

function Require-File([string]$Path, [string]$Hint) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "缺少 $Path。$Hint"
    }
    return [IO.Path]::GetFullPath($Path)
}

function Run([string]$Program, [string[]]$Arguments) {
    $redactNext = $false
    $displayArguments = foreach ($argument in $Arguments) {
        if ($redactNext) {
            $redactNext = $false
            '<redacted>'
        } elseif ($argument -in @('-storepass','-keypass','--ks-pass','--key-pass')) {
            $redactNext = $true
            $argument
        } elseif ($argument -match '^pass:') {
            'pass:<redacted>'
        } else {
            $argument
        }
    }
    Write-Host ('> ' + $Program + ' ' + ($displayArguments -join ' '))
    & $Program @Arguments
    if ($LASTEXITCODE -ne 0) { throw "命令失败，退出码 $LASTEXITCODE：$Program" }
}

function Add-DexToApk([string]$Apk, [string]$Dex) {
    $stream = [IO.File]::Open($Apk, [IO.FileMode]::Open, [IO.FileAccess]::ReadWrite)
    try {
        $zip = [IO.Compression.ZipArchive]::new($stream, [IO.Compression.ZipArchiveMode]::Update, $false)
        try {
            $numbers = foreach ($entry in $zip.Entries) {
                if ($entry.FullName -match '^classes(?<n>\d*)\.dex$') {
                    if ($Matches.n) { [int]$Matches.n } else { 1 }
                }
            }
            $next = (($numbers | Measure-Object -Maximum).Maximum + 1)
            if (-not $next) { $next = 1 }
            $entryName = if ($next -eq 1) { 'classes.dex' } else { "classes$next.dex" }
            if ($zip.GetEntry($entryName)) { throw "DEX 入口已存在：$entryName" }
            [IO.Compression.ZipFileExtensions]::CreateEntryFromFile(
                $zip, $Dex, $entryName, [IO.Compression.CompressionLevel]::Optimal
            ) | Out-Null
            Write-Host "已注入 $entryName"
        } finally {
            $zip.Dispose()
        }
    } finally {
        $stream.Dispose()
    }
}

function Add-CarProvider([string]$InputApk, [string]$OutputApk, [string]$DecodeDirectory,
                         [string]$Java, [string]$ApkEditor) {
    Run $Java @('-jar', $ApkEditor, 'd', '-i', $InputApk, '-o', $DecodeDirectory, '-dex', '-t', 'xml', '-f')
    $manifestPath = Join-Path $DecodeDirectory 'AndroidManifest.xml'
    [xml]$manifest = Get-Content -LiteralPath $manifestPath -Raw
    $application = $manifest.SelectSingleNode('/manifest/application')
    if (-not $application) { throw 'AndroidManifest.xml 中找不到 application。' }
    $androidNs = 'http://schemas.android.com/apk/res/android'
    $duplicate = $application.ChildNodes | Where-Object {
        $_.Name -eq 'provider' -and
        $_.GetAttribute('name', $androidNs) -eq 'com.apple.android.music.car.CarWindowProvider'
    } | Select-Object -First 1
    if (-not $duplicate) {
        $provider = $manifest.CreateElement('provider')
        $provider.SetAttribute('name', $androidNs, 'com.apple.android.music.car.CarWindowProvider') | Out-Null
        $provider.SetAttribute('authorities', $androidNs, 'com.apple.android.music.car.windowinit') | Out-Null
        $provider.SetAttribute('enabled', $androidNs, 'true') | Out-Null
        $provider.SetAttribute('exported', $androidNs, 'false') | Out-Null
        $provider.SetAttribute('initOrder', $androidNs, '1999999999') | Out-Null
        $application.AppendChild($provider) | Out-Null
        $settings = [Xml.XmlWriterSettings]::new()
        $settings.Encoding = [Text.UTF8Encoding]::new($false)
        $settings.Indent = $true
        $writer = [Xml.XmlWriter]::Create($manifestPath, $settings)
        try { $manifest.Save($writer) } finally { $writer.Dispose() }
    }
    Run $Java @('-jar', $ApkEditor, 'b', '-i', $DecodeDirectory, '-o', $OutputApk, '-f')
}

$expectedHash = '18F1B7A0296FBF059D68509506729D6A291408BE43E3CE50060E15E5E8B2941A'
$actualHash = (Get-FileHash -LiteralPath (Require-File $InputApkm '请传入 Apple Music 6.5.0 (1580) 的 APKM。') -Algorithm SHA256).Hash
if ($actualHash -ne $expectedHash) {
    throw "输入 APKM 不受支持。期望 SHA-256 $expectedHash，实际 $actualHash"
}

$local = Join-Path $root '.local'
$apkEditor = Require-File (Join-Path $local 'tools\APKEditor.jar') '请先运行 scripts\setup-toolchain.ps1。'
$apktool = Require-File (Join-Path $local 'tools\apktool.jar') '请先运行 scripts\setup-toolchain.ps1。'
$javac = Get-ChildItem (Join-Path $local 'toolchain\jdk') -Filter javac.exe -Recurse | Select-Object -First 1
$java = Get-ChildItem (Join-Path $local 'toolchain\jdk') -Filter java.exe -Recurse | Select-Object -First 1
$jar = Get-ChildItem (Join-Path $local 'toolchain\jdk') -Filter jar.exe -Recurse | Select-Object -First 1
$keytool = Get-ChildItem (Join-Path $local 'toolchain\jdk') -Filter keytool.exe -Recurse | Select-Object -First 1
$androidJar = Get-ChildItem (Join-Path $local 'toolchain\platform') -Filter android.jar -Recurse | Select-Object -First 1
$d8 = Get-ChildItem (Join-Path $local 'toolchain\build-tools') -Filter d8.bat -Recurse | Select-Object -First 1
$zipalign = Get-ChildItem (Join-Path $local 'toolchain\build-tools') -Filter zipalign.exe -Recurse | Select-Object -First 1
$apksigner = Get-ChildItem (Join-Path $local 'toolchain\build-tools') -Filter apksigner.bat -Recurse | Select-Object -First 1
foreach ($item in @($javac,$java,$jar,$keytool,$androidJar,$d8,$zipalign,$apksigner)) {
    if (-not $item) { throw '本地工具链不完整，请重新运行 scripts\setup-toolchain.ps1 -Force。' }
}
$env:JAVA_HOME = $java.Directory.Parent.FullName
$env:PATH = $java.Directory.FullName + [IO.Path]::PathSeparator + $env:PATH

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$workRoot = Join-Path (Join-Path $root 'build') ([guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $workRoot -Force | Out-Null
Write-Host "工作目录：$workRoot"

try {
    $apkmDirectory = Join-Path $workRoot 'apkm'
    [IO.Compression.ZipFile]::ExtractToDirectory($InputApkm, $apkmDirectory)
    $moduleDirectory = Join-Path $workRoot 'modules'
    New-Item -ItemType Directory -Path $moduleDirectory | Out-Null

    $abi = if ($Profile -in @('tv-armv7','car-armv7','tv-armv7-xhdpi')) { 'armeabi_v7a' } else { 'arm64_v8a' }
    $modules = @('base.apk', "split_config.$abi.apk")
    if ($Profile -eq 'tv-armv7-xhdpi') {
        $modules += 'split_config.xhdpi.apk'
    } else {
        $modules += @('split_config.ldpi.apk','split_config.mdpi.apk','split_config.tvdpi.apk',
                      'split_config.hdpi.apk','split_config.xhdpi.apk','split_config.xxhdpi.apk',
                      'split_config.xxxhdpi.apk')
    }
    foreach ($module in $modules) {
        Copy-Item -LiteralPath (Require-File (Join-Path $apkmDirectory $module) "APKM 缺少模块 $module") -Destination $moduleDirectory
    }

    $merged = Join-Path $workRoot 'merged.apk'
    Run $java.FullName @('-jar', $apkEditor, 'm', '-i', $moduleDirectory, '-o', $merged, '-clean-meta', '-f')
    $decoded = Join-Path $workRoot 'decoded'
    Run $java.FullName @('-jar', $apktool, 'd', '-r', '-f', $merged, '-o', $decoded)
    & (Join-Path $PSScriptRoot 'patch-smali.ps1') -DecodedDirectory $decoded

    $classes = Join-Path $workRoot 'classes'
    New-Item -ItemType Directory -Path $classes | Out-Null
    $sources = @((Join-Path $root 'src\tv\com\apple\android\music\player\fragment\TVLyricsLayout.java'))
    if ($Profile -in @('car-armv7','car-arm64')) {
        $sources += (Join-Path $root 'src\car\com\apple\android\music\car\CarWindowProvider.java')
    }
    Run $javac.FullName (@('-encoding','UTF-8','-source','8','-target','8','-classpath',$androidJar.FullName,'-d',$classes) + $sources)
    $classJar = Join-Path $workRoot 'patch-classes.jar'
    Run $jar.FullName @('cf', $classJar, '-C', $classes, '.')
    $dexDirectory = Join-Path $workRoot 'dex'
    New-Item -ItemType Directory -Path $dexDirectory | Out-Null
    Run $d8.FullName @('--min-api','30','--output',$dexDirectory,$classJar)

    $unsigned = Join-Path $workRoot 'unsigned.apk'
    Run $java.FullName @('-jar', $apktool, 'b', $decoded, '-o', $unsigned)
    Add-DexToApk $unsigned (Join-Path $dexDirectory 'classes.dex')

    if ($Profile -in @('car-armv7','car-arm64')) {
        $carUnsigned = Join-Path $workRoot 'car-unsigned.apk'
        Add-CarProvider $unsigned $carUnsigned (Join-Path $workRoot 'car-decoded') $java.FullName $apkEditor
        $unsigned = $carUnsigned
    }

    $signingDirectory = Join-Path $local 'signing'
    New-Item -ItemType Directory -Path $signingDirectory -Force | Out-Null
    $keystore = Join-Path $signingDirectory 'user-signing.jks'
    $passwordFile = Join-Path $signingDirectory 'password.txt'
    if (-not (Test-Path -LiteralPath $keystore)) {
        $random = [byte[]]::new(24)
        [Security.Cryptography.RandomNumberGenerator]::Fill($random)
        $password = [Convert]::ToHexString($random)
        [IO.File]::WriteAllText($passwordFile, $password, [Text.UTF8Encoding]::new($false))
        Run $keytool.FullName @('-genkeypair','-keystore',$keystore,'-storepass',$password,
            '-keypass',$password,'-alias','applemusic-local','-keyalg','RSA','-keysize','4096',
            '-validity','10000','-dname','CN=Local Apple Music Patcher, O=Local Build, C=XX')
        Write-Warning "已生成本机签名密钥。请备份 $signingDirectory"
    }
    $password = (Get-Content -LiteralPath (Require-File $passwordFile '签名密码文件缺失。') -Raw).Trim()

    $aligned = Join-Path $workRoot 'aligned.apk'
    Run $zipalign.FullName @('-f','-p','4',$unsigned,$aligned)
    $output = Join-Path $OutputDirectory "AppleMusic-6.5.0-1580-$Profile-patched.apk"
    if (Test-Path -LiteralPath $output) { Remove-Item -LiteralPath $output -Force }
    Run $apksigner.FullName @('sign','--ks',$keystore,'--ks-key-alias','applemusic-local',
        '--ks-pass',"pass:$password",'--key-pass',"pass:$password",'--out',$output,$aligned)
    Run $apksigner.FullName @('verify','--verbose','--print-certs',$output)

    $hash = (Get-FileHash -LiteralPath $output -Algorithm SHA256).Hash
    Write-Host ''
    Write-Host '构建完成：'
    Write-Host "  配置：$Profile"
    Write-Host "  APK：$output"
    Write-Host "  SHA-256：$hash"
} finally {
    if ($KeepWork) {
        Write-Host "保留工作目录：$workRoot"
    } elseif (Test-Path -LiteralPath $workRoot) {
        $resolved = [IO.Path]::GetFullPath($workRoot)
        $allowed = [IO.Path]::GetFullPath((Join-Path $root 'build')) + [IO.Path]::DirectorySeparatorChar
        if (-not $resolved.StartsWith($allowed, [StringComparison]::OrdinalIgnoreCase)) {
            throw "拒绝清理 build 目录之外的路径：$resolved"
        }
        Remove-Item -LiteralPath $resolved -Recurse -Force
    }
}
