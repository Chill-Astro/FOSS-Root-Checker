<p align="center">
  <kbd>
  <img src="https://github.com/user-attachments/assets/303643a8-6518-408c-9447-81dc9be7f291" width="1920px" height="1080px" alt="Root Checker Promo"> 
  </kbd>
</p>

<div align="center">

FOSS Root Checker as the name suggests is an `Open Source` Root Checker app for verifying `Root Access` on Android Mobile Devices. Ever wondered what Root Checker Apps do behind the scenes on your Phones once you give them Root Access?

Well that's why I made this App! It is just a simple Root Checker for Newbies but with Transparency of what is done.

App Version : `v36.23.1.0`

Package ID : `foss.chillastro.root.checker`

Requirements : `Android 6 and Higher`

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

- If "Search Root" is pressed, it Checke 40+ System Paths to Find Traces of Root Access. If it finds, Root Traces are Found. If not it is clearly shown.
- Now if "Verify Root" is pressed the App Executes `su -c id` and if it returns 0, Root Access is Verified. Else, Root Access not Available.
- For BusyBox Checker, it checks BusyBox Specefix Paths. If not Found, the App executes `su -c which busybox` to verify BusyBox Installation.

---

## Key Features :

- Ultra Low Footprint of 1.54MB ONLY!
- Privacy First Design with full transperancy. ✅
- Reduced Animations Enforced on Low end Devices ( if RAM < 4 GB ). ✅
- No Ads, In-App Purchases and no Data Collection. ✅
- Modern Material UI with Monet Theming. ✅
- Support for Android 6+ Devices. ✅
- Works with Magisk, KernelSU, KSU Forks, APatch and older Methods ✅
- Thorough Guidance provided on Rooting and Unlocking Bootloader. ✅

---    

## Preview :

// Will add Screen Recording of the App in Action, after Release!

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

Alternatively Hit Build > Signed App Bundle or Apk. You will have to provide your OWN Certificate ( As mine is Private ).

This method is RECOMMENDED as this is how I build myself.

---

## Building from Source ( Without Android Studio ) :

STEP 1 : Install Prerequisites 
  - JDK 17 (Java Development Kit)
  - Command Line tools from [here](https://developer.android.com/tools)
    
STEP 2: Set Environment Variables : Set your ANDROID_HOME environment variable to point to your SDK location.

  - Linux/macOS:

        export ANDROID_HOME=$HOME/Android/Sdk

  - Windows: Set ANDROID_HOME in System Environment Variables to `C:\Users\<Username>\AppData\Local\Android\Sdk`

STEP 3 : Clone this Repository 

    git clone https://github.com/Chill-Astro/FOSS-Root-Checker.git
    cd FOSS-Root-Checker

STEP 4 : Build 

  - Linux / MacOS :

        ./gradlew assembleDebug
    
  - Windows :

        gradlew.bat assembleDebug

Alternatively for Release Builds :

STEP 4 : Create a Certificate ( Modify this as needed ) :

        keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias

STEP 5: Configure Environment Variables ( Modify as Needed ) 

  RELEASE_STORE_FILE=path/to/your/my-release-key.jks

  RELEASE_STORE_PASSWORD=your_keystore_password

  RELEASE_KEY_ALIAS=my-alias

  RELEASE_KEY_PASSWORD=your_key_password        

STEP 6 : Build 

  - Linux / MacOS :

        ./gradlew assembleRelease
    
  - Windows :

        gradlew.bat assembleRelease

---

## HALL OF FAME 👍 : 

// Will add Forked Repos which are genuinely good. 🤩 I will list everything Good about them.

---

## HALL OF NEUTRALITY 😐 :

// Will add Inactive Forks. Uh yeah that's it atleast it's Forking not Cloning! 😅

---

## HALL OF SHAME 👎 :

// Includes Clones who are working against the MIT Licence and Distributing Malware. All Flaws are mentioned. 😑

- RuotianJoy/FOSS-Root-Checker ( Cloned + Distributing Malware 👎 )

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

## Note from Developer :

Appreciate my effort? Why not leave a Star ⭐ ! Also if forked, please credit me for my effort and thanks if you do! :)

---
