package com.example.myapplication.config

/**
 * 애플리케이션 환경 설정
 *
 * 개발, 스테이징, 프로덕션 환경별로 다른 설정을 관리합니다.
 */
object Config {

    /** 현재 환경 타입 */
    enum class Environment {
        DEVELOPMENT,
        STAGING,
        PRODUCTION
    }

    /** 현재 환경 설정 기본적으로 DEVELOPMENT 환경으로 설정 필요시 수동으로 변경 가능 */
    private val currentEnvironment: Environment = Environment.DEVELOPMENT

    /** API 기본 URL */
    val baseUrl: String
        get() =
                when (currentEnvironment) {
                    Environment.DEVELOPMENT -> "https://textmate.zapto.org"
                    Environment.STAGING -> "https://textmate.zapto.org"
                    Environment.PRODUCTION -> "https://textmate.zapto.org"
                }

    /** API 타임아웃 설정 (밀리초) */
    val apiTimeout: Long
        get() =
                when (currentEnvironment) {
                    Environment.DEVELOPMENT -> 60000L // 개발 환경에서는 더 긴 타임아웃
                    Environment.STAGING -> 30000L
                    Environment.PRODUCTION -> 30000L
                }

    /** 연결 타임아웃 설정 (밀리초) */
    val connectionTimeout: Long
        get() =
                when (currentEnvironment) {
                    Environment.DEVELOPMENT -> 30000L
                    Environment.STAGING -> 20000L
                    Environment.PRODUCTION -> 20000L
                }

    /** 로깅 활성화 여부 */
    val isLoggingEnabled: Boolean
        get() =
                when (currentEnvironment) {
                    Environment.DEVELOPMENT -> true
                    Environment.STAGING -> true
                    Environment.PRODUCTION -> false
                }

    /** 디버그 모드 여부 */
    val isDebugMode: Boolean
        get() = currentEnvironment == Environment.DEVELOPMENT

    /** 현재 환경 정보를 문자열로 반환 */
    val environmentName: String
        get() = currentEnvironment.name

    /** User-Agent 문자열 */
    val userAgent: String
        get() = "TextMate-Android/1.0 (${environmentName})"

    /** Google OAuth 설정 */
    object GoogleOAuth {
        /** Google Web Client ID - 새로운 OAuth 클라이언트 ID */
        const val WEB_CLIENT_ID =
                "779645352775-g7ocv34fisjumqrvhvvnajt8tedda07s.apps.googleusercontent.com"

        /** Google OAuth 인증 엔드포인트 */
        val authEndpoint: String
            get() = "$baseUrl/api/auth/google"

        /** Google Sign-In 설정이 올바른지 확인 */
        fun isConfigured(): Boolean {
            return WEB_CLIENT_ID != "YOUR_WEB_CLIENT_ID" && WEB_CLIENT_ID.isNotBlank()
        }
    }
}
