# --- UI & Framework (Compose Core Protection) ---
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keepattributes com.google.errorprone.annotations.DoNotMock

# --- Kotlin Coroutines (Prevents startup crashes) ---
-keep class kotlinx.coroutines.android.** { *; }

# --- Data Persistence ---
-keepclassmembers class ** {
    @androidx.room.* *;
    @kotlinx.serialization.SerialName <fields>;
}

# --- Root Detection & Hardware Logic ---
-keep class foss.chillastro.root.checker.HardwareProbe { *; }
-keepclassmembers class foss.chillastro.root.checker.** {
    *** isTrashDevice(...);
    *** isSUWorking(...);
    *** findBusyBoxPath*(...);
    *** saveLog(...);
    *** getLogs(...);
}

# --- Compose & Kotlin Metadata ---
-keep class kotlin.Metadata { *; }

# --- Prevents "Method Not Found" in Serialization ---
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature
-keepclassmembernames class kotlinx.serialization.json.** { *; }

# --- Hardware & Shell Exec Extras ---
-keep class java.lang.Runtime { *; }
-keep class java.lang.Process { *; }
-keep class java.lang.ProcessBuilder { *; }
-keep class java.io.File { *; }

# --- Debugging & Stacktraces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile