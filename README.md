<p align="center">
  <img src="https://github.com/Chill-Astro/FOSS-Root-Checker/blob/master/logo-nobg.png" width="128px" height="128px" alt="ROOT Checker">
</p>
<h1 align="center">FOSS Root Checker</h1>

<div align="center">

FOSS ROOT Checker as the name suggests is an `Open Source` Root Checker app for verifying `Root Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them Root Access?

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
- No Ads, In-App Purchases and no Data Collection. ✅
- Modern Material UI. ✅
- Support for Android 10+ Devices. ✅
- Works with Magisk, KernelSU, APatch and any other method. ✅
- Thorough Guidance provided on Rooting and Unlocking Bootloader. ✅

---    

## Preview :

<div align="center"> ---- Demonstrated on POCO C55 ( NON-ROOTED ) ---- </div>
<br>
<div align="center">
<img src="https://github.com/user-attachments/assets/0ef0d1eb-ce62-42d5-931e-ee83d9303318" width="300px">
<img src="https://github.com/user-attachments/assets/90844668-70ae-4bda-a48c-29adba4b156d" width="300px">
<img src="https://github.com/user-attachments/assets/aadd307a-fb67-47aa-a7ee-44da54a94b6a" width="300px">
<img src="https://github.com/user-attachments/assets/bfd0329c-5787-4fef-bb01-f8e14e3d256c" width="300px">
<img src="https://github.com/user-attachments/assets/390223f5-eba9-4e7e-ac19-79e929f2f672" width="300px">  
<img src="https://github.com/user-attachments/assets/03fbd8f8-c67b-4ce9-b8f5-ced33eee117b" width="300px">    
</div>
<br>
<div align="center"> ---- Demonstrated on Realme C11 2020 ( ROOTED ) ---- </div>
<br>
<div align="center">
<img src="https://github.com/user-attachments/assets/dd2633ab-2376-4952-9e72-543fdb57fdca" width="300px">
<img src="https://github.com/user-attachments/assets/07a71582-d11f-475b-ba71-6531672d774a" width="300px">    
<img src="https://github.com/user-attachments/assets/35602dc6-f486-43c6-8fe2-2954a50e7f87" width="300px">
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
