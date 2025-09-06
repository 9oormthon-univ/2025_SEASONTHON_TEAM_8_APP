/**
 * 코멘토 앱의 온보딩 시작 화면
 *
 * 앱을 처음 실행할 때 나타나는 환영 화면으로, 브랜드 아이덴티티와 주요 기능을 소개합니다.
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.auth.LocalUserManager
import com.example.myapplication.ui.components.GoogleAuthButton
import com.example.myapplication.ui.theme.BackgroundColor

@Composable
fun OnboardingScreen(
        onGetStarted: () -> Unit = {},
        onAuthSuccess: (String, String, String) -> Unit = { _, _, _ -> } // email, name, profileUrl
) {
    val context = LocalContext.current
    val userManager = remember { LocalUserManager(context) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        // 배경 이미지
        Image(
                painter = painterResource(id = R.drawable.background_onboarding),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
        )
        Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
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
                        modifier = Modifier.size(270.dp).padding(bottom = 24.dp),
                        contentScale = ContentScale.Fit
                )
            }

            // 하단 버튼들
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 64.dp)
            ) {
                // Google 인증 버튼
                GoogleAuthButton(
                        onAuthSuccess = { email, name, profileUrl ->
                            // 로컬 인증 성공 시 처리
                            println("✅ 온보딩에서 Google 로그인 성공:")
                            println("   이메일: $email")
                            println("   이름: $name")
                            println("   프로필 사진: $profileUrl")

                            // 로컬에 사용자 정보 저장
                            userManager.saveUserInfo(email, name, profileUrl)

                            // 저장된 정보 확인
                            userManager.printUserInfo()

                            // 사용자 정보를 상위 컴포넌트로 전달
                            onAuthSuccess(email, name, profileUrl)

                            // 메인 화면으로 이동
                            onGetStarted()
                        },
                        onAuthError = { errorMessage ->
                            // 에러 처리 (필요시 스낵바나 다이얼로그 표시)
                            println("❌ Google 인증 오류: $errorMessage")
                        }
                )

                // 로컬 로그인 버튼 (Google OAuth 문제 우회용)
                Button(
                        onClick = {
                            // 로컬 로그인 시뮬레이션
                            println("🔐 로컬 로그인 시작...")

                            // 시뮬레이션된 사용자 정보
                            val email = "user@example.com"
                            val name = "사용자"
                            val profileUrl = "https://via.placeholder.com/150"

                            // 로컬에 사용자 정보 저장
                            userManager.saveUserInfo(email, name, profileUrl)

                            // 저장된 정보 확인
                            userManager.printUserInfo()

                            // 사용자 정보를 상위 컴포넌트로 전달
                            onAuthSuccess(email, name, profileUrl)

                            // 메인 화면으로 이동
                            onGetStarted()
                        },
                        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp).padding(top = 12.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                        contentColor = Color.White
                                ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                        border =
                                androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color.White.copy(alpha = 0.3f)
                                )
                ) {
                    Text(
                            text = "Continue without Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                    )
                }

                // Log in 텍스트 버튼
                TextButton(onClick = onGetStarted, modifier = Modifier.padding(top = 16.dp)) {
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
