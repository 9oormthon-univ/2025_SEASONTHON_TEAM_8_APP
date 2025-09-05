package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlin.math.cos
import kotlin.math.sin

// 앱 메인 컬러 팔레트
val MainColor1 = Color(0xFF34C2E8)  // 메인컬러1 - 밝은 청록색
val MainColor2 = Color(0xFFD1FFFF)  // 메인컬러2 - 연한 민트색
val BackgroundColor = Color(0xFF030606)  // 배경컬러 - 진한 검정색
val PointColor1 = Color(0xFF8670BD)  // 포인트컬러1 - 보라색
val PointColor2 = Color(0xFF78337D)  // 포인트컬러2 - 진한 보라색

// 라이너 그래디언트 컬러 팔레트
val GradientColor1 = Color(0xFF34C2E8)  // 그래디언트 시작 - 밝은 청록색
val GradientColor2 = Color(0xFFA9EFF9)  // 그래디언트 중간1 - 연한 하늘색
val GradientColor3 = Color(0xFFD1FFFF)  // 그래디언트 중간2 - 연한 민트색
val GradientColor4 = Color(0xFFB2F3FA)  // 그래디언트 중간3 - 중간 민트색
val GradientColor5 = Color(0xFF34C2E8)  // 그래디언트 끝 - 밝은 청록색

// 디자이너 스펙 그래디언트 (투명도 0.8 적용)
val DesignGradientColor1 = Color(0xCC34C2E8)  // 4.75% - rgba(52, 194, 232, 0.80)
val DesignGradientColor2 = Color(0xCCA9EFF9)  // 27.52% - rgba(169, 239, 249, 0.80)
val DesignGradientColor3 = Color(0xCCD1FFFF)  // 48.5% - rgba(209, 255, 255, 0.80)
val DesignGradientColor4 = Color(0xCCB2F3FA)  // 66.36% - rgba(178, 243, 250, 0.80)
val DesignGradientColor5 = Color(0xCC34C2E8)  // 96.27% - rgba(52, 194, 232, 0.80)

// 베이스 컬러 (blend mode overlay용)
val OverlayBaseColor = Color(0xFF8670BD)  // #8670BD

// 디자이너 스펙 그래디언트 브러시 (234도 각도)
@Composable
fun getDesignGradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            DesignGradientColor1,
            DesignGradientColor2,
            DesignGradientColor3,
            DesignGradientColor4,
            DesignGradientColor5
        )
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = MainColor1,
    secondary = PointColor1,
    tertiary = PointColor2,
    background = BackgroundColor,
    surface = BackgroundColor
)

private val LightColorScheme = lightColorScheme(
    primary = MainColor1,
    secondary = PointColor1,
    tertiary = PointColor2,
    background = MainColor2,
    surface = MainColor2
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}