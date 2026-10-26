@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
package foss.chillastro.root.checker

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.system.exitProcess
import foss.chillastro.root.checker.ui.theme.AppShapes
import foss.chillastro.root.checker.ui.theme.LocalMonochrome

// Settings branding and its hidden tap interactions live together to keep state local.
@Composable
fun EasterEggsHeader() {
    val ctx = LocalContext.current
    val noRipple = remember { MutableInteractionSource() }

    var showPoem by rememberSaveable { mutableStateOf(false) }
    var bTaps by rememberSaveable { mutableIntStateOf(0) }
    var vTaps by rememberSaveable { mutableIntStateOf(0) }
    var logoTaps by rememberSaveable { mutableIntStateOf(0) }


    val appVersion = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            }
        } catch (_: Exception) { "37.23.3.0" }
    }

    if (showPoem) { // God comes with Dedication and so does FREEDOM. So tap 108 times ig for an Easter egg!
        BasicAlertDialog(onDismissRequest = { showPoem = false }, properties = DialogProperties(dismissOnClickOutside = false)) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.widthIn(max = 400.dp).padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFFFFFFF), Color(0xFF128807))
                                ),
                                shape = MaterialTheme.shapes.large
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\"यूनान-ओ-मिस्र-ओ-रूमा, सब मिट गए जहाँ से\nअब तक मगर है बाक़ी, नाम-ओ-निशाँ हमारा\nकुछ बात है कि हस्ती मिटती नहीं हमारी\nसदियों रहा है दुश्मन दौर-ए-ज़माँ हमारा\"",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showPoem = false; logoTaps = 0 },
                        shapes = expressiveButtonShapes(MaterialTheme.shapes.medium)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    val accent = MaterialTheme.colorScheme.primary
    val softAccent = MaterialTheme.colorScheme.primaryContainer
    val logoBackground = if (accent.luminance() >= softAccent.luminance()) accent else softAccent
    Surface(
        shape = AppShapes.root,
        color = if (LocalMonochrome.current) Color.White else logoBackground,
        modifier = Modifier.size(220.dp).clickable(indication = null, interactionSource = noRipple) {
            logoTaps++
            when (logoTaps) { // The following lines are by Honourable Netaji Subhas Chandra Bose, the Man who sacrificed everything for his Motherland!
                5 -> Toast.makeText(ctx, "It is our duty to pay for our liberty...", Toast.LENGTH_SHORT).show()
                10 -> Toast.makeText(ctx, "...with our own blood.", Toast.LENGTH_SHORT).show()
                15 -> Toast.makeText(ctx, "Give me blood, and I will give you freedom!", Toast.LENGTH_LONG).show()
                20 -> Toast.makeText(ctx, "Delhi Chalo!", Toast.LENGTH_SHORT).show()
                25 -> Toast.makeText(ctx, "Success always stands on the pillars of failure.", Toast.LENGTH_SHORT).show()
                30 -> Toast.makeText(ctx, "No real change in history has ever been achieved...", Toast.LENGTH_LONG).show()
                35 -> Toast.makeText(ctx, "...by discussions alone.", Toast.LENGTH_LONG).show()
                40 -> Toast.makeText(ctx, "One individual may die for an idea,", Toast.LENGTH_LONG).show()
                45 -> Toast.makeText(ctx, "but that idea will, after his death,", Toast.LENGTH_LONG).show()
                50 -> Toast.makeText(ctx, "incarnate itself in a thousand lives.", Toast.LENGTH_LONG).show()
                55 -> Toast.makeText(ctx, "Freedom is not given, it is taken.", Toast.LENGTH_SHORT).show()
                80 -> Toast.makeText(ctx, "So you have a lot of dedication!", Toast.LENGTH_SHORT).show()
                103 -> Toast.makeText(ctx, "5!", Toast.LENGTH_SHORT).show()
                104 -> Toast.makeText(ctx, "4!", Toast.LENGTH_SHORT).show()
                105 -> Toast.makeText(ctx, "3!", Toast.LENGTH_SHORT).show()
                106 -> Toast.makeText(ctx, "2!", Toast.LENGTH_SHORT).show()
                107 -> Toast.makeText(ctx, "1!", Toast.LENGTH_SHORT).show()
                108 -> {
                    Toast.makeText(ctx, "वन्दे मातरम्!", Toast.LENGTH_SHORT).show() // From Anand Math by Bankim Chandra Chatterjee
                    showPoem = true // Tarana-e-Milli by Muhammad Iqbal. Patriotism!
                    logoTaps = 0
                }
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.root_logo),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(160.dp)
            )
        }
    }
    Text(
        text = buildAnnotatedString { append("Developer: "); withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Chill-Astro Software") } },
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 16.dp).clickable(indication = null, interactionSource = noRipple) {
            if (++bTaps == 5) {
                Toast.makeText(ctx, "Chill-Astro Software - TRANSPARENT BY DESIGN", Toast.LENGTH_SHORT).show() // Ay why not check out my other FOSS Projects!
                bTaps = 0
            }
        }
    )

    Text(
        text = buildAnnotatedString { append("Version: "); withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(appVersion) } },
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable(indication = null, interactionSource = noRipple) {
            vTaps++
            if (vTaps == 5) Toast.makeText(ctx, "Hi there! You Found me. :)", Toast.LENGTH_SHORT).show()
            if (vTaps == 10) Toast.makeText(ctx, "I hope you like the App! ^_^", Toast.LENGTH_LONG).show()
            if (vTaps == 15) Toast.makeText(ctx, "Ok now you are just poking me....", Toast.LENGTH_LONG).show()
            if (vTaps == 25) Toast.makeText(ctx, "Ok its not funny. Now its hurting my screen!", Toast.LENGTH_LONG).show()
            if (vTaps == 50) Toast.makeText(ctx, "Does Tapping give you anything?", Toast.LENGTH_LONG).show()
            if (vTaps == 75) Toast.makeText(ctx, "Are you a Human Autoclicker?", Toast.LENGTH_LONG).show()
            if (vTaps == 100) Toast.makeText(ctx, "Or maybe you ARE an Autoclicker?", Toast.LENGTH_LONG).show()
            if (vTaps >= 150) {
                Toast.makeText(ctx, "Touch Some Grass! 🌿", Toast.LENGTH_LONG).show()
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ exitProcess(0) }, 1500)
            }
        }
    )
}
