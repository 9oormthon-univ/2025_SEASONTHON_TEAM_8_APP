/**
 * 코멘토 앱의 웰컴 화면
 * 
 * Welcome.mp4 애니메이션을 중앙에 표시하는 시작 화면입니다.
 * 
 * 주요 기능:
 * - Welcome.mp4 비디오 애니메이션 재생
 * - 검정색 배경으로 비디오 강조
 * - 하단에 시작하기 버튼
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import android.widget.VideoView
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BackgroundColor
import com.example.myapplication.ui.theme.MainColor1
import com.example.myapplication.ui.theme.MainColor2
import com.example.myapplication.ui.theme.GradientColor1
import com.example.myapplication.ui.theme.GradientColor2
import com.example.myapplication.ui.theme.GradientColor3
import com.example.myapplication.ui.theme.GradientColor4
import com.example.myapplication.ui.theme.GradientColor5
import com.example.myapplication.ui.theme.PointColor1
import com.example.myapplication.ui.theme.getDesignGradientBrush

@Composable
fun WelcomScreen(
    onGetStarted: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // 자동 전환 효과
    LaunchedEffect(Unit) {
        delay(2000) // 2초 대기 후 바로 전환
        onGetStarted() // 다음 화면으로 이동
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 말풍선들 (상단에 배치)
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 120.dp)
            ) {
                // 그라데이션 정의
                val cardGradient = getDesignGradientBrush()
                // 상단 말풍선 - "안녕하세요"
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(
                            color = MainColor1,
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Text(
                        text = "안녕하세요",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 두 번째 말풍선 - "저는 당신의 TextMate 예요"
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(
                            color = MainColor1,
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "저는 당신의 ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "TextMate",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PointColor1
                        )
                        Text(
                            text = " 예요",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                
                // Welcome 비디오/애니메이션 (화면 대부분 차지)
            }
            
            // 비디오 (전체 화면 중앙에 오버레이)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp, bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { context ->
                        VideoView(context).apply {
                            val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.image01}")
                            setVideoURI(videoUri)
                            setOnPreparedListener { mediaPlayer ->
                                mediaPlayer.isLooping = true
                                mediaPlayer.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                                start()
                            }
                            // VideoView 강제 크기 조정
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            // 스케일 강제 적용
                            scaleX = 0.8f
                            scaleY = 0.8f
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}
