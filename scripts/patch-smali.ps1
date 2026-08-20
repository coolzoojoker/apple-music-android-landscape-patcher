param(
    [Parameter(Mandatory = $true)]
    [string]$DecodedDirectory
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Read-Utf8([string]$Path) {
    return [IO.File]::ReadAllText($Path, [Text.UTF8Encoding]::new($false))
}

function Write-Utf8([string]$Path, [string]$Text) {
    [IO.File]::WriteAllText($Path, $Text, [Text.UTF8Encoding]::new($false))
}

function Replace-Once([string]$Text, [string]$Old, [string]$New, [string]$Label) {
    $first = $Text.IndexOf($Old, [StringComparison]::Ordinal)
    if ($first -lt 0) { throw "补丁锚点不存在：$Label" }
    if ($Text.IndexOf($Old, $first + $Old.Length, [StringComparison]::Ordinal) -ge 0) {
        throw "补丁锚点不唯一：$Label"
    }
    return $Text.Substring(0, $first) + $New + $Text.Substring($first + $Old.Length)
}

function Get-RequiredFile([string]$RelativePath) {
    $path = Join-Path $DecodedDirectory $RelativePath
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "缺少目标文件：$RelativePath"
    }
    return $path
}

# Apple Music 6.5.0 incorrectly treats validated Ethernet as offline. Preserve
# the original cellular/Wi-Fi checks and accept connected non-cellular networks.
$networkPath = Get-RequiredFile 'smali_classes3\ba.1\a.smali'
$network = Read-Utf8 $networkPath
$methodMatch = [regex]::Match(
    $network,
    '(?ms)^\.method public final b\(Landroid/content/Context;\)Z\r?\n.*?^\.end method'
)
if (-not $methodMatch.Success) { throw '找不到网络状态方法 ba/a.b(Context)' }
$method = $methodMatch.Value
$method = Replace-Once $method "    move-result p0`r`n`r`n    .line 26`r`n    if-eqz p0, :cond_2" "    move-result v0`r`n`r`n    .line 26`r`n    if-eqz v0, :cond_2" 'Ethernet/result-register'
$oldTail = "    :cond_2`r`n    const/4 p0, 0x0`r`n`r`n    .line 31`r`n    return p0"
$newTail = @"
    :cond_2
    invoke-virtual {p0, p1}, Lba/a;->e(Landroid/content/Context;)Z

    move-result v0

    if-eqz v0, :tv_net_offline

    invoke-virtual {p0, p1}, Lba/a;->f(Landroid/content/Context;)Z

    move-result v0

    if-nez v0, :tv_net_offline

    const/4 v0, 0x1

    return v0

    :tv_net_offline
    const/4 v0, 0x0

    .line 31
    return v0
"@ -replace "`n", "`r`n"
$method = Replace-Once $method $oldTail $newTail 'Ethernet/fallback'
$network = $network.Substring(0, $methodMatch.Index) + $method + $network.Substring($methodMatch.Index + $methodMatch.Length)
Write-Utf8 $networkPath $network

# Install/destroy the landscape controller with the native player fragment.
$baseFragmentPath = Get-RequiredFile 'smali_classes2\com\apple\android\music\player\fragment\m.1.smali'
$baseFragment = Read-Utf8 $baseFragmentPath
$destroyAnchor = ".method public onDestroyView()V`r`n    .locals 3`r`n"
$destroyHook = @"
.method public onDestroyView()V
    .locals 3

    invoke-virtual {p0}, Landroidx/fragment/app/l;->getView()Landroid/view/View;

    move-result-object v0

    invoke-static {v0}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->destroy(Landroid/view/View;)V
"@ -replace "`n", "`r`n"
$baseFragment = Replace-Once $baseFragment $destroyAnchor $destroyHook 'player/destroy'
$installAnchor = "    :goto_3`r`n    invoke-virtual {p0}, Lcom/apple/android/music/player/fragment/m;->w1()V`r`n"
$installHook = @"
    :goto_3
    invoke-virtual {p0}, Lcom/apple/android/music/player/fragment/m;->w1()V

    invoke-virtual {p0}, Landroidx/fragment/app/l;->getView()Landroid/view/View;

    move-result-object v0

    invoke-static {v0}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->install(Landroid/view/View;)V
