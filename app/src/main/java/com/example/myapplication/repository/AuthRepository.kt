package com.example.myapplication.repository

import com.example.myapplication.model.AuthResponse
import com.example.myapplication.network.AuthApi

class AuthRepository {
    private val authApi = AuthApi()

    suspend fun authenticateWithGoogle(idToken: String): Result<AuthResponse> {
        return try {
            // AuthApi를 사용한 Google ID 토큰 인증
            authApi.authenticateWithGoogle(idToken)
        } catch (e: Exception) {
            println("Google 인증 실패: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        // 로그아웃 로직 (필요시 구현)
        println("사용자 로그아웃")
    }

    fun getCurrentUser() = null // 현재 사용자 정보 (필요시 구현)
}
