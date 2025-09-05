package com.example.myapplication.rpgkeyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
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
        // 무채색 색상 팔레트
        private const val COLOR_PRIMARY = "#424242"      // 진한 회색
        private const val COLOR_SECONDARY = "#757575"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#EEEEEE"   // 연한 회색
        private const val COLOR_DARK_GRAY = "#212121"    // 매우 진한 회색
        private const val COLOR_WHITE = "#FFFFFF"        // 흰색
        private const val COLOR_BLACK = "#000000"        // 검은색
        private const val COLOR_ACCENT = "#757575"       // 강조색 (중간 회색)
        private const val COLOR_ERROR_BG = "#F5F5F5"     // 오류 배경색 (연한 회색)
        
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
     * 맞춤법 검사 결과 UI를 생성
     */
    fun createSpellCheckResult(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val resultLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp())
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SCREEN_HEIGHT_DP.dp()
            )
            setBackgroundColor(Color.parseColor(COLOR_WHITE))
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "맞춤법 검사"
            textSize = TEXT_SIZE_TITLE
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, TITLE_MARGIN_BOTTOM_DP.dp())
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        resultLayout.addView(titleText)
        
        // 더미 맞춤법 오류들 (실제로는 AI로 검사)
        val spellErrors = getSpellErrors(currentText)
        
        if (spellErrors.isEmpty()) {
            // 오류가 없는 경우
            val noErrorText = TextView(context).apply {
                text = "✅ 맞춤법 오류 없음"
                textSize = TEXT_SIZE_ERROR
                setTextColor(Color.parseColor(COLOR_ACCENT))
                setPadding(0, 0, 0, ERROR_MARGIN_DP.dp())
                gravity = android.view.Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            resultLayout.addView(noErrorText)
        } else {
            // 오류가 있는 경우 (키보드 크기에 맞춰 줄임)
            spellErrors.take(2).forEach { error ->
                val errorLayout = createErrorItem(error, inputConnection)
                resultLayout.addView(errorLayout)
            }
        }
        
        // 확인 버튼
        val confirmButton = createConfirmButton {
            // 수정된 텍스트 적용
            val correctedText = getCorrectedText(currentText, spellErrors)
            inputConnection?.commitText(correctedText, 1)
        }
        resultLayout.addView(confirmButton)
        
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
