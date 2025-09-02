package com.example.myapplication.keyboard

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
 * 맞춤법 검사 기능을 담당하는 클래스
 * 작성 중인 텍스트의 맞춤법을 검사하고 수정 제안
 */
class SpellCheckManager(private val context: Context) {
    
    companion object {
        // 색상
        private const val COLOR_PRIMARY = "#2196F3"
        private const val COLOR_LIGHT_GRAY = "#E0E0E0"
        private const val COLOR_DARK_GRAY = "#424242"
        private const val COLOR_WHITE = "#FFFFFF"
        private const val COLOR_RED = "#F44336"
        private const val COLOR_GREEN = "#4CAF50"
        
        // 크기
        private const val BUTTON_HEIGHT_DP = 48
        private const val BUTTON_PADDING_HORIZONTAL_DP = 8
        private const val BUTTON_PADDING_VERTICAL_DP = 12
        private const val BUTTON_MARGIN_DP = 4
        private const val BUTTON_CORNER_RADIUS_DP = 8f
        private const val BUTTON_ELEVATION_DP = 2f
        
        // 폰트 크기
        private const val TEXT_SIZE_MEDIUM = 14f
        private const val TEXT_SIZE_SMALL = 12f
    }
    
    /**
     * dp 단위를 픽셀로 변환하는 확장 함수
     */
    private fun Float.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
    private fun Int.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
    
    /**
     * 둥근 모서리를 가진 배경을 생성하는 함수
     */
    private fun roundedBg(color: Int, radiusDp: Float = 10f): GradientDrawable =
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
            setPadding(16, 16, 16, 16)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor(COLOR_WHITE))
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "맞춤법 검사 결과"
            textSize = 16f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 16)
        }
        resultLayout.addView(titleText)
        
        // 검사된 텍스트 표시
        val originalText = TextView(context).apply {
            text = "검사된 텍스트: $currentText"
            textSize = 14f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 8)
        }
        resultLayout.addView(originalText)
        
        // 더미 맞춤법 오류들 (실제로는 AI로 검사)
        val spellErrors = getSpellErrors(currentText)
        
        if (spellErrors.isEmpty()) {
            // 오류가 없는 경우
            val noErrorText = TextView(context).apply {
                text = "✅ 맞춤법 오류가 발견되지 않았습니다."
                textSize = 14f
                setTextColor(Color.parseColor(COLOR_GREEN))
                setPadding(0, 0, 0, 16)
            }
            resultLayout.addView(noErrorText)
        } else {
            // 오류가 있는 경우
            spellErrors.forEach { error ->
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
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.parseColor("#FFF3E0"))
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, BUTTON_MARGIN_DP, 0, BUTTON_MARGIN_DP)
            }
        }
        
        // 오류 설명
        val errorText = TextView(context).apply {
            text = "❌ ${error.original} → ${error.corrected}"
            textSize = 14f
            setTextColor(Color.parseColor(COLOR_RED))
            setPadding(0, 0, 0, 4)
        }
        errorLayout.addView(errorText)
        
        // 수정 버튼
        val fixButton = Button(context).apply {
            text = "수정하기"
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = (BUTTON_HEIGHT_DP * 0.7f).dp()
            }
            setOnClickListener { 
                inputConnection?.commitText(error.corrected, 1)
            }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP, 
                      BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP)
            textSize = TEXT_SIZE_SMALL
            background = roundedBg(Color.parseColor(COLOR_PRIMARY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
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
                setMargins(0, 16, 0, 0)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP, 
                      BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP)
            textSize = TEXT_SIZE_MEDIUM
            background = roundedBg(Color.parseColor(COLOR_PRIMARY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
            elevation = BUTTON_ELEVATION_DP
        }
    }
    
    /**
     * 더미 맞춤법 오류 데이터 반환
     */
    private fun getSpellErrors(text: String): List<SpellError> {
        // 실제로는 AI로 맞춤법 검사를 수행
        // text 매개변수는 향후 AI 분석에 사용될 예정
        return listOf(
            SpellError("맞춤법", "맞춤법", "맞춤법"),
            SpellError("검사", "검사", "검사"),
            SpellError("기능", "기능", "기능")
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
