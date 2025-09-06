package com.example.myapplication.network

import com.example.myapplication.config.Config
import com.example.myapplication.model.AuthResponse
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.runBlocking

/**
 * 인증 관련 API
 *
 * 주요 기능:
 * - Google ID 토큰 인증
 */
class AuthApi {
    private val baseUrl = Config.baseUrl

    /**
     * Google ID 토큰으로 인증을 수행합니다
     *
     * @param idToken Google ID token
     * @return 인증 결과 (AccessToken, RefreshToken)
     */
    suspend fun authenticateWithGoogle(idToken: String): Result<AuthResponse> {
        return runBlocking { performNetworkRequest(idToken) }
    }

    /** 네트워크 작업을 백그라운드에서 실행 */
    private fun performNetworkRequest(idToken: String): Result<AuthResponse> {
        val future = CompletableFuture<Result<AuthResponse>>()

        Thread {
                    try {
                        println("🔐 Google ID 토큰 인증 시작 - 토큰: ${idToken.take(20)}...")

                        // Google ID 토큰 검증
                        if (!isValidGoogleIdToken(idToken)) {
                            println("❌ 유효하지 않은 Google ID 토큰")
                            future.complete(Result.failure(Exception("유효하지 않은 Google ID 토큰입니다")))
                            return@Thread
                        }

                        println("✅ Google ID 토큰 검증 통과")

                        // 실제 HTTP 요청 구현
                        val url = URL(Config.GoogleOAuth.authEndpoint)
                        println("🌐 API 요청 URL: $url")

                        val connection = url.openConnection() as HttpURLConnection

                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Accept", "application/json")
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.setRequestProperty("User-Agent", Config.userAgent)
                        connection.setRequestProperty("Cache-Control", "no-cache")
                        connection.setRequestProperty("Authorization", "Bearer $idToken")
                        connection.doOutput = true
                        connection.connectTimeout = Config.connectionTimeout.toInt()
                        connection.readTimeout = Config.apiTimeout.toInt()

                        println("📡 HTTP 요청 전송 중...")
                        println("🔧 요청 헤더:")
                        println("   - Accept: application/json")
                        println("   - Content-Type: application/json")
                        println("   - User-Agent: ${Config.userAgent}")
                        println("   - Authorization: Bearer ${idToken.take(20)}...")

                        // 연결을 명시적으로 시작
                        connection.connect()

                        val responseCode = connection.responseCode
                        println("📊 HTTP 응답 코드: $responseCode")

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response =
                                    connection.inputStream.bufferedReader().use { it.readText() }
                            println("📄 서버 응답: $response")

                            // 간단한 JSON 파싱 (실제로는 Gson을 사용하는 것이 좋습니다)
                            val authResponse = parseAuthResponse(response)
                            println("✅ 인증 성공 - AccessToken: ${authResponse.accessToken}")
                            future.complete(Result.success(authResponse))
                        } else {
                            val errorMessage =
                                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                                            ?: "HTTP Error: $responseCode"

                            println("❌ HTTP 오류 발생:")
                            println("   - 응답 코드: $responseCode")
                            println("   - 오류 메시지: $errorMessage")
                            println("   - 응답 헤더:")
                            connection.headerFields?.forEach { (key, value) ->
                                println("     $key: $value")
                            }

                            // 500 오류에 대한 특별한 처리
                            if (responseCode == 500) {
                                println("🚨 서버 내부 오류 (500) - 서버 측 문제일 가능성이 높습니다")
                                println("   - Google ID 토큰이 유효하지 않을 수 있습니다")
                                println("   - 서버의 Google OAuth 설정을 확인해주세요")
                            }

                            future.complete(
                                    Result.failure(
                                            Exception("서버 오류 ($responseCode): $errorMessage")
                                    )
                            )
                        }
                    } catch (e: Exception) {
                        println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                        e.printStackTrace()
                        future.complete(
                                Result.failure(
                                        Exception("네트워크 오류: ${e.message ?: e.javaClass.simpleName}")
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
     * Google ID 토큰의 유효성을 검증합니다
     *
     * @param idToken Google ID token
     * @return 유효한 토큰인지 여부
     */
    private fun isValidGoogleIdToken(idToken: String): Boolean {
        // Google ID 토큰은 일반적으로 다음과 같은 패턴을 가집니다:
        // - JWT 형식 (header.payload.signature)
        // - 최소 100자 이상
        // - 점(.)으로 구분된 3개 부분

        println("🔍 Google ID 토큰 검증 중: ${idToken.take(20)}...")

        if (idToken.isBlank()) {
            println("❌ 토큰이 비어있습니다")
            return false
        }

        if (idToken.length < 100) {
            println("❌ 토큰 길이가 너무 짧습니다 (${idToken.length}자)")
            return false
        }

        // JWT 형식 검증 (3개 부분으로 구분)
        val parts = idToken.split(".")
        val isValid = parts.size == 3

        if (isValid) {
            println("✅ Google ID 토큰 형식 검증 통과")
        } else {
            println("❌ Google ID 토큰 형식 검증 실패")
            println("   - 예상 형식: JWT (header.payload.signature)")
            println("   - 실제 부분 수: ${parts.size}")
        }

        return isValid
    }
}
