package com.example.myapplication.network

import com.example.myapplication.model.AuthResponse
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.runBlocking

object NetworkModule {
    private const val BASE_URL = "https://textmate.zapto.org"

    val apiService: ApiService =
            object : ApiService {
                override suspend fun authenticateWithGoogle(code: String): Result<AuthResponse> {
                    return runBlocking { performNetworkRequest(code) }
                }

                /** 네트워크 작업을 백그라운드에서 실행 */
                private fun performNetworkRequest(code: String): Result<AuthResponse> {
                    val future = CompletableFuture<Result<AuthResponse>>()

                    Thread {
                                try {
                                    println("🔐 Google OAuth 인증 시작 - 코드: $code")

                                    // Google OAuth 코드 검증
                                    if (!isValidGoogleAuthCode(code)) {
                                        println("❌ 유효하지 않은 Google OAuth 코드")
                                        future.complete(
                                                Result.failure(
                                                        Exception("유효하지 않은 Google OAuth 코드입니다")
                                                )
                                        )
                                        return@Thread
                                    }

                                    println("✅ Google OAuth 코드 검증 통과")

                                    // 실제 HTTP 요청 구현
                                    val url = URL("$BASE_URL/auth/google?code=$code")
                                    println("🌐 API 요청 URL: $url")

                                    val connection = url.openConnection() as HttpURLConnection

                                    connection.requestMethod = "GET"
                                    connection.setRequestProperty("Accept", "application/json")
                                    connection.setRequestProperty(
                                            "User-Agent",
                                            "TextMate-Android/1.0"
                                    )
                                    connection.connectTimeout = 30000
                                    connection.readTimeout = 30000

                                    println("📡 HTTP 요청 전송 중...")

                                    // 연결을 명시적으로 시작
                                    connection.connect()

                                    val responseCode = connection.responseCode
                                    println("📊 HTTP 응답 코드: $responseCode")

                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        val response =
                                                connection.inputStream.bufferedReader().use {
                                                    it.readText()
                                                }
                                        println("📄 서버 응답: $response")

                                        // 간단한 JSON 파싱 (실제로는 Gson을 사용하는 것이 좋습니다)
                                        val authResponse = parseAuthResponse(response)
                                        println(
                                                "✅ 인증 성공 - AccessToken: ${authResponse.accessToken}"
                                        )
                                        future.complete(Result.success(authResponse))
                                    } else {
                                        val errorMessage =
                                                connection.errorStream?.bufferedReader()?.use {
                                                    it.readText()
                                                }
                                                        ?: "HTTP Error: $responseCode"
                                        println("❌ HTTP 오류: $errorMessage")
                                        future.complete(
                                                Result.failure(
                                                        Exception(
                                                                "서버 오류 ($responseCode): $errorMessage"
                                                        )
                                                )
                                        )
                                    }
                                } catch (e: Exception) {
                                    println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                                    e.printStackTrace()
                                    future.complete(
                                            Result.failure(
                                                    Exception(
                                                            "네트워크 오류: ${e.message ?: e.javaClass.simpleName}"
                                                    )
                                            )
                                    )
                                }
                            }
                            .start()

                    return future.get()
                }

                private fun parseAuthResponse(json: String): AuthResponse {
                    // 간단한 JSON 파싱 (실제로는 Gson을 사용하는 것이 좋습니다)
                    val accessToken = extractValue(json, "accessToken")
                    val refreshToken = extractValue(json, "refreshToken")
                    return AuthResponse(accessToken, refreshToken)
                }

                private fun extractValue(json: String, key: String): String {
                    val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\""
                    val regex = pattern.toRegex()
                    return regex.find(json)?.groupValues?.get(1) ?: ""
                }

                /**
                 * Google OAuth 코드의 유효성을 검증합니다
                 *
                 * @param code Google OAuth authorization code
                 * @return 유효한 코드인지 여부
                 */
                private fun isValidGoogleAuthCode(code: String): Boolean {
                    // Google OAuth 코드는 일반적으로 다음과 같은 패턴을 가집니다:
                    // - 4/0AX4XfWh... 형태
                    // - 최소 20자 이상
                    // - 특수문자와 영문자, 숫자 조합

                    if (code.length < 20) return false

                    // Google OAuth 코드 패턴 검증
                    val googleOAuthPattern = "^4/0AX4XfWh.*".toRegex()
                    return googleOAuthPattern.matches(code)
                }
            }
}
