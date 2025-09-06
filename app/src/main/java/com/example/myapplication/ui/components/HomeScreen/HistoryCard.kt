package com.example.myapplication.ui.components.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MainColor1
import com.example.myapplication.ui.theme.PointColor1

/**
 * 히스토리 카드 컴포넌트
 *
 * 대화 히스토리를 표시하는 카드 UI 컴포넌트입니다.
 *
 * @param title 채팅방 이름
 * @param date 대화 날짜
 * @param avatarText 아바타에 표시될 텍스트 (기본값: "Mi")
 * @param isGroupChat 단체 톡방인지 개인 톡방인지 구분
 * @param onClick 카드 클릭 시 실행될 콜백
 */
@Composable
fun HistoryCard(
        title: String,
        date: String,
        avatarText: String = "Mi",
        isGroupChat: Boolean = true, // 단체 톡방 여부
        onClick: () -> Unit = {}
) {
    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 아바타 아이콘
            Box(
                    modifier =
                            Modifier.size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                            if (isGroupChat) MainColor1
                                            else PointColor1 // 단체는 MainColor1, 개인은 PointColor1
                                    ),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = avatarText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 중앙 텍스트 내용
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                )
                Text(
                        text = date,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 오른쪽 메뉴 아이콘 (세로 점 3개)
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) {
                        Box(
                                modifier =
                                        Modifier.size(4.dp)
                                                .clip(CircleShape)
                                                .background(
                                                        if (isGroupChat) MainColor1
                                                        else PointColor1 // 단체는 MainColor1, 개인은
                                                        // PointColor1
                                                        )
                        )
                    }
                }
            }
        }
    }
}
