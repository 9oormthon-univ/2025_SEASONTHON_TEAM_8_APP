package com.example.myapplication.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Google OAuth 토큰을 관리하는 클래스
 *
 * SharedPreferences를 사용하여 액세스 토큰과 리프레시 토큰을 저장하고 관리합니다.
 */
class TokenManager(private val context: Context) {

    private val prefs: SharedPreferences =
            context.getSharedPreferences("auth_tokens", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    /**
     * 토큰들을 저장합니다
     *
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            putBoolean(IS_LOGGED_IN_KEY, true)
            apply()
        }
    }

    /**
     * 액세스 토큰을 반환합니다
     *
     * @return 액세스 토큰 또는 null
     */
    fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN_KEY, null)
    }

    /**
     * 리프레시 토큰을 반환합니다
     *
     * @return 리프레시 토큰 또는 null
     */
    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN_KEY, null)
    }

    /**
     * 로그인 상태를 확인합니다
     *
     * @return 로그인 여부
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN_KEY, false) && getAccessToken() != null
    }

    /** 모든 토큰을 삭제하고 로그아웃 처리합니다 */
    fun clearTokens() {
        prefs.edit().apply {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            putBoolean(IS_LOGGED_IN_KEY, false)
            apply()
        }
    }

    /**
     * 토큰이 유효한지 확인합니다 (간단한 null 체크)
     *
     * @return 토큰 유효성
     */
    fun hasValidTokens(): Boolean {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()
        return !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()
    }
}
