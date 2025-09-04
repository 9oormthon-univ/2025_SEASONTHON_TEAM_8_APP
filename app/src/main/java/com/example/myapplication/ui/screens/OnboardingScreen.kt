/**
 * 코멘토 앱의 온보딩 시작 화면
 * 
 * 앱을 처음 실행할 때 나타나는 환영 화면으로,
 * 브랜드 아이덴티티와 주요 기능을 소개합니다.
 * 
 * 주요 기능:
 * - 구글과 TextMate 로고로 브랜드 신뢰성 강조
 * - 그라데이션 배경으로 모던한 느낌
 * - 주요 기능 소개 카드들
 * - 시작하기 버튼으로 메인 화면 진입
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BackgroundColor

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.background_onboarding),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // 상단 여백
            Spacer(modifier = Modifier.height(120.dp))
            
            // 중앙 TextMate 로고 섹션
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // TextMate 로고
                Image(
                    painter = painterResource(id = R.drawable.logo_textmate),
                    contentDescription = "TextMate 로고",
                    modifier = Modifier
                        .size(270.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            // 하단 버튼들
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                // Continue with Google 버튼
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 구글 로고 (작게)
                        Image(
                            painter = painterResource(id = R.drawable.logo_google),
                            contentDescription = "Google 로고",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 12.dp)
                        )
                        
                        Text(
                            text = "Continue with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Log in 텍스트 버튼
                TextButton(
                    onClick = onGetStarted,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Log in",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


