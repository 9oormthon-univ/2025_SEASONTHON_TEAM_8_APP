package com.example.myapplication.rpgkeyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.widget.ScrollView
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.graphics.drawable.GradientDrawable
import android.view.inputmethod.InputConnection
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.example.myapplication.BuildConfig

/**
 * API 요청/응답 데이터 클래스
 */
data class SpellCheckRequest(
    val text: String,
    val language: String = "korean"
)

data class SpellCheckResponse(
    val original_text: String,
    val corrected_text: String,
    val language: String,
    val corrections_made: Boolean
)

/**
 * 맞춤법 검사 UI 생성 및 실제 API 통신 처리
 * - 백엔드 API를 통해 맞춤법 검사 및 교정
 * - 무채색 기반의 통일된 UI 디자인 적용
 * - 키보드 크기에 맞춘 컴팩트한 디자인
 */
class SpellCheckManager(private val context: Context) {
    
    companion object {
        // AOS 다크 테마 색상 팔레트
        private const val COLOR_PRIMARY = "#FF2A2A2A"      // AOS/Dark/Primary
        private const val COLOR_SECONDARY = "#FF424242"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#FFE0E0E0"   // AOS/Dark/On Primary
        private const val COLOR_DARK_GRAY = "#FF000000"    // AOS/Dark/Secondary
        private const val COLOR_WHITE = "#FFE0E0E0"        // 밝은 회색
        private const val COLOR_BLACK = "#FF000000"        // 검은색
        private const val COLOR_ACCENT = "#FF4CAF50"       // 강조색 (에메랄드)
        private const val COLOR_ERROR_BG = "#FF1A1A1A"     // 오류 배경색 (어두운 회색)
        
        // 키보드 크기에 맞춘 크기 상수
        private const val SCREEN_HEIGHT_DP = 280         // 키보드 높이
        private const val BUTTON_HEIGHT_DP = 40          // 키보드에 적합한 버튼 높이
        private const val BUTTON_PADDING_HORIZONTAL_DP = 8
        private const val BUTTON_PADDING_VERTICAL_DP = 8
        private const val BUTTON_MARGIN_DP = 4
        private const val BUTTON_CORNER_RADIUS_DP = 6f
        private const val BUTTON_ELEVATION_DP = 1f
        private const val CARD_PADDING_DP = 12
        private const val TITLE_MARGIN_BOTTOM_DP = 12
        private const val ERROR_MARGIN_DP = 6
        private const val ERROR_PADDING_DP = 8
        
        // 폰트 크기
        private const val TEXT_SIZE_TITLE = 14f
        private const val TEXT_SIZE_BUTTON = 12f
        private const val TEXT_SIZE_ERROR = 12f
        private const val TEXT_SIZE_SMALL = 10f
        
        // API 설정
        private const val API_ENDPOINT = "/text/spell-check"
    }
    
