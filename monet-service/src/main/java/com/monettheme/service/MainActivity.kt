@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.monettheme.service

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.expressive.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.monettheme.api.ThemeColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val engine by lazy { MonetEngine(this) }
    private val _themeState = MutableStateFlow<ThemeColors?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val theme by _themeState.collectAsStateWithLifecycle()
            val isDark = (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

            LaunchedEffect(isDark) {
                refreshTheme(isDark)
            }

            theme?.let { colors ->
                MonetServiceTheme(seedColor = Color(colors.seedColor), darkTheme = colors.isDarkTheme) {
                    ServiceScreen(
                        colors = colors,
                        onRefresh = { refreshTheme(colors.isDarkTheme) },
                        onToggleTheme = { refreshTheme(!colors.isDarkTheme) }
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ExpressiveCircularProgressIndicator()
                }
            }
        }
    }

    private fun refreshTheme(dark: Boolean) {
        lifecycleScope.launch {
            val colors = withContext(Dispatchers.Default) {
                engine.generateFromWallpaper(dark)
            }
            _themeState.update { colors }
        }
    }
}

@Composable
fun ServiceScreen(
    colors: ThemeColors,
    onRefresh: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            ExpressiveTopAppBar(
                title = { Text("Monet Theme Service", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = scheme.primary,
                    titleContentColor = scheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExpressiveFloatingActionButton(
                onClick = onRefresh,
                containerColor = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer
            ) {
                Text("⟳", fontSize = 24.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(colors, onToggleTheme)

            Text("Primary 族", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ColorRow(
                listOf(
                    Triple("Primary", Color(colors.primary), Color(colors.onPrimary)),
                    Triple("On Primary", Color(colors.onPrimary), Color(colors.primary)),
                    Triple("Container", Color(colors.primaryContainer), Color(colors.onPrimaryContainer))
                )
            )

            Text("Secondary 族", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ColorRow(
                listOf(
                    Triple("Secondary", Color(colors.secondary), Color(colors.onSecondary)),
                    Triple("Container", Color(colors.secondaryContainer), Color(colors.onSecondaryContainer))
                )
            )

            Text("Tertiary / Error / Surface", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ColorRow(
                listOf(
                    Triple("Tertiary", Color(colors.tertiary), Color(colors.onTertiary)),
                    Triple("Error", Color(colors.error), Color(colors.onError)),
                    Triple("Surface", Color(colors.surface), Color(colors.onSurface))
                )
            )

            ExpressiveCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = scheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("API 说明", fontWeight = FontWeight.Bold, color = scheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "其他应用可通过 AIDL 绑定此服务生成主题。\n" +
                        "权限: com.monettheme.permission.GENERATE_THEME\n" +
                        "Action: com.monettheme.api.IMonetColorService",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatusCard(colors: ThemeColors, onToggle: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val mode = if (colors.isDarkTheme) "深色模式" else "浅色模式"
    val api = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        "系统 Monet (Android 12+)" else "兼容 Monet (Android 9-11)"

    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = scheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "当前模式: $mode",
                color = scheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
            Text(
                "生成引擎: $api",
                color = scheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "种子色: #${Integer.toHexString(colors.seedColor).uppercase().padStart(6, '0')}",
                color = scheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExpressiveButton(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = scheme.primary,
                    contentColor = scheme.onPrimary
                )
            ) {
                Text("切换 浅色/深色")
            }
        }
    }
}

@Composable
fun ColorRow(items: List<Triple<String, Color, Color>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (name, bg, textColor) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(name, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MonetServiceTheme(
    seedColor: Color,
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        dynamicDarkColorScheme(seedColor)
    } else {
        dynamicLightColorScheme(seedColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ExpressiveTypography(),
        content = content
    )
}
