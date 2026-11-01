<p align="center">
  <kbd>
  <img width="1920" height="1080" alt="Root Checker Promo" src="https://chill-astro.github.io/assets/images/root-checker-hero.webp" />
  </kbd>
</p>

<div align="center">

FOSS Root Checker is an **Open-Source** ***Root Checker*** **ANDROID APP** for **Verifying Superuser Access** and **Busybox Verification** for Magisk, KernelSU, APatch & Older Methods on Mobile Devices.

**App Version** : `v37.24.4.1`

**Codename** : `FRC-SU`

**Requirements** : `Android 7 and Higher`

An Online Version of the App's Guide is given [here](https://github.com/Chill-Astro/Android-Rooting-Guide).

_- TRUSTED SOURCES -_

| Download Channels | Get the App |
| :--- | :--- |
| **GitHub** (Both) | <a href="https://github.com/Chill-Astro/FOSS-Root-Checker/releases/latest" target="_blank"><img src="https://img.shields.io/static/v1?label=%20&message=GitHub&color=FFFFFF&labelColor=000000&style=for-the-badge&logo=github&logoColor=FFFFFF" height="40" alt="GitHub"></a> |
| **Appteka** (Official) | <a href="https://appteka.store/app/3c8r334644" target="_blank"><img src="https://img.shields.io/static/v1?label=%20&message=Appteka&color=2ECC71&labelColor=000000&style=for-the-badge&logo=android&logoColor=2ECC71" height="40" alt="Appteka"></a> |
| **IzzyOnDroid** (Offline) | [<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroidButtonGreyBorder_nofont.png" height="40" alt="Get it at IzzyOnDroid">](https://apt.izzysoft.de/packages/foss.chillastro.root.checker.offline) |
| **Uptodown** (Official) | <a href='https://foss-chillastro-root-checker.en.uptodown.com/android' title='Download Root Checker' ><img src='https://stc.utdstc.com/img/mediakit/download-gio-big.png' height="40" alt='Download Root Checker'></a> |
| **OpenAPK** (Both)| <a href="https://www.openapk.net/root-checker/foss.chillastro.root.checker.offline/"><img src="https://www.openapk.net/images/openapk-badge.png" height="40" alt="OpenAPK.net"></a> |

---

</div>

> [!IMPORTANT]
> This Project is not Affiliated with Google, Magisk, and other Open Source / Closed Source Referenced in this Project. Certain Brands mentioned are for General Information Only.

---

## Uh, Why are there 2 Releases?

For Privacy of course! Let me break this down in simple terms :

- **Official** ( `foss.chillastro.root.checker` ) : Has sensitive permissions like `INTERNET`, `STORAGE` and `SELF-INSTALLING APPS` alongside `Superuser`. This facilitates the `Self-Updater` Feature!  

- **Offline** ( `foss.chillastro.root.checker.offline` ) : If you are afraid of Internet, this **LACKS** the `Self-Updater`! Enjoy Privacy! :)

---

## How it works? 

> [!NOTE]
> All checks are performed on a background thread (`Dispatchers.IO`) to ensure your device remains responsive during the scan.

- If "Search Root" is pressed, she Checks 40+ System Paths to Find Traces of Root Access. If it finds, Root Traces are Found. If not it is clearly shown.
- Now if "Verify Root" is pressed the App Executes `su -c id` and if it returns 0, Root Access is Verified. Else, Root Access not Available.
- For BusyBox Checker, she checks BusyBox Specefix Paths. If not Found, the App executes `su -c which busybox` to verify BusyBox Installation.

---

## Key Features :

- Ultra Low Footprint of ~2MB! ✅
- Privacy First Design with full transparency. ✅
- Reduced Animations Enforced on Low end Devices ( if RAM < 4 GB ). ✅
- No Ads, In-App Purchases and no Data Collection. ✅
- Modern Material UI with Monet Theming. ✅
- Support for Android 7+ Devices. ✅
- Monochrome Mode for Minimalists. ✅
- Works with Magisk, KernelSU, KSU Forks, APatch and older Methods ✅
- Thorough Guidance provided on Rooting and Unlocking Bootloader. ✅

---    

## Why is this at v37?! :

<div align="center">

<H2>

v`37`.`24`.`4`.`1`

</H2>

</div>

- `37` -> Target Android API Level
- `24` -> Minimum Android Version to Install App
- `4` -> Major Update
- `1` -> Minor Update

