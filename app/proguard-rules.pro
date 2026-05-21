# --- UI & Framework ---
-dontwarn androidx.compose.runtime.**
-keep class android.os.Build { *; }

# --- Kotlin Coroutines (Prevents startup crashes) ---
-keep class kotlinx.coroutines.android.** { *; }

# --- Data Persistence ---
-keepclassmembers class ** {
    @androidx.room.* *;
    @kotlinx.serialization.SerialName <fields>;
}

# --- Root Detection Logic (The Critical Part) ---
-keep class foss.chillastro.root.checker.HardwareProbe { *; }
-keepclassmembers class foss.chillastro.root.checker.** {
    *** isSUWorking(...);
    *** findBusyBoxPath*(...);
    *** saveLog(...);
    *** getLogs(...);
}

# --- Compose & Kotlin Metadata ---
-keep class kotlin.Metadata { *; }
-keep class androidx.compose.ui.platform.** { *; }

# --- Prevents "Method Not Found" in Serialization ---
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature
-keepclassmembernames class kotlinx.serialization.json.** { *; }

# --- Hardware & Root Extras ---
-keep class android.content.pm.PackageManager { *; }
-keep class java.lang.Process { *; }
-keep class java.io.File { *; }

# --- Debugging & Stacktraces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile