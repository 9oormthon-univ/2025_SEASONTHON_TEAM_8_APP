package com.example.myapplication.ui.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.config.Config
import com.example.myapplication.viewmodel.AuthState
import com.example.myapplication.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun GoogleAuthButton(
        modifier: Modifier = Modifier,
        onAuthSuccess: (String, String) -> Unit = { _, _ -> },
        onAuthError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel() }
    val authState by authViewModel.authState.collectAsState()

    // Google Sign-In 클라이언트 초기화
    val googleSignInClient: GoogleSignInClient = remember {
        // Google OAuth 설정 검증
        if (!Config.GoogleOAuth.isConfigured()) {
            println("❌ Google OAuth 설정이 완료되지 않았습니다. Config.kt에서 WEB_CLIENT_ID를 설정해주세요.")
        }

        val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Config.GoogleOAuth.WEB_CLIENT_ID)
                        .requestEmail()
                        .requestProfile()
                        .build()
        GoogleSignIn.getClient(context, gso)
    }

    // `ActivityResultLauncher`를 사용하여 Google Sign-In 결과를 처리합니다.
    val googleAuthLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        // Google 계정 정보를 가져와서 ID 토큰을 얻습니다.
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account?.idToken

                        if (idToken != null) {
                            authViewModel.authenticateWithGoogle(idToken)
                        } else {
                            onAuthError("ID 토큰을 가져오지 못했습니다.")
                        }
                    } catch (e: ApiException) {
                        // Google Sign-In 실패 시 에러 처리
                        val errorMessage =
                                when (e.statusCode) {
                                    12501 -> { // SIGN_IN_CANCELLED
                                        println("Google Sign-In이 사용자에 의해 취소되었습니다.")
                                        "Google Sign-In이 취소되었습니다."
                                    }
                                    7 -> { // NETWORK_ERROR
                                        println("네트워크 오류로 Google Sign-In이 실패했습니다.")
                                        "네트워크 연결을 확인해주세요."
                                    }
                                    8 -> { // INTERNAL_ERROR
                                        println("Google Sign-In 내부 오류가 발생했습니다.")
                                        "Google Sign-In 내부 오류가 발생했습니다."
                                    }
                                    10 -> { // DEVELOPER_ERROR
                                        println("Google OAuth 설정 오류입니다.")
                                        "Google OAuth 설정 오류입니다."
                                    }
                                    else -> {
                                        println("Google Sign-In 실패: ${e.statusCode} - ${e.message}")
                                        "Google Sign-In 실패: ${e.statusCode}"
                                    }
                                }
                        onAuthError(errorMessage)
                    }
                } else {
                    // 사용자가 인증을 취소했거나 다른 오류가 발생했을 때
                    println("Google Sign-In 결과 코드: ${result.resultCode}")
                    println("Google Sign-In이 취소되었거나 실패했습니다.")
                    onAuthError("Google Sign-In이 취소되었습니다.")
                }
            }

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

    // UI 부분은 동일하게 유지
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
                        // Google OAuth 설정 검증
                        if (!Config.GoogleOAuth.isConfigured()) {
                            onAuthError("Google OAuth 설정이 완료되지 않았습니다. 개발자에게 문의하세요.")
                            return@Button
                        }

                        // 버튼 클릭 시 런처를 사용하여 Google Sign-In 인텐트를 실행합니다.
                        println("🔐 Google Sign-In 시작...")
                        val signInIntent = googleSignInClient.signInIntent
                        googleAuthLauncher.launch(signInIntent)
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