---

## Screenshots :

<div align="center">
<kbd>
 <img width="1920" height="1080" alt="Root Checker Preview" src="https://chill-astro.github.io/assets/images/root-checker-preview.webp" />
</kbd>
</div>

---

## Screen Recording :

<div align="center">

https://github.com/user-attachments/assets/0e689ce8-6e08-46a0-beb4-00703fd31ff8

</div>

---


## TRAILER on @delta-v-sfs :

<div align="center">
  <kbd>
  <a href="https://www.youtube.com/watch?v=ikYPs2zk7Yc">
    <img src="https://img.youtube.com/vi/ikYPs2zk7Yc/maxresdefault.jpg" alt="Watch the FOSS Root Checker Trailer"style="border-radius:10px;">
    <br>
    <b><h3>▶ Click to Watch the Official Trailer</h3></b>
  </a>
</kbd>
</div>

---

## Building from Source ( With Android Studio ) :

STEP 1 : Get Android Studio from [here](https://developer.android.com/studio)

STEP 2 : Clone this Repository 

    git clone https://github.com/Chill-Astro/FOSS-Root-Checker.git

STEP 3 : Open this Directory in Android Studio.

STEP 4 : Hit Build > Generate App Bundles or APKs > Generate APK as shown.

<kbd>
  <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/8a7a12e1-c9ca-4fc9-94d1-ac8ddf062380" />
</kbd>

Alternatively Hit Build > Signed App Bundle or Apk. You will have to provide your OWN Certificate.

This method is RECOMMENDED as this is how I build myself.

---

## Credits :

<details>

<summary>Very Long List 😴</summary>

- [Magisk by @topjohnwu](https://github.com/topjohnwu/Magisk) : For Rooting pretty much anything these days.
- [KernelSU by @tiann](https://github.com/tiann/KernelSU) : For Kernel-Level Rooting on GKI Devices.
- [APatch by @bmax121](https://github.com/bmax121/APatch) : For Easy Kernel-Level Rooting.
- [mtkclient by @bkerler](https://github.com/bkerler/mtkclient) : For allowing MTK Devices to be Rooted Easily ( Including my Phone ).
- [Shamiko by @LSPosed](https://github.com/LSPosed/LSPosed.github.io/releases/) : For hiding root traces and faking bootloader status.
- [Tricky Store by @5ec1cff](https://github.com/5ec1cff/TrickyStore) : For spoofing Hardware Backed Attestation.
- [Tricky Addon by @KOWX712](https://github.com/KOWX712/Tricky-Addon-Update-Target-List) : For making the Tricky Store process accessible via WebUI.
- [Zygisk Next by @Dr-TSNG](https://github.com/Dr-TSNG/ZygiskNext) : For providing a standalone Zygisk implementation.
- [ReZygisk by @PerformanC](https://github.com/PerformanC/ReZygisk) : For an alternative Zygisk implementation.
- [Zygisk Assistant by @snake-4](https://github.com/snake-4/Zygisk-Assistant) : For helping hide Zygisk from detection.
- [Play Integrity Fix by @KOWX712](https://github.com/KOWX712/PlayIntegrityFix) : For maintaining Google Play Integrity standards.
- [Play Integrity Fork by @osm0sis](https://github.com/osm0sis/PlayIntegrityFork) : For the widely used community fork of the integrity fix.
- [TEESimulator by @JingMatrix](https://github.com/JingMatrix/TEESimulator) : An open-source alternative for TEE spoofing.
- [NoHello by @MhmRdd](https://github.com/MhmRdd/NoHello) : An open-source alternative for hiding root.
- [Tricky Store OSS by @beakthoven](https://github.com/beakthoven/TrickyStoreOSS) : For providing an open-source version of Tricky Store.
- [KernelSU Next by @KernelSU-Next](https://github.com/KernelSU-Next/KernelSU-Next) : For the continued development and community fork of KSU.
- [SkiSU Ultra by @SkiSU-Ultra](https://github.com/SkiSU-Ultra/SkiSU-Ultra) : For providing specialized kernel-level rooting features.
- [TWRP & OrangeFox](https://twrp.me/) : For the custom recoveries that make flashing these modules possible.

</details>

## Note from Developer :

A Star ⭐ will be Appreciated to Support this Project! :)

---