    // HTTP 클라이언트
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(BuildConfig.API_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(BuildConfig.API_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(BuildConfig.API_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .build()
    
    // 코루틴 스코프
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * dp 단위를 픽셀로 변환하는 확장 함수
     */
    private fun Float.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
    private fun Int.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
    
    /**
     * 둥근 모서리를 가진 배경을 생성하는 함수
     */
    private fun roundedBg(color: Int, radiusDp: Float = 6f): GradientDrawable =
        GradientDrawable().apply {
            cornerRadius = radiusDp.dp().toFloat()
            setColor(color)
        }
    
    /**
     * 그라데이션 배경을 생성하는 함수
     */
    private fun gradientBg(startColor: Int, endColor: Int, radiusDp: Float = 12f): GradientDrawable =
        GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)).apply {
            cornerRadius = radiusDp.dp().toFloat()
        }
    
    /**
     * API를 통해 맞춤법 검사 요청
     */
    private suspend fun spellCheckWithAPI(
        text: String,
        authToken: String?,
        onSuccess: (String, Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            println("SpellCheckManager: API 호출 시작 - 텍스트: $text")
            
            val requestBody = JSONObject().apply {
                put("text", text)
                put("language", "korean")
            }.toString()
            
            println("SpellCheckManager: 요청 본문: $requestBody")
            
            val requestBuilder = Request.Builder()
                .url("${BuildConfig.API_BASE_URL}$API_ENDPOINT")
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")
            
            // JWT 토큰이 있으면 Authorization 헤더 추가 (테스트용으로 기본 토큰 사용)
            val tokenToUse = if (!authToken.isNullOrEmpty()) authToken else BuildConfig.TEST_JWT_TOKEN
            requestBuilder.addHeader("Authorization", "Bearer $tokenToUse")
            
            val request = requestBuilder.build()
            println("SpellCheckManager: 요청 URL: ${request.url}")
            
            val response = httpClient.newCall(request).execute()
            println("SpellCheckManager: 응답 코드: ${response.code}")
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                println("SpellCheckManager: 응답 본문: $responseBody")
                
                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val correctedText = jsonResponse.getString("corrected_text")
                    val correctionsMade = jsonResponse.getBoolean("corrections_made")
                    println("SpellCheckManager: 교정된 텍스트: $correctedText, 교정 여부: $correctionsMade")
                    onSuccess(correctedText, correctionsMade)
                } else {
                    println("SpellCheckManager: 응답 본문이 null")
                    onError("응답 데이터가 없습니다.")
                }
            } else {
                println("SpellCheckManager: API 요청 실패 - ${response.code} ${response.message}")
                onError("API 요청 실패: ${response.code} ${response.message}")
            }
        } catch (e: Exception) {
            println("SpellCheckManager: 예외 발생 - ${e.message}")
            e.printStackTrace()
            onError("네트워크 오류: ${e.message}")
        }
    }
    
    /**
     * 맞춤법 검사 결과 UI를 생성 (사진 스타일)
     */
    fun createSpellCheckResult(
        inputConnection: InputConnection?,
        currentText: String,
        authToken: String? = null
    ): LinearLayout {
        val resultLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 16.dp(), 16.dp(), 0.dp())
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                280.dp() // 고정 높이로 통일
            )
            setBackgroundColor(Color.BLACK) // 검은색 배경
        }
        
        // 말풍선 (다크 그레이 배경, 흰색 텍스트) - 스크롤 가능
        val speechBubble = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20.dp(), 16.dp(), 20.dp(), 16.dp())
            background = roundedBg(Color.parseColor("#FF424242"), 12f) // 다크 그레이
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                180.dp() // 고정 높이로 설정
            ).apply {
                setMargins(0, 0, 0, 8.dp())
            }
        }
        
        // 스크롤뷰로 감싸기
        val scrollView = ScrollView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isVerticalScrollBarEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setPadding(0, 0, 0, 0) // 패딩 제거
        }
        
        // 말풍선 텍스트 (실제 사용자 입력 텍스트)
        val bubbleText = TextView(context).apply {
            text = if (currentText.isNotEmpty()) {
                "맞춤법 검사 중... 잠시만 기다려주세요."
            } else {
                "맞춤법을 검사할 텍스트를 입력해주세요..."
            }
            textSize = 16f // 폰트 크기 더 증가
            setTextColor(Color.WHITE) // 흰색 텍스트
            setTypeface(null, android.graphics.Typeface.BOLD) // 볼드로 변경
            lineHeight = 24.dp() // 줄 간격 더 증가
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 12.dp(), 0, 12.dp()) // 상하 패딩 더 증가
            gravity = android.view.Gravity.CENTER_VERTICAL // 수직 중앙 정렬
        }
        
        // 수정된 텍스트를 저장할 변수
        var modifiedText = ""
        
        // 텍스트가 있으면 바로 맞춤법 검사 시작
        if (currentText.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    spellCheckWithAPI(
                        text = currentText,
                        authToken = authToken,
                        onSuccess = { correctedText, correctionsMade ->
                            // UI 스레드에서 실행
                            coroutineScope.launch(Dispatchers.Main) {
                                modifiedText = correctedText
                                val statusText = if (correctionsMade) {
                                    "맞춤법 교정 완료: $correctedText\n\n체크마크를 눌러 적용하세요"
                                } else {
                                    "맞춤법 오류 없음: $correctedText"
                                }
                                bubbleText.text = statusText
                            }
                        },
                        onError = { errorMessage ->
                            // UI 스레드에서 실행
                            coroutineScope.launch(Dispatchers.Main) {
                                bubbleText.text = "오류: $errorMessage"
                            }
                        }
                    )
                } catch (e: Exception) {
                    // 예외 발생 시 UI 스레드에서 실행
                    coroutineScope.launch(Dispatchers.Main) {
                        bubbleText.text = "예외 발생: ${e.message}"
                    }
                }
            }
        }
        
        scrollView.addView(bubbleText)
        speechBubble.addView(scrollView)
        resultLayout.addView(speechBubble)
        
        // 하단 컨테이너 (체크마크만 오른쪽 하단)
        val bottomContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 0)
            }
            setPadding(0, 0, 0, 20.dp())
            gravity = android.view.Gravity.BOTTOM
        }
        
        // 빈 공간 (왼쪽)
        val spacer = View(context).apply {
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        bottomContainer.addView(spacer)
        
        // 체크마크 아이콘 (오른쪽 하단)
        val checkmarkIcon = TextView(context).apply {
            text = "✓"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(12.dp(), 12.dp(), 12.dp(), 12.dp())
            background = gradientBg(
                Color.parseColor("#FF34C2E8"), // 파란색
                Color.parseColor("#FF5AA0E6"), // 더 진한 파란색
                25f // 더 둥근 모서리
            )
            gravity = android.view.Gravity.CENTER
            elevation = 4f
            layoutParams = LayoutParams(40.dp(), 40.dp()).apply {
                setMargins(0, 0, 0, 0)
            }
            setOnClickListener {
                if (modifiedText.isNotEmpty()) {
                    // 교정된 텍스트가 있으면 현재 커서 위치의 텍스트만 교체
                    // 1. 현재 커서 위치의 텍스트 길이만큼 삭제
                    inputConnection?.deleteSurroundingText(currentText.length, 0)
                    // 2. 교정된 텍스트 삽입
                    inputConnection?.commitText(modifiedText, 1)
                    bubbleText.text = "맞춤법 교정이 적용되었습니다: $modifiedText"
                    modifiedText = "" // 적용 후 초기화
                } else {
                    // 교정된 텍스트가 없으면 대기 메시지
                    bubbleText.text = "맞춤법 검사 중입니다... 잠시만 기다려주세요."
                }
            }
        }
        bottomContainer.addView(checkmarkIcon)
        resultLayout.addView(bottomContainer)
        
        return resultLayout
    }
    
    
    /**
     * 리소스 정리
     */
    fun cleanup() {
        coroutineScope.cancel()
        httpClient.dispatcher.executorService.shutdown()
    }
}
