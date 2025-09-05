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
 * 리라이팅(문체 변환) UI를 생성하고 결과를 입력창에 반영
 * - 더미(샘플) 변환을 적용하며, 실제 AI 연결 시 이 부분만 교체하면 됨
 * - 무채색 기반의 통일된 UI 디자인 적용
 * - 키보드 크기에 맞춘 컴팩트한 디자인
 */
class RewritingManager(private val context: Context) {
    
    companion object {
        // AOS 다크 테마 색상 팔레트
        private const val COLOR_PRIMARY = "#FF2A2A2A"      // AOS/Dark/Primary
        private const val COLOR_SECONDARY = "#FF424242"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#FFE0E0E0"   // AOS/Dark/On Primary
        private const val COLOR_DARK_GRAY = "#FF000000"    // AOS/Dark/Secondary
        private const val COLOR_WHITE = "#FFE0E0E0"        // 밝은 회색
        private const val COLOR_BLACK = "#FF000000"        // 검은색
        
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
     * 그라데이션 배경을 생성하는 함수
     */
    private fun gradientBg(startColor: Int, endColor: Int, radiusDp: Float = 12f): GradientDrawable =
        GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)).apply {
            cornerRadius = radiusDp.dp().toFloat()
        }
    
    /**
     * 리라이팅 옵션 선택 UI를 생성 (사진 스타일)
     */
    fun createRewritingOptions(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val optionsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 16.dp(), 16.dp(), 0.dp()) // 하단 패딩 제거
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
                setMargins(0, 0, 0, 8.dp()) // 하단 마진 줄임 (16dp → 8dp)
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
        
        // 말풍선 텍스트 (8줄 반복)
        val bubbleText = TextView(context).apply {
            text = "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변\n" +
                   "사장님 안녕하세요, ㅇㅇㅇㅇㅇㅇ-ai의 수정된 답변"
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
        optionsLayout.addView(speechBubble)
        
        // 중간 영역 제거 - 말풍선과 버튼 사이에 공간만 추가
        // val spacer = View(context).apply {
        //     layoutParams = LayoutParams(
        //         ViewGroup.LayoutParams.MATCH_PARENT,
        //         20.dp() // 간격만 유지
        //     )
        // }
        // optionsLayout.addView(spacer)
        
        // 버튼들 (4개 가로 배치) - 하단 고정
        val buttonContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 0) // 하단 마진 제거
            }
            setPadding(0, 0, 0, 20.dp()) // 하단 패딩 추가로 버튼이 잘리지 않도록
        }
        
        // 버튼 옵션들 (사진과 동일)
        val buttonOptions = listOf(
            "더 격식있게",
            "친절하게", 
            "재미있게",
            "간결하게"
        )
        
        buttonOptions.forEach { buttonText ->
            val button = createStyleButton(buttonText) { 
                // 더미 데이터로 수정된 텍스트 적용
                val rewrittenText = getRewrittenText(currentText, buttonText)
                inputConnection?.commitText(rewrittenText, 1)
            }
            buttonContainer.addView(button)
        }
        
        // 체크마크 아이콘 (오른쪽 끝) - 더 둥글고 예쁘게
        val checkmarkIcon = TextView(context).apply {
            text = "✓"
            textSize = 18f // 폰트 크기 증가
            setTextColor(Color.WHITE)
            setPadding(12.dp(), 12.dp(), 12.dp(), 12.dp()) // 패딩 증가
            background = gradientBg(
                Color.parseColor("#FF34C2E8"), // 파란색
                Color.parseColor("#FF5AA0E6"), // 더 진한 파란색
                25f // 더 둥근 모서리
            )
            gravity = android.view.Gravity.CENTER
            elevation = 4f // 그림자 추가
            layoutParams = LayoutParams(40.dp(), 40.dp()).apply { // 크기 증가
                setMargins(12.dp(), 0, 0, 0) // 마진 증가
            }
        }
        buttonContainer.addView(checkmarkIcon)
        optionsLayout.addView(buttonContainer)
        
        return optionsLayout
    }
    
    /**
     * 스타일 버튼 생성 (더 둥글고 예쁜 디자인)
     */
    private fun createStyleButton(
        buttonText: String, 
        onClick: () -> Unit
    ): Button {
        return Button(context).apply {
            text = buttonText
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = 40.dp() // 높이 증가
                setMargins(6.dp(), 0, 6.dp(), 0) // 마진 증가
            }
            setOnClickListener { onClick() }
            setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp()) // 패딩 증가
            textSize = 12f // 폰트 크기 증가
            background = gradientBg(
                Color.parseColor("#FF87CEEB"), // 연한 파란색
                Color.parseColor("#FFB0E0E6"), // 더 연한 파란색
                20f // 더 둥근 모서리
            )
            setTextColor(Color.parseColor("#FF1E3A8A")) // 진한 파란색 텍스트
            elevation = 4f // 그림자 증가
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD) // 볼드 폰트
            // 버튼 상태별 색상 설정
            stateListAnimator = null // 기본 애니메이션 제거
        }
    }
    
    /**
     * 문체별 칩 생성 (가로 배치용) - 기존 함수 유지
     */
    private fun createStyleChip(
        styleName: String, 
        @Suppress("UNUSED_PARAMETER") exampleText: String, 
        onClick: () -> Unit
    ): Button {
        return Button(context).apply {
            text = styleName
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = 32.dp()
                setMargins(4.dp(), 0, 4.dp(), 0)
            }
            setOnClickListener { onClick() }
            setPadding(12.dp(), 6.dp(), 12.dp(), 6.dp())
            textSize = TEXT_SIZE_SMALL
            background = gradientBg(
                Color.parseColor("#CC34C2E8"), 
                Color.parseColor("#CCD1FFFF"), 
                20f
            )
            setTextColor(Color.parseColor(COLOR_LIGHT_GRAY))
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
            "더 격식있게" -> "[격식체] $originalText"
            "친절하게" -> "[친절체] $originalText"
            "재미있게" -> "[유머체] $originalText"
            "간결하게" -> "[간결체] $originalText"
            "공손체" -> "[공손체] $originalText"
            "친근체" -> "[친근체] $originalText"
            "단답체" -> "[단답체] $originalText"
            "격식체" -> "[격식체] $originalText"
            else -> originalText
        }
    }
}
