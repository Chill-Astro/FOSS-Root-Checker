<p align="center">
  <img src="https://github.com/Chill-Astro/FOSS-Root-Checker/blob/master/logo-nobg.png" width="128px" height="128px" alt="ROOT Checker">
</p>
<h1 align="center">FOSS Root Checker</h1>

<div align="center">

FOSS Root Checker as the name suggests is an `Open Source` Root Checker app for verifying `Root Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them Root Access?

Well that's why I made this App! It is just a simple Root Checker for Newbies but with Transparency of what is done.

App Version : `v36.29.1.0`

Package ID : `foss.chillastro.su`

Requirements : `Android 10 and Higher`

Currently in Development. Releasing on March 26th 2026! 🌟

To be Released on UptoDown Store, APKPure and FDroid.

NOTE : FOSS Root Checker is Designed to be Bleeding Edge for very modern devices. In case of any Lag, I apologise.... I have tested on really bad hardware so uh, "Works for me!"

Also ensure to Download this APK only from Trusted Sources such as FDroid, UptoDown, Appteka (thanks for testing my Pre-Release Builds) and so on. Do not trust sources listed in [here](https://github.com/Chill-Astro/FOSS-Root-Checker/issues/1) and NEVER TRUST .ZIP FILES PROVIDED IN ANY FORKED / STOLEN REPOSITORY'S READMEs! They Bypass Github's Security Checks and may Compromise your System with Malware and Steal your ENTIRE Data! 

</div>

---

## How it works? 

// Outdated information. To be Modified as it's Dual Path now.

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

// Will Update to include Screenshots only from Rooted POCO C55 (earth) as idk it looks cleaner + No repetitive screenshots. For old Screenshots, check commits.

// Will add Screen Recording of the App in Action, after Release!
---

## Building from Source :

To be done after Release.

---

## HALL OF FAME 👍 : 

// Will add Forked Repos which are genuinely good. 🤩 I will list everything Good about them.

---

## HALL OF NEUTRALITY 😐 :

// Will add Inactive Forks. Uh yeah that's it atleast it's Forking not Cloning! 😅

---

## HALL OF SHAME 👎 :

// Includes Clones who are working against the MIT Licence and Distributing Malware. All Flaws are mentioned. 😑

- RuotianJoy/FOSS-Root-Checker (Cloned + Distributing Malware 👎 )

---

## ⚠️ IMPORTANT NOTICE ⚠️

Please be aware: There are fraudulent repositories on GitHub that are cloning this project's name and using AI-generated readmes, but they contain **completely random and unrelated files in each release**. These are NOT official versions of this project.

**ALWAYS ensure you are downloading or cloning this project ONLY from its official and legitimate source:**
`https://github.com/Chill-Astro/FOSS-Root-Checker`

Check [here](https://github.com/Chill-Astro/FOSS-Root-Checker/issues/1) for more details. I am trying my best to report these people.

---

## Credits :

- [Magisk by @topjohnwu](https://github.com/topjohnwu/Magisk) : For Rooting pretty much anything these days.
- [KernelSU by @tiann](https://github.com/tiann/KernelSU) : For Kernel-Level Rooting on GKI Devices.
- [APatch by @bmax121](https://github.com/bmax121/APatch) : For Easy Kernel-Level Rooting.
- [mtkclient by @bkerler](https://github.com/bkerler/mtkclient) : For allowing MTK Devices to be Rooted Easily ( Including my Phone ).

## Note from Developer :

Appreciate my effort? Why not leave a Star ⭐ ! Also if forked, please credit me for my effort and thanks if you do! :)

---
