# ====================================================================
# 1. CORE OPTIMIZATIONS & SHRINKING
# ====================================================================

# Enable maximum compression passes to strip dead space
-optimizationpasses 5

# Keep standard debugging attributes for useful crash stack traces
-keepattributes SourceFile, LineNumberTable, Signature, *Annotation*, InnerClasses, EnclosingMethod
-renamesourcefileattribute SourceFile

# ====================================================================
# 2. APPLICATION LOGIC (Your Root Checker Details)
# ====================================================================

# Keep your main probing utility class intact so reflection or logic handles don't break
-keep class foss.chillastro.root.checker.HardwareProbe { *; }

# Keep specific method names that you might call via strings or rely on for log lookups
-keepclassmembers class foss.chillastro.root.checker.** {
    *** isTrashDevice(...);
    *** isSUWorking(...);
    *** findBusyBoxPath*(...);
    *** saveLog(...);
    *** getLogs(...);
}

# Protect SharedPreferences/serialization field mappings if you use data models
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# ====================================================================
# 3. JETPACK COMPOSE & COROUTINES (Rely on library-shipped defaults)
# ====================================================================

# Do NOT keep whole androidx.compose packages. The compiler injects its own rules.
# We only add a fallback to suppress harmless warning noise during compilation.
-dontwarn androidx.compose.**
-dontwarn kotlinx.coroutines.**

# Keep errorprone check attributes without keeping the underlying framework classes
-keepattributes com.google.errorprone.annotations.DoNotMock