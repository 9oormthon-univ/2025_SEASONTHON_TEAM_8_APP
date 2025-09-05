/**
 * 코멘토 앱의 홈 화면
 * 
 * 이 화면은 앱의 메인 대시보드 역할을 합니다.
 * 
 * 주요 기능:
 * - TextMate 로고 표시
 * - 중앙 캐릭터 비디오
 * - 스크롤 가능한 히스토리 섹션
 * - 대화 분석 및 키 설정 버튼
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BackgroundColor
import com.example.myapplication.ui.theme.getDesignGradientBrush
import com.example.myapplication.ui.theme.MainColor2
import com.example.myapplication.ui.theme.MainColor1
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

@Composable
fun HomeScreen(
    onNavigateToConversationAnalysis: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // 앱 아이콘 또는 로고 (이모지 사용)
        Text(
            text = "⌨️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 앱 제목 (브랜드명)
        Text(
            text = "코멘토",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 앱 설명 (간단한 소개)
        Text(
            text = "이곳은 메인화면!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
       

        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 중앙 비디오 섹션 (상단 여백 추가)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(horizontal = 32.dp)
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 소개 텍스트
                    Text(
                        text = buildAnnotatedString {
                            append("저는 당신의 ")
                            withStyle(style = SpanStyle(color = MainColor2)) {
                                append("TextMate")
                            }
                            append(" 입니다\n따뜻한 대화를 도와드릴게요")
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 캐릭터 비디오
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.character}")
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
                                scaleX = 2.0f
                                scaleY = 2.0f
                            }
                        },
                        modifier = Modifier
                            .size(250.dp)
                    )
                }
            }
            
            // 하단 스크롤 가능한 히스토리 섹션
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // 버튼들
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 대화 분석 버튼
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .clickable { onNavigateToConversationAnalysis() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MainColor2
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_conversation_analysis),
                                    contentDescription = "대화 분석",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "대화 분석 바로가기",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.icon_arrow),
                                    contentDescription = "화살표",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                    }
                    
                    // 히스토리 제목과 See all 버튼
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        TextButton(
                            onClick = { /* See all 동작 */ }
                        ) {
                            Text(
                                text = "See all",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    
                    // 스크롤 가능한 히스토리 리스트
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(10) { index ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF2A2A2A)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 히스토리 아이템 내용
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "대화 ${index + 1}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "2024년 1월 ${15 + index}일",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                    
                                    // 화살표 아이콘
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_arrow),
                                        contentDescription = "화살표",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
