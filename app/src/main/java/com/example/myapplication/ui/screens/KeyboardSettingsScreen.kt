/**
 * 코멘토 키보드 설정 화면
 *
 * 이 화면은 사용자가 코멘토 키보드를 설정하고 개인화할 수 있는 화면입니다.
 *
 * 주요 기능:
 * - 키보드 활성화 및 시스템 설정 연동
 * - 페르소나 선택 (친근한, 격식있는, 재미있는, 전문적인)
 * - AI 자동완성 기능 토글
 * - 단계별 설정 가이드 제공
 *
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BackgroundColor
import com.example.myapplication.ui.theme.MainColor1
import com.example.myapplication.ui.theme.MainColor2
import com.example.myapplication.ui.theme.PointColor1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardSettingsScreen(onBackClick: () -> Unit = {}) {
    // 현재 컨텍스트 가져오기 (Intent 실행을 위해)
    val context = LocalContext.current

    Scaffold(
            topBar = {
                // 상단 앱바
                Card(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .shadow(
                                                elevation = 8.dp,
                                                spotColor = Color.White.copy(alpha = 0.3f)
                                        ),
                        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundColor)
                ) {
                    Column {
                        TopAppBar(
                                title = {
                                    Text(
                                            text = "키보드 설정",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = onBackClick) {
                                        Icon(
                                                Icons.Default.ArrowBack,
                                                contentDescription = "뒤로가기",
                                                tint = Color.White
                                        )
                                    }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.Transparent,
                                                titleContentColor = Color.White,
                                                navigationIconContentColor = Color.White
                                        )
                        )
                        // 하얀색 그림자
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(5.dp)
                                                .background(
                                                        brush =
                                                                androidx.compose.ui.graphics.Brush
                                                                        .verticalGradient(
                                                                                colors =
                                                                                        listOf(
                                                                                                MainColor2
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.3f
                                                                                                        ),
                                                                                                Color.Transparent
                                                                                        )
                                                                        )
                                                )
                        )
                    }
                }
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(paddingValues)) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 키보드 활성화 버튼 (시스템 설정으로 이동)
                Button(
                        onClick = {
                            // 안드로이드 시스템 입력 방법 설정으로 이동
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor1)
                ) { Text(text = "키보드 활성화", fontSize = 18.sp, color = Color.White) }

                Spacer(modifier = Modifier.height(16.dp))

                // 설정 가이드 카드
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = BackgroundColor
                                )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                                text = "설정 방법",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // 단계별 설정 가이드
                        Text(
                                text =
                                        "1. '키보드 활성화' 버튼을 탭하세요\n" +
                                                "2. 'AI 자동완성 키보드'를 선택하세요\n" +
                                                "3. '사용'을 탭하세요\n" +
                                                "4. 텍스트 입력 시 키보드 선택에서 'AI 자동완성 키보드'를 선택하세요",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AI 설정 카드
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = BackgroundColor
                                )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                                text = "AI 설정",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 페르소나 설정 섹션
                        Text(
                                text = "페르소나",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // 페르소나 선택 버튼들 (가로 스크롤)
                        LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 4가지 페르소나 옵션
                            items(listOf("친근한", "격식있는", "재미있는", "전문적인")) { persona ->
                                Button(
                                        modifier = Modifier.width(100.dp).height(30.dp),
                                        onClick = { /* 페르소나 선택 로직 (현재 미구현) */},
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        // 기본값으로 "친근한" 선택 상태 표시
                                                        containerColor =
                                                                if (persona == "친근한") MainColor1
                                                                else MainColor2.copy(alpha = 0.3f)
                                                )
                                ) {
                                    Text(
                                            fontSize = 12.sp,
                                            text = persona,
                                            color =
                                                    if (persona == "친근한") Color.White
                                                    else Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 자동완성 설정 섹션
                        Text(
                                text = "자동완성",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // AI 예측 활성화 토글 스위치
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                    checked = true, // 기본값으로 활성화
                                    onCheckedChange = { /* 자동완성 토글 로직 (현재 미구현) */}
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "AI 예측 활성화", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
