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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun GoogleAuthButton(
        modifier: Modifier = Modifier,
        onAuthSuccess: (String, String, String) -> Unit = { _, _, _ ->
        }, // email, name, profileUrl 추가
        onAuthError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign-In 클라이언트 초기화
    val googleSignInClient: GoogleSignInClient = remember {
        // Google OAuth 설정 검증
        if (!Config.GoogleOAuth.isConfigured()) {
            println("❌ Google OAuth 설정이 완료되지 않았습니다. Config.kt에서 WEB_CLIENT_ID를 설정해주세요.")
        }

        println("🔧 Google Sign-In 클라이언트 초기화:")
        println("   WEB_CLIENT_ID: ${Config.GoogleOAuth.WEB_CLIENT_ID}")
        println("   설정 완료 여부: ${Config.GoogleOAuth.isConfigured()}")

        try {
            val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Config.GoogleOAuth.WEB_CLIENT_ID)
                            .requestEmail()
                            .requestProfile()
                            .build()

            println("   GoogleSignInOptions: $gso")
            println("   요청된 ID Token: ${Config.GoogleOAuth.WEB_CLIENT_ID}")

            val client = GoogleSignIn.getClient(context, gso)
            println("   GoogleSignInClient 생성 완료")

            // 현재 로그인된 계정 확인
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                println("   이미 로그인된 계정: ${account.email}")
            } else {
                println("   로그인된 계정 없음")
            }

            client
        } catch (e: Exception) {
            println("❌ Google Sign-In 클라이언트 초기화 실패: ${e.message}")
            println("   예외 타입: ${e.javaClass.simpleName}")
            e.printStackTrace()
            throw e
        }
    }

    // `ActivityResultLauncher`를 사용하여 Google Sign-In 결과를 처리합니다.
    val googleAuthLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                println("🔍 Google Sign-In 결과 상세 정보:")
                println("   결과 코드: ${result.resultCode}")
                println("   Activity.RESULT_OK: ${Activity.RESULT_OK}")
                println("   데이터: ${result.data}")

                // Google Sign-In은 결과 코드가 0이어도 성공할 수 있으므로 데이터가 있으면 시도
                if (result.resultCode == Activity.RESULT_OK || result.data != null) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    println("   Task: $task")
                    println("   Task 완료 여부: ${task.isComplete}")
                    println("   Task 성공 여부: ${task.isSuccessful}")

                    try {
                        // Google 계정 정보를 가져와서 로컬에서 처리합니다.
                        val account = task.getResult(ApiException::class.java)
                        println("   계정 정보: $account")

                        if (account != null) {
                            // 로컬에서 사용자 정보 추출
                            val email = account.email ?: ""
                            val displayName = account.displayName ?: ""
                            val photoUrl = account.photoUrl?.toString() ?: ""

                            println("✅ Google 로그인 성공:")
                            println("   이메일: $email")
                            println("   이름: $displayName")
                            println("   프로필 사진: $photoUrl")

                            // 로컬 인증 성공 콜백 호출
                            onAuthSuccess(email, displayName, photoUrl)
                        } else {
                            println("❌ 계정 정보가 null입니다.")
                            onAuthError("Google 계정 정보를 가져오지 못했습니다.")
                        }
                    } catch (e: ApiException) {
                        // Google Sign-In 실패 시 에러 처리
                        println("❌ ApiException 발생: ${e.statusCode} - ${e.message}")
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
                                        println("   - SHA-1 인증서 해시가 올바른지 확인하세요")
                                        println("   - google-services.json 파일이 최신인지 확인하세요")
                                        println("   - Google Console에서 OAuth 클라이언트 설정을 확인하세요")
                                        "Google OAuth 설정 오류입니다. 개발자에게 문의하세요."
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
                    println("❌ Google Sign-In 결과 코드가 RESULT_OK가 아닙니다.")
                    println("   결과 코드: ${result.resultCode}")
                    println("   예상 코드: ${Activity.RESULT_OK}")

                    when (result.resultCode) {
                        Activity.RESULT_CANCELED -> {
                            println("   사용자가 취소했습니다.")
                            onAuthError("Google Sign-In이 취소되었습니다.")
                        }
                        else -> {
                            println("   알 수 없는 오류입니다.")
                            onAuthError("Google Sign-In이 실패했습니다. (코드: ${result.resultCode})")
                        }
                    }
                }
                isLoading = false
            }

    // UI 부분
    if (isLoading) {
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
    } else {
        Button(
                onClick = {
                    // Google OAuth 설정 검증
                    if (!Config.GoogleOAuth.isConfigured()) {
                        onAuthError("Google OAuth 설정이 완료되지 않았습니다. 개발자에게 문의하세요.")
                        return@Button
                    }

                    // Google Sign-In 시작
                    isLoading = true
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
