<p align="center">
  <a href="README.md"><strong>简体中文</strong></a> ·
  <a href="README_EN.md"><strong>English</strong></a>
</p>

# Apple Music Android Landscape Patcher

A local patching toolkit that rebuilds **Apple Music 6.5.0 (1580)** into a landscape-oriented version for Android TV devices and Android-based in-vehicle systems that permit third-party APK installation.

It fixes wired Ethernet being incorrectly treated as offline and adds landscape HOME, lyrics, queue, and music-video layouts with a touch-friendly swipe-down gesture. The in-vehicle profiles also include immersive system-bar handling.

> This repository distributes only original patch source code and build scripts. It does not contain an Apple Music APK, Apple assets, decompiled DEX files, or a shared signing key. You must obtain the legitimate base package yourself and comply with applicable law and Apple's terms. This project is not affiliated with Apple Inc.

## Input Methods

The landscape player is currently designed primarily for touchscreens and pointer input. On a TV, use a USB/Bluetooth mouse, an air-mouse remote, or another remote that can emulate an on-screen pointer.

Focus navigation with a conventional directional-pad remote is not yet fully adapted. Some controls may be unreachable or inoperable when using only the arrow and OK buttons. Tablets and in-vehicle touchscreens can be operated directly by touch.

## Quick Start — No Command Line Required