"@ -replace "`n", "`r`n"
$baseFragment = Replace-Once $baseFragment $installAnchor $installHook 'player/install'
Write-Utf8 $baseFragmentPath $baseFragment

# Cover the dedicated lyrics fragment lifecycle as well. Custom labels avoid
# renumbering or touching the app's original control-flow labels.
$lyricsPath = Get-RequiredFile 'smali_classes2\com\apple\android\music\player\fragment\PlayerLyricsViewFragment.smali'
$lyrics = Read-Utf8 $lyricsPath
$rootAnchor = "    iget-object v0, v0, Landroidx/databinding/ViewDataBinding;->d:Landroid/view/View;`r`n`r`n    .line 884"
$rootHook = "    iget-object v0, v0, Landroidx/databinding/ViewDataBinding;->d:Landroid/view/View;`r`n`r`n    invoke-static {v0}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->install(Landroid/view/View;)V`r`n`r`n    .line 884"
$lyrics = Replace-Once $lyrics $rootAnchor $rootHook 'lyrics/install'
$lyricsDestroyAnchor = ".method public final onDestroyView()V`r`n    .locals 2`r`n"
$lyricsDestroyHook = @"
.method public final onDestroyView()V
    .locals 2

    invoke-virtual {p0}, Landroidx/fragment/app/l;->getView()Landroid/view/View;

    move-result-object v0

    invoke-static {v0}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->destroy(Landroid/view/View;)V
"@ -replace "`n", "`r`n"
$lyrics = Replace-Once $lyrics $lyricsDestroyAnchor $lyricsDestroyHook 'lyrics/destroy'
$hiddenAnchor = "    invoke-super {p0, p1}, Lcom/apple/android/music/player/fragment/e;->onHiddenChanged(Z)V`r`n"
$hiddenHook = @"
    invoke-super {p0, p1}, Lcom/apple/android/music/player/fragment/e;->onHiddenChanged(Z)V

    if-eqz p1, :tv_skip_exit

    invoke-virtual {p0}, Landroidx/fragment/app/l;->getView()Landroid/view/View;

    move-result-object v2

    invoke-static {v2}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->exitFullscreen(Landroid/view/View;)V

    :tv_skip_exit
"@ -replace "`n", "`r`n"
$lyrics = Replace-Once $lyrics $hiddenAnchor $hiddenHook 'lyrics/hidden'
Write-Utf8 $lyricsPath $lyrics

# Forward native player-state changes so stale lyrics/queue state can be reset.
$statePath = Get-RequiredFile 'smali_classes2\com\apple\android\music\player\fragment\w0$k.smali'
$state = Read-Utf8 $statePath
$stateAnchor = "    .locals 9`r`n`r`n    .line 1`r`n    iget-object v0, p0, Lcom/apple/android/music/player/fragment/w0`$k;->h:Lcom/apple/android/music/player/fragment/w0;"
$stateHook = "    .locals 9`r`n`r`n    .line 1`r`n    invoke-static {p1, p2}, Lcom/apple/android/music/player/fragment/TVLyricsLayout;->onPlayerState(Landroid/view/View;I)V`r`n`r`n    iget-object v0, p0, Lcom/apple/android/music/player/fragment/w0`$k;->h:Lcom/apple/android/music/player/fragment/w0;"
$state = Replace-Once $state $stateAnchor $stateHook 'player/state'
Write-Utf8 $statePath $state

Write-Host 'Smali 补丁已应用，并通过全部唯一锚点校验。'

