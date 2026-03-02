# --- UI & Framework ---
-keep class androidx.compose.runtime.** { *; }
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

# --- Debugging & Stacktraces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile