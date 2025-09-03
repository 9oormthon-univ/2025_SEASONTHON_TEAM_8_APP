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
 */
class RewritingManager(private val context: Context) {
    
    companion object {
        // 색상
        private const val COLOR_PRIMARY = "#2196F3"
        private const val COLOR_LIGHT_GRAY = "#E0E0E0"
        private const val COLOR_DARK_GRAY = "#424242"
        private const val COLOR_WHITE = "#FFFFFF"
        
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
     * 리라이팅 옵션 선택 UI를 생성
     */
    fun createRewritingOptions(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val optionsLayout = LinearLayout(context).apply {
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
            text = "문체 선택"
            textSize = 16f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 16)
        }
        optionsLayout.addView(titleText)
        
        // 문체 옵션들
        val styleOptions = listOf(
            "공손체" to "안녕하십니까. 오늘도 좋은 하루 되시기 바랍니다.",
            "친근체" to "안녕! 오늘도 좋은 하루 보내!",
            "단답체" to "안녕. 좋은 하루.",
            "격식체" to "안녕하세요. 오늘도 좋은 하루 되시길 바랍니다.",
            "반말체" to "안녕! 오늘도 좋은 하루 보내!"
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
            setPadding(BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP, 
                      BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP)
            textSize = TEXT_SIZE_SMALL
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            elevation = BUTTON_ELEVATION_DP
            gravity = android.view.Gravity.START
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
            "반말체" -> "[반말체] $originalText"
            else -> originalText
        }
    }
}
