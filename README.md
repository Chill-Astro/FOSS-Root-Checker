<p align="center">
  <kbd>
  <img width="1920" height="1080" alt="Rooting Revolution" src="https://github.com/user-attachments/assets/fc8d67f6-df19-496b-9960-60786e22886d" />
  </kbd>
</p>

<div align="center">

FOSS Root Checker as the name suggests is an `Open Source` Root Checker app for verifying `Root Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them Root Access?

Well that's why I made this App! It is just a simple Root Checker for Newbies but with Transparency of what is done.

App Version : `v36.23.2.0`

Package ID : `foss.chillastro.root.checker`

Offline Release ID : `foss.chillastro.root.checker.offline` ( Lacks Update Checker )

Requirements : `Android 6 and Higher`

An Online Version of the App's Guide is given [here](https://github.com/Chill-Astro/Android-Rooting-Guide).

Waiting for Approval on Uptodown Store and IzzyonDroid.

_- TRUSTED SOURCES -_

<a href="https://github.com/Chill-Astro/FOSS-Root-Checker/releases/latest" target="_blank"><img src="https://img.shields.io/static/v1?label=%20&message=GitHub&color=FFFFFF&labelColor=000000&style=for-the-badge&logo=github&logoColor=FFFFFF" height="80" alt="GitHub"></a>
<a href="https://sourceforge.net/projects/foss-root-checker/" target="_blank"><img src="https://img.shields.io/static/v1?label=%20&message=SourceForge&color=EE7034&labelColor=000000&style=for-the-badge&logo=sourceforge&logoColor=EE7034" height="80" alt="SourceForge"></a>
<a href="https://appteka.store/apps/568r276436" target="_blank"><img src="https://img.shields.io/static/v1?label=%20&message=Appteka&color=2ECC71&labelColor=000000&style=for-the-badge&logo=android&logoColor=2ECC71" height="80" alt="Appteka"></a>

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroidButtonGreyBorder_nofont.png" height="80" alt="Get it at IzzyOnDroid">](https://apt.izzysoft.de/packages/foss.chillastro.root.checker.offline)

---

</div>

> [!IMPORTANT]
> This Project is not Affiliated with Google, Magisk, and other Open Source / Closed Source Referenced in this Project. Certain Brands mentioned are for General Information Only.

> [!WARNING]
> Do not trust sources listed in [this page](https://github.com/Chill-Astro/FOSS-Root-Checker/issues/1) and NEVER TRUST .ZIP FILES PROVIDED IN ANY FORKED / STOLEN REPOSITORY'S READMEs! They Bypass Github's Security Checks and may Compromise your System with Malware and Steal your ENTIRE Data!

---

## How it works?

> [!NOTE]
> All checks are performed on a background thread (`Dispatchers.IO`) to ensure your device remains responsive during the scan.

- If "Search Root" is pressed, it Checks 40+ System Paths to Find Traces of Root Access. If it finds, Root Traces are Found. If not it is clearly shown.
- Now if "Verify Root" is pressed the App Executes `su -c id` and if it returns 0, Root Access is Verified. Else, Root Access not Available.
- For BusyBox Checker, it checks BusyBox Specefix Paths. If not Found, the App executes `su -c which busybox` to verify BusyBox Installation.

---

## HALT ON DEVELOPMENT :

- This Project is now a "Complete Product" in my Vision. So I have no other Ideas.
  
--- 

## Key Features :

- Ultra Low Footprint of 1.55MB ONLY!
- Privacy First Design with full transperancy. ✅
- Reduced Animations Enforced on Low end Devices ( if RAM < 4 GB ). ✅
- No Ads, In-App Purchases and no Data Collection. ✅
- Modern Material UI with Monet Theming. ✅
- Support for Android 6+ Devices. ✅
- Works with Magisk, KernelSU, KSU Forks, APatch and older Methods ✅
- Thorough Guidance provided on Rooting and Unlocking Bootloader. ✅

---    

## Screenshots :

<div align="center">
<kbd>
 <img width="1920" height="1080" alt="Root Checker Preview" src="https://github.com/user-attachments/assets/b736eec2-f626-4b69-87b9-1698eee5bffc" />
</kbd>
</div>

---

## Screen Recording :

<div align="center">

https://github.com/user-attachments/assets/f5d7bd5e-6e76-47ff-a945-2cdacab0fba7

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

## HALL OF FAME 👍 :

// Will add Forked Repos which are genuinely good. 🤩 I will list everything Good about them.

---

## HALL OF NEUTRALITY 😐 :

// Will add Inactive Forks. Uh yeah that's it atleast it's Forking not Cloning! 😅

---

## HALL OF SHAME 👎 :

// Includes Clones who are working against the MIT Licence and Distributing Malware. All Flaws are mentioned. 😑

- ~~RuotianJoy/FOSS-Root-Checker ( Cloned + Distributing Malware 👎 )~~ ( Removed Successfully )

---

## ⚠️ IMPORTANT NOTICE ⚠️

Please be aware: There are fraudulent repositories on GitHub that are cloning this project's name and using AI-generated readmes, but they contain **completely random and unrelated files in each release**. These are NOT official versions of this project.

**ALWAYS ensure you are downloading or cloning this project ONLY from its official and legitimate source:**
`https://github.com/Chill-Astro/FOSS-Root-Checker`

Check [here](https://github.com/Chill-Astro/FOSS-Root-Checker/issues/1) for more details. I am trying my best to report these people.

---

## ⚠️ Smoking Gun for Danger :

<details>
<summary><b>View Details</b></summary>

**If your download contains any of the following, DELETE IT IMMEDIATELY:**

* **Suspicious Windows Executables:** Files ending in `.exe`, `.bat`, or `.dll` (e.g., `luau.exe`, `StartApp.bat`).
* **Compressed Archives:** This project is distributed as an **APK**, never as a `.zip` or `.7z` containing Windows binaries.
* **Hidden Scripts:** Text files like `asm.txt` used to execute malicious code on your PC.
* The Following Folder Structure is used by Malware (Shown in a VM) :

![Screenshot_2026-03-01-18-52-39-337_com clone android dual space](https://github.com/user-attachments/assets/be691c9f-7def-4e8b-982c-c7ca2e9a067d)

![Screenshot_2026-03-01-18-53-09-759_com clone android dual space](https://github.com/user-attachments/assets/1c75031d-95be-4716-9347-b762e3dad5b8)

</details>

---

## Credits :

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

## Note from Developer :

Appreciate my effort? Why not leave a Star ⭐ ! Also if forked, please credit me for my effort and thanks if you do! :)

---
