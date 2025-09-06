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
import com.example.myapplication.ui.components.HomeScreen.HistoryCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    onNavigateToConversationAnalysis: () -> Unit = {},
    onNavigateToKeyboardSettings: () -> Unit = {},
    onNavigateToGroupAnalysis: () -> Unit = {}, // 단체 톡방 분석으로 이동
    onNavigateToPersonalAnalysis: () -> Unit = {} // 개인 톡방 분석으로 이동
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            // 상단 앱바
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        spotColor = Color.White.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                )
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Image(
                                painter = painterResource(id = R.drawable.logo_textmate),
                                contentDescription = "TextMate 로고",
                                modifier = Modifier.height(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                    // 하얀색 그림자
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        MainColor2.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
        ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 중앙 비디오 섹션 (상단 여백 추가)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(horizontal = 32.dp)
                    .padding(top = 20.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 소개 텍스트 (왼쪽 정렬)
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
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 캐릭터 비디오 (중앙 정렬)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
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
            }
            
            // 하단 스크롤 가능한 히스토리 섹션
            Card(
                modifier = Modifier
                    .fillMaxSize(),
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
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_conversation_analysis),
                                        contentDescription = "대화 분석",
                                        modifier = Modifier.size(70.dp)
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_arrow),
                                        contentDescription = "화살표",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "대화 분석 바로가기",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                        

                        // 키 설정 버튼
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .clickable { onNavigateToKeyboardSettings() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MainColor1
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_key_setting),
                                        contentDescription = "키 설정",
                                        modifier = Modifier.size(70.dp)
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_arrow),
                                        contentDescription = "화살표",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "키보드 설정 바로가기",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
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
                            val isGroupChat = index % 2 == 0 // 짝수 인덱스는 단체, 홀수는 개인
                            val title = if (isGroupChat) {
                                "단체 대화 ${index / 2 + 1}"
                            } else {
                                "개인 대화 ${index / 2 + 1}"
                            }
                            val avatarText = if (isGroupChat) {
                                "단${index / 2 + 1}"
                            } else {
                                "개${index / 2 + 1}"
                            }
                            
                            HistoryCard(
                                title = title,
                                date = "2024년 1월 ${15 + index}일",
                                avatarText = avatarText,
                                isGroupChat = isGroupChat,
                                onClick = { 
                                    // 대화 유형에 따라 다른 분석 화면으로 이동
                                    when (index) {
                                        0 -> {
                                            // 첫 번째 대화는 단체 톡방 분석
                                            onNavigateToGroupAnalysis()
                                        }
                                        1 -> {
                                            // 두 번째 대화는 개인 톡방 분석
                                            onNavigateToPersonalAnalysis()
                                        }
                                        2 -> {
                                            // 세 번째 대화는 단체 톡방 분석
                                            onNavigateToGroupAnalysis()
                                        }
                                        3 -> {
                                            // 네 번째 대화는 개인 톡방 분석
                                            onNavigateToPersonalAnalysis()
                                        }
                                        4 -> {
                                            // 다섯 번째 대화는 단체 톡방 분석
                                            onNavigateToGroupAnalysis()
                                        }
                                        5 -> {
                                            // 여섯 번째 대화는 개인 톡방 분석
                                            onNavigateToPersonalAnalysis()
                                        }
                                        6 -> {
                                            // 일곱 번째 대화는 단체 톡방 분석
                                            onNavigateToGroupAnalysis()
                                        }
                                        7 -> {
                                            // 여덟 번째 대화는 개인 톡방 분석
                                            onNavigateToPersonalAnalysis()
                                        }
                                        8 -> {
                                            // 아홉 번째 대화는 단체 톡방 분석
                                            onNavigateToGroupAnalysis()
                                        }
                                        9 -> {
                                            // 열 번째 대화는 개인 톡방 분석
                                            onNavigateToPersonalAnalysis()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        }
    }
}
