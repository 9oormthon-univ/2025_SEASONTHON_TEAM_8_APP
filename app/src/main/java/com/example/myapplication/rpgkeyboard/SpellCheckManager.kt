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

/**
 * 맞춤법 검사 UI 생성 및 더미 검사/수정 처리
 * - 오류 리스트를 카드 형태로 보여주고, 항목별 수정 또는 일괄 확인 제공
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
    }
    
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
     * 맞춤법 검사 결과 UI를 생성 (사진 스타일)
     */
    fun createSpellCheckResult(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val resultLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 16.dp(), 16.dp(), 0.dp())
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
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
                140.dp() // 통일된 높이
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
        }
        
        // 말풍선 텍스트 (맞춤법 수정된 ai 답변 반복)
        val bubbleText = TextView(context).apply {
            text = "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변\n" +
                   "맞춤법 수정된 ai 답변 맞춤법 수정된 ai 답변"
            textSize = 12f
            setTextColor(Color.WHITE) // 흰색 텍스트
            setTypeface(null, android.graphics.Typeface.NORMAL)
            lineHeight = 18.dp()
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        
        scrollView.addView(bubbleText)
        speechBubble.addView(scrollView)
        resultLayout.addView(speechBubble)
        
        // 체크마크 아이콘만 (가운데 정렬)
        val checkmarkContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 20.dp(), 0, 20.dp())
            }
            gravity = android.view.Gravity.CENTER
        }
        
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
            layoutParams = LayoutParams(40.dp(), 40.dp())
        }
        
        checkmarkContainer.addView(checkmarkIcon)
        resultLayout.addView(checkmarkContainer)
        
        return resultLayout
    }
    
    /**
     * 맞춤법 오류 항목 생성
     */
    private fun createErrorItem(
        error: SpellError, 
        inputConnection: InputConnection?
    ): LinearLayout {
        val errorLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(ERROR_PADDING_DP.dp(), ERROR_PADDING_DP.dp(), ERROR_PADDING_DP.dp(), ERROR_PADDING_DP.dp())
            setBackgroundColor(Color.parseColor(COLOR_ERROR_BG))
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, ERROR_MARGIN_DP, 0, ERROR_MARGIN_DP)
            }
        }
        
        // 오류 설명
        val errorText = TextView(context).apply {
            text = "❌ ${error.original} → ${error.corrected}"
            textSize = TEXT_SIZE_ERROR
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 4.dp())
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        errorLayout.addView(errorText)
        
        // 수정 버튼
        val fixButton = Button(context).apply {
            text = "수정"
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = (BUTTON_HEIGHT_DP * 0.7f).dp()
            }
            setOnClickListener { 
                inputConnection?.commitText(error.corrected, 1)
            }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp(), 
                      BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp())
            textSize = TEXT_SIZE_SMALL
            background = roundedBg(Color.parseColor(COLOR_ACCENT), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        errorLayout.addView(fixButton)
        
        return errorLayout
    }
    
    /**
     * 확인 버튼 생성
     */
    private fun createConfirmButton(onClick: () -> Unit): Button {
        return Button(context).apply {
            text = "확인"
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = BUTTON_HEIGHT_DP.dp()
                setMargins(0, 16.dp(), 0, 0)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp(), 
                      BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp())
            textSize = TEXT_SIZE_BUTTON
            background = roundedBg(Color.parseColor(COLOR_ACCENT), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
            elevation = BUTTON_ELEVATION_DP.dp().toFloat()
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
    
    /**
     * 더미 맞춤법 오류 데이터 반환
     */
    private fun getSpellErrors(@Suppress("UNUSED_PARAMETER") text: String): List<SpellError> {
        // 실제로는 AI로 맞춤법 검사를 수행
        // text 매개변수는 향후 AI 분석에 사용될 예정
        return listOf(
            SpellError("맞춤법", "맞춤법", "맞춤법"),
            SpellError("검사", "검사", "검사")
        )
    }
    
    /**
     * 수정된 텍스트 반환
     */
    private fun getCorrectedText(originalText: String, errors: List<SpellError>): String {
        var correctedText = originalText
        errors.forEach { error ->
            correctedText = correctedText.replace(error.original, error.corrected)
        }
        return "[수정됨] $correctedText"
    }
    
    /**
     * 맞춤법 오류 데이터 클래스
     */
    data class SpellError(
        val original: String,
        val corrected: String,
        val description: String
    )
}
