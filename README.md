<p align="center">
  <img src="https://github.com/Chill-Astro/FOSS-Root-Checker/blob/master/logo-nobg.png" width="128px" height="128px" alt="ROOT Checker">
</p>
<h1 align="center">FOSS ROOT Checker</h1>

<div align="center">

FOSS ROOT Checker as the name suggests is an `Open Source` Root Checker app for verifying `ROOT Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them ROOT Access?

Well that's why I made this App! It is just a simple Root Checker for Newbies but with Transparency of what is done.

App Version : `v36.29.1.0`

Package ID : `foss.chillastro.su`

Requirements : `Android 10 and Higher`

Currently in Development. Releasing in March! 🌟

To be Released on Amazon App Store, UptoDown Store, APKPure and FDroid.

</div>

---

## How it works? 

The **FOSS Root Checker** employs a dual-layered verification strategy to determine system integrity without compromising your privacy:

1. **Functional Execution Check:** The app attempts to spawn a shell process to run the `su -c id` command. If the system returns a User ID of `0`, the app confirms that active SuperUser execution privileges are granted.
2. **Filesystem Signature Scan:** As a secondary fallback, the app performs a manual search through high-priority system paths (such as `/system/xbin/`, `/sbin/`, and `/data/local/`) for the presence of a standalone `su` binary.

By combining these methods, the app accurately detects root access across both legacy environments (Android 9/10) and modern implementations like **Magisk**, **KernelSU**, and **APatch**. All checks are performed on a background thread (`Dispatchers.IO`) to ensure your device remains responsive during the scan.

---

## Key Features :

- Privacy First Design with full transperancy. ✅
- Modern Material UI. ✅
- Support for Android 9+ Devices. ✅
- Works with Magisk, KernelSU, APatch and any other method. ✅
- Guidance provided on Rooting. ✅

---    

## Preview :

<div align="center"> ---- Demonstrated on POCO C55 ( NON-ROOTED ) ---- </div>
<br>
<div align="center">
<img src="https://github.com/user-attachments/assets/bdd4da2d-d82a-4c81-aa8c-4677cd87908f" width="300px">
<img src="https://github.com/user-attachments/assets/034caf58-371b-4c5b-bd8f-2e5fa19b7c06" width="300px">
<img src="https://github.com/user-attachments/assets/6f7b9b41-0821-46d9-a206-f2a4b3330efe" width="300px">
</div>
<br>
<div align="center"> ---- Demonstrated on Realme C11 2020 ( ROOTED ) ---- </div>
<br>
<div align="center">
<img src="https://github.com/user-attachments/assets/f0060140-b62c-4631-8813-2f436faccf8e" width="300px">
<img src="https://github.com/user-attachments/assets/2f9465e7-b452-4b98-b873-894d931e7ccf" width="300px">
</div>
<br>

---

## ⚠️ IMPORTANT NOTICE ⚠️

Please be aware: There are fraudulent repositories on GitHub that are cloning this project's name and using AI-generated readmes, but they contain **completely random and unrelated files in each release**. These are NOT official versions of this project.

**ALWAYS ensure you are downloading or cloning this project ONLY from its official and legitimate source:**
`https://github.com/Chill-Astro/FOSS-Root-Checker`

I am trying my best to report these people.

---

## Credits :

- [Magisk by @topjohnwu](https://github.com/topjohnwu/Magisk) : For Rooting pretty much anything these days.
- [KernelSU by @tiann](https://github.com/tiann/KernelSU) : For Kernel-Level Rooting on GKI Devices.
- [APatch by @bmax121](https://github.com/bmax121/APatch) : For Easy Kernel-Level Rooting.
- [mtkclient by @bkerler](https://github.com/bkerler/mtkclient) : For allowing MTK Devices to be Rooted Easily ( Including my Phone ).

## Note from Developer :

Appreciate my effort? Why not leave a Star ⭐ ! Also if forked, please credit me for my effort and thanks if you do! :)

---
