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
 * 리라이팅(문체 변환) UI를 생성하고 결과를 입력창에 반영
 * - 더미(샘플) 변환을 적용하며, 실제 AI 연결 시 이 부분만 교체하면 됨
 * - 무채색 기반의 통일된 UI 디자인 적용
 * - 키보드 크기에 맞춘 컴팩트한 디자인
 */
class RewritingManager(private val context: Context) {
    
    companion object {
        // 무채색 색상 팔레트
        private const val COLOR_PRIMARY = "#424242"      // 진한 회색
        private const val COLOR_SECONDARY = "#757575"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#EEEEEE"   // 연한 회색
        private const val COLOR_DARK_GRAY = "#212121"    // 매우 진한 회색
        private const val COLOR_WHITE = "#FFFFFF"        // 흰색
        private const val COLOR_BLACK = "#000000"        // 검은색
        
        // 키보드 크기에 맞춘 크기 상수
        private const val SCREEN_WIDTH_DP = 280          // 키보드 너비
        private const val SCREEN_HEIGHT_DP = 200         // 키보드 높이
        private const val BUTTON_HEIGHT_DP = 40          // 키보드에 적합한 버튼 높이
        private const val BUTTON_PADDING_HORIZONTAL_DP = 8
        private const val BUTTON_PADDING_VERTICAL_DP = 8
        private const val BUTTON_MARGIN_DP = 4
        private const val BUTTON_CORNER_RADIUS_DP = 6f
        private const val BUTTON_ELEVATION_DP = 1f
        private const val CARD_PADDING_DP = 12
        private const val TITLE_MARGIN_BOTTOM_DP = 12
        
        // 폰트 크기
        private const val TEXT_SIZE_TITLE = 14f
        private const val TEXT_SIZE_BUTTON = 12f
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
     * 리라이팅 옵션 선택 UI를 생성
     */
    fun createRewritingOptions(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val optionsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp())
            layoutParams = LayoutParams(
                SCREEN_WIDTH_DP.dp(),
                SCREEN_HEIGHT_DP.dp()
            )
            setBackgroundColor(Color.parseColor(COLOR_WHITE))
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "문체 선택"
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
        optionsLayout.addView(titleText)
        
        // 문체 옵션들 (키보드 크기에 맞춰 줄임)
        val styleOptions = listOf(
            "공손체" to "안녕하십니까.",
            "친근체" to "안녕!",
            "단답체" to "안녕.",
            "격식체" to "안녕하세요."
        )
        
        styleOptions.forEach { (styleName, exampleText) ->
            val styleButton = createStyleButton(styleName, exampleText) { 
                // 더미 데이터로 수정된 텍스트 적용
                val rewrittenText = getRewrittenText(currentText, styleName)
                inputConnection?.commitText(rewrittenText, 1)
            }
            optionsLayout.addView(styleButton)
        }
        
        return optionsLayout
    }
    
    /**
     * 문체별 버튼 생성
     */
    private fun createStyleButton(
        styleName: String, 
        exampleText: String, 
        onClick: () -> Unit
    ): Button {
        return Button(context).apply {
            text = "$styleName\n$exampleText"
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = BUTTON_HEIGHT_DP.dp()
                setMargins(0, BUTTON_MARGIN_DP, 0, BUTTON_MARGIN_DP)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp(), 
                      BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp())
            textSize = TEXT_SIZE_BUTTON
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            elevation = BUTTON_ELEVATION_DP.dp().toFloat()
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }
    
    /**
     * 더미 데이터로 리라이팅된 텍스트 반환
     */
    private fun getRewrittenText(originalText: String, style: String): String {
        return when (style) {
            "공손체" -> "[공손체] $originalText"
            "친근체" -> "[친근체] $originalText"
            "단답체" -> "[단답체] $originalText"
            "격식체" -> "[격식체] $originalText"
            else -> originalText
        }
    }
}
