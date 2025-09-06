
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

/**
 * 상단 기능 버튼 바를 생성/관리하는 헬퍼
 * - 리라이팅 / 맞춤법 / 일정추가 버튼을 균등 폭으로 생성
 * - 클릭 콜백은 외부(AIFeatureOverlay/Service)에서 주입
 */
class AIFeatureManager(private val context: Context) {
    
    companion object {
        // 색상
        private const val COLOR_PRIMARY = "#2196F3"
        private const val COLOR_LIGHT_GRAY = "#E0E0E0"
        private const val COLOR_DARK_GRAY = "#424242"
        
        // 크기
        private const val BUTTON_HEIGHT_DP = 48
        private const val BUTTON_PADDING_HORIZONTAL_DP = 2
        private const val BUTTON_PADDING_VERTICAL_DP = 8
        private const val BUTTON_MARGIN_DP = 2
        private const val BUTTON_CORNER_RADIUS_DP = 8f
        private const val BUTTON_ELEVATION_DP = 2f
        
        // 폰트 크기
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
     * AI 기능 버튼들을 생성
     */
    fun createAIFeatureButtons(
        onRewritingClick: () -> Unit,
        onSpellCheckClick: () -> Unit,
        onScheduleAddClick: () -> Unit,
        onBackToKeyboardClick: () -> Unit
    ): LinearLayout {
        val featureLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 8)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        // 키보드로 돌아가기 버튼 (아이콘)
        val backToKeyboardButton = createIconButton("⌨", onBackToKeyboardClick)
        featureLayout.addView(backToKeyboardButton)
        
        // 리라이팅 버튼
        val rewritingButton = createFeatureButton("리라이팅", onRewritingClick)
        featureLayout.addView(rewritingButton)
        
        
        
        // 맞춤법 버튼
        val spellCheckButton = createFeatureButton("맞춤법", onSpellCheckClick)
        featureLayout.addView(spellCheckButton)
        
        // 일정추가 버튼
        val scheduleButton = createFeatureButton("일정추가", onScheduleAddClick)
        featureLayout.addView(scheduleButton)
        
        return featureLayout
    }
    
    /**
     * 개별 기능 버튼 생성
     */
    private fun createFeatureButton(text: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            this.text = text
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
                height = BUTTON_HEIGHT_DP.dp()
                setMargins(BUTTON_MARGIN_DP, BUTTON_MARGIN_DP, BUTTON_MARGIN_DP, BUTTON_MARGIN_DP)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP, 
                      BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP)
            this.textSize = TEXT_SIZE_SMALL
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            elevation = BUTTON_ELEVATION_DP
        }
    }
    
    /**
     * 아이콘 버튼 생성 (키보드로 돌아가기용)
     */
    private fun createIconButton(icon: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            this.text = icon
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = BUTTON_HEIGHT_DP.dp()
                width = BUTTON_HEIGHT_DP.dp() // 정사각형 버튼
                setMargins(BUTTON_MARGIN_DP, BUTTON_MARGIN_DP, BUTTON_MARGIN_DP, BUTTON_MARGIN_DP)
            }
            setOnClickListener { onClick() }
            setPadding(0, 0, 0, 0) // 아이콘은 패딩 없이
            this.textSize = 18f // 아이콘 크기
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            elevation = BUTTON_ELEVATION_DP
        }
    }
}
