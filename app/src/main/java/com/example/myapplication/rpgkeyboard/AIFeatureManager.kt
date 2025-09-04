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

/**
 * 상단 기능 버튼 바를 생성/관리하는 헬퍼
 * - 리라이팅 / 맞춤법 / 일정추가 버튼을 균등 폭으로 생성
 * - 클릭 콜백은 외부(AIFeatureOverlay/Service)에서 주입
 */
class AIFeatureManager(private val context: Context) {
    
    companion object {
        // 색상 - 현대적인 색상 팔레트
        private const val COLOR_PRIMARY = "#6366F1"      // 인디고
        private const val COLOR_PRIMARY_LIGHT = "#818CF8" // 밝은 인디고
        private const val COLOR_SECONDARY = "#EC4899"     // 핑크
        private const val COLOR_SECONDARY_LIGHT = "#F472B6" // 밝은 핑크
        private const val COLOR_ACCENT = "#10B981"        // 에메랄드
        private const val COLOR_ACCENT_LIGHT = "#34D399"  // 밝은 에메랄드
        private const val COLOR_NEUTRAL = "#F8FAFC"       // 매우 밝은 회색
        private const val COLOR_TEXT_PRIMARY = "#1E293B"  // 진한 회색
        private const val COLOR_TEXT_SECONDARY = "#64748B" // 중간 회색
        
        // 크기
        private const val BUTTON_HEIGHT_DP = 52
        private const val BUTTON_PADDING_HORIZONTAL_DP = 4
        private const val BUTTON_PADDING_VERTICAL_DP = 12
        private const val BUTTON_MARGIN_DP = 3
        private const val BUTTON_CORNER_RADIUS_DP = 12f
        private const val BUTTON_ELEVATION_DP = 4f
        
        // 폰트 크기
        private const val TEXT_SIZE_SMALL = 13f
        private const val ICON_SIZE = 20f
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
     * 그라데이션 배경을 생성하는 함수
     */
    private fun gradientBg(startColor: Int, endColor: Int, radiusDp: Float = 12f): GradientDrawable =
        GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)).apply {
            cornerRadius = radiusDp.dp().toFloat()
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
            setPadding(0, 0, 0, 0)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        // 키보드로 돌아가기 버튼 (아이콘)
        val backToKeyboardButton = createIconButton("⌨", onBackToKeyboardClick, COLOR_NEUTRAL, COLOR_TEXT_SECONDARY)
        featureLayout.addView(backToKeyboardButton)
        
        // 리라이팅 버튼 (인디고 그라데이션)
        val rewritingButton = createFeatureButton("리라이팅", onRewritingClick, COLOR_PRIMARY, COLOR_PRIMARY_LIGHT)
        featureLayout.addView(rewritingButton)
        
        // 맞춤법 버튼 (핑크 그라데이션)
        val spellCheckButton = createFeatureButton("맞춤법", onSpellCheckClick, COLOR_SECONDARY, COLOR_SECONDARY_LIGHT)
        featureLayout.addView(spellCheckButton)
        
        // 일정추가 버튼 (에메랄드 그라데이션)
        val scheduleButton = createFeatureButton("일정추가", onScheduleAddClick, COLOR_ACCENT, COLOR_ACCENT_LIGHT)
        featureLayout.addView(scheduleButton)
        
        return featureLayout
    }
    
    /**
     * 개별 기능 버튼 생성
     */
    private fun createFeatureButton(text: String, onClick: () -> Unit, startColor: String, endColor: String): Button {
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
            background = gradientBg(Color.parseColor(startColor), Color.parseColor(endColor), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.WHITE)
            elevation = BUTTON_ELEVATION_DP
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
    
    /**
     * 아이콘 버튼 생성 (키보드로 돌아가기용)
     */
    private fun createIconButton(icon: String, onClick: () -> Unit, bgColor: String, textColor: String): Button {
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
            this.textSize = ICON_SIZE // 아이콘 크기
            background = roundedBg(Color.parseColor(bgColor), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(textColor))
            elevation = BUTTON_ELEVATION_DP
        }
    }
}