1. Download the Windows patcher ZIP from this project's **Releases** page and extract the entire archive.
2. Obtain the Apple Music 6.5.0 (1580) APKM from the [APKMirror release page](https://www.apkmirror.com/apk/apple/apple-music/apple-music-6-5-0-release/apple-music-6-5-0-android-apk-download/). This project does not mirror or download the Apple package for you.
3. Double-click `Start-Patcher.cmd`, select the APKM, and choose a TV or in-vehicle profile by following the prompts.
4. The patcher downloads and verifies its tools, merges the bundle, applies the selected patches, and signs the result locally. File Explorer opens the output folder when the build finishes.

Requirements:

- 64-bit Windows 10 or Windows 11
- At least 5 GB of free disk space and a stable internet connection
- An Android 11 or newer TV/in-vehicle device that allows third-party APK installation
- Your own Apple Music account and subscription
- The supported Apple Music 6.5.0 (1580) APKM

Expected base package:

- Type: APK Bundle / APKM, not a standalone base APK
- Displayed size: 160.99 MB (168,814,164 bytes)
- Expected filename: `com.apple.android.music_6.5.0-1580_4arch_7dpi_e7097cc310a6e34d1c0e3dc70145ee78_apkmirror.com.apkm`
- SHA-256 verified automatically by the patcher: `18F1B7A0296FBF059D68509506729D6A291408BE43E3CE50060E15E5E8B2941A`

APKMirror is a third-party site and is not affiliated with this project. An identical package obtained from another lawful source can also be used; the patcher verifies its contents automatically.

## Build Profiles

| Profile | Recommended devices | Output |
|---|---|---|
| `tv-armv7` | 32-bit Android TV devices | ARMv7, all-density single APK |
| `tv-arm64` | 64-bit Android TV / Google TV devices | ARM64, all-density single APK |
| `car-armv7` | Older 32-bit Android in-vehicle systems | ARMv7, all-density immersive single APK |
| `car-arm64` | ARM64 in-vehicle systems, including compatible DiLink-class devices | ARM64, all-density immersive single APK |
| `tv-armv7-xhdpi` | Verified Sony TV configuration | ARMv7, xhdpi single APK |

The minimum supported OS is Android 11. Profiles are separated by CPU architecture and intended use, not by individual Android release.

Verified devices:

- `tv-armv7-xhdpi`: Sony K-85XR70
- `tv-armv7`: Sony K-85XR70
- `car-arm64`: basic functions verified on the development in-vehicle unit; other models remain unverified

All-density profiles include multiple DPI resource sets for different manufacturer configurations. A 4K television may still render the application at a 1920×1080 logical resolution and upscale it at the system compositor level.

## Installing the Patched APK

The patcher produces one APK. ADB is optional.

### TV installation using storage

1. Copy the TV APK from `dist` to a USB drive, the TV's internal storage, or a network share accessible from the TV.
2. Allow the chosen file manager to install unknown applications in the TV settings.
3. Open the APK in the file manager and follow the installation prompt.

### Optional network ADB installation

Use ADB when the TV has no suitable file manager or when you need detailed installation errors:

```powershell
$adb = Get-ChildItem ".\.local\toolchain\platform-tools" -Filter adb.exe -Recurse | Select-Object -First 1
& $adb.FullName connect "TV_IP:5555"
& $adb.FullName install "dist\AppleMusic-6.5.0-1580-tv-arm64-patched.apk"
```

The build wizard can also perform these steps. Enable developer options and network/ADB debugging on the TV, keep the computer and TV on the same network, and approve the first debugging request shown on the TV.

### In-vehicle installation

Copy the appropriate car APK to a USB drive or another location the system can access, then use the manufacturer's supported file manager or application installer. This project does not provide or bypass engineering passwords, system signatures, installation restrictions, or driving-safety controls. Do not install, debug, or operate the player while driving.

### Signature notice

The first build creates your private signing key in `.local\signing`. Back up that entire directory. Builds signed with the same key can update each other; losing it normally requires uninstalling the patched app before installing a newly signed build.

Because the local signature differs from Apple's official signature, the patched version normally cannot update the official app directly. Confirm that your account and offline downloads can be restored before manually uninstalling the official version. The patcher never uninstalls applications or clears device data automatically.

## Optional Command-Line Build

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\scripts\setup-toolchain.ps1
.\scripts\build.ps1 -InputApkm "D:\Downloads\AppleMusic-6.5.0.apkm" -Profile tv-arm64
```

The first setup downloads pinned versions of Temurin JDK, Android SDK components, APKEditor, and Apktool from their official release locations and verifies them automatically. Output is written to `dist`.

## What SHA-256 Means

SHA-256 is a file-integrity fingerprint, not a password, activation code, or account credential. You do not need to send it to the project maintainer or enter it manually. The wizard checks the base package automatically. The hash on the Release page is provided only for optional verification of the downloaded patcher ZIP.

## Features

- **Wired-network detection:** HOME, lyrics, and online search no longer depend solely on Wi-Fi or cellular connectivity checks.
- **Landscape HOME:** rearranges artwork/video, song information, progress, and playback controls.
- **Lyrics and queue:** retains the native player's data and button state in a landscape split layout.
- **Music videos:** attempts to reuse the native video output across player modes instead of replacing it with static artwork.
- **Touch and mouse input:** supports player buttons and a full-page swipe-down gesture; car profiles hide supported system bars.
- **Local signing:** the signing key and generated APK remain on your computer.

## Screenshots

All screenshots use a 16:9 aspect ratio and the same display width. Click an image to view the original.

### Standard Track — HOME

<p align="center"><img src="docs/screenshots/ordinary-home.png" alt="Standard track HOME" width="960"></p>

### Standard Track — Lyrics

<p align="center"><img src="docs/screenshots/ordinary-lyrics.png" alt="Standard track lyrics" width="960"></p>

### Standard Track — Queue

<p align="center"><img src="docs/screenshots/ordinary-queue.png" alt="Standard track queue" width="960"></p>

### Music Video — HOME

<p align="center"><img src="docs/screenshots/video-home.png" alt="Music video HOME" width="960"></p>

### Music Video — Queue

<p align="center"><img src="docs/screenshots/video-queue.png" alt="Music video queue" width="960"></p>

## Known Limitations

- This is a runtime landscape rearrangement of a phone UI, not an official Apple TV or automotive client.
- Extremely rapid transitions among HOME, lyrics, and queue may be less fluid than a native desktop client.
- When repeatedly switching between music videos and standard tracks, the first video frame can be delayed by network, decoder, or cache state.
- Tracks without lyrics, single-track playback, and newly rebuilt queues depend on native player callbacks; some devices may refresh button state late.
- Immersive mode depends on the manufacturer's System UI implementation. Some systems may retain a gesture region or briefly show system bars after a dialog.
- A re-signed build cannot normally update the official Apple-signed app. Uninstalling the official app removes its local data.

When reporting a problem, include the device model, Android version, CPU architecture, build profile, reproduction steps, and logs with account and device identifiers removed.

## Voluntary Support

The project remains free. Support unlocks no features and does not affect issue reports or updates.

- International: [Ko-fi](https://ko-fi.com/coolzoojoker)
- Additional options: [Voluntary support page](SUPPORT.md)

## Development and Release Safety

Run the release audit before publishing:

```powershell
.\scripts\audit-release.ps1
```

The patcher stops on input mismatches or missing target-code anchors. A new Apple Music version must be audited and adapted explicitly; validation checks must not be bypassed. See [SECURITY.md](SECURITY.md) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Development Note

The maintainer defined the requirements and interaction design and performed real-device testing on TV and in-vehicle hardware. Most of the code and documentation was written with assistance from OpenAI Codex. The project maintainer is responsible for final review, releases, and ongoing maintenance.

## License

Original code in this repository is licensed under Apache-2.0. Apple Music and third-party tools are outside the scope of this license.
