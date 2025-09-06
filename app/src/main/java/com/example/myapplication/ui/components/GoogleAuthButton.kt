package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.viewmodel.AuthState
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun GoogleAuthButton(
        modifier: Modifier = Modifier,
        onAuthSuccess: (String, String) -> Unit = { _, _ -> },
        onAuthError: (String) -> Unit = {}
) {
    val authViewModel = remember { AuthViewModel() }
    val authState by authViewModel.authState.collectAsState()

    // 인증 상태에 따른 UI 업데이트
    LaunchedEffect(authState) {
        when (val currentState = authState) {
            is AuthState.Success -> {
                onAuthSuccess(
                        currentState.authResponse.accessToken,
                        currentState.authResponse.refreshToken
                )
                authViewModel.resetAuthState()
            }
            is AuthState.Error -> {
                onAuthError(currentState.message)
                authViewModel.resetAuthState()
            }
            else -> {
                /* 다른 상태는 처리하지 않음 */
            }
        }
    }

    when (authState) {
        is AuthState.Loading -> {
            Button(
                    onClick = { /* 로딩 중에는 클릭 비활성화 */},
                    modifier = modifier.fillMaxWidth(0.8f).height(56.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = Color.Black.copy(alpha = 0.6f),
                                    contentColor = Color.White
                            ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = false
            ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "인증 중...", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        else -> {
            Button(
                    onClick = {
                        // 실제 Google OAuth 인증 코드 생성
                        // 실제 구현에서는 Google OAuth 플로우를 통해 받은 코드를 사용해야 합니다
                        val googleAuthCode = generateGoogleAuthCode()
                        authViewModel.authenticateWithGoogle(googleAuthCode)
                    },
                    modifier = modifier.fillMaxWidth(0.8f).height(56.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = Color.Black.copy(alpha = 0.8f),
                                    contentColor = Color.White
                            ),
                    shape = RoundedCornerShape(28.dp),
                    border =
                            androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.3f)
                            )
            ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                ) {
                    // 구글 로고
                    Image(
                            painter = painterResource(id = R.drawable.logo_google),
                            contentDescription = "Google 로고",
                            modifier = Modifier.size(20.dp).padding(end = 12.dp)
                    )

                    Text(
                            text = "Continue with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/** Google OAuth 인증 코드를 생성하는 함수 실제 구현에서는 Google OAuth 플로우를 통해 받은 코드를 사용해야 합니다 */
private fun generateGoogleAuthCode(): String {
    // 실제 Google OAuth 플로우에서는 다음과 같은 형태의 코드를 받습니다:
    // "4/0AX4XfWh..." 형태의 authorization code

    // 현재는 시뮬레이션을 위한 코드를 생성합니다
    val timestamp = System.currentTimeMillis()
    val randomCode = (100000..999999).random()

    // 실제 Google OAuth authorization code 형태로 시뮬레이션
    return "4/0AX4XfWh${randomCode}_${timestamp}_google_oauth_code"
}
