<p align="center">
  <img src="https://github.com/Chill-Astro/FOSS-Root-Checker/blob/master/logo-nobg.png" width="128px" height="128px" alt="ROOT Checker">
</p>
<h1 align="center">FOSS Root Checker</h1>

<div align="center">

FOSS Root Checker as the name suggests is an `Open Source` Root Checker app for verifying `Root Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them Root Access?

Well that's why I made this App! It is just a simple Root Checker for Newbies but with Transparency of what is done.

App Version : `v36.27.1.0`

Package ID : `foss.chillastro.root.checker`

Requirements : `Android 8.1 and Higher`

Currently in Development. Releasing on March 26th 2026! 🌟

To be Released on UptoDown Store, APKPure and FDroid.

</div>

> [!NOTE]
> Also ensure to Download this APK only from Trusted Sources such as FDroid, UptoDown, Appteka (thanks for testing my Pre-Release Builds) and so on.

> [!WARNING]
> Do not trust sources listed in [this page](https://github.com/Chill-Astro/FOSS-Root-Checker/issues/1) and NEVER TRUST .ZIP FILES PROVIDED IN ANY FORKED / STOLEN REPOSITORY'S READMEs! They Bypass Github's Security Checks and may Compromise your System with Malware and Steal your ENTIRE Data!

---

## How it works? 

> [!NOTE]
> All checks are performed on a background thread (`Dispatchers.IO`) to ensure your device remains responsive during the scan.

- The App First Checks 40+ System Paths to Find Traces of SU Binaries to Verify Root Access. If it finds, Root Acess is Verified. If not it moves onto the next step.
- Now the App Executes `su -c id` and if it returns 0, Root Access is Verified. Else, Root Access not Available.
- For BusyBox Checker, it checks BusyBox Specefix Paths. If not Found, the App executes `su -c which busybox` to verify BusyBox Installation.

---

## Key Features :

- Ultra Small Footprint. The .APK is 1.54MB ONLY! ✅
- Privacy First Design with full transperancy. ✅
- No Ads, In-App Purchases and no Data Collection. ✅
- Modern Material UI with Monet Theming. ✅
- Support for Android 8.1+ Devices. ✅
- Works with Magisk, KernelSU, KSU Forks, APatch and older Methods ✅
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

## ⚠️ Smoking Gun for Danger :

> [!CAUTION]
> **MALWARE ALERT:** If your downloaded folder looks like the images below, **DO NOT OPEN** any files. Format the drive or delete the folder immediately. Official releases are ONLY `.apk` files.

<details>
<summary><b>View Details</b></summary>

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

## Note from Developer :

Appreciate my effort? Why not leave a Star ⭐ ! Also if forked, please credit me for my effort and thanks if you do! :)

---
