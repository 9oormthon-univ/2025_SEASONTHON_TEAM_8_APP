package com.example.myapplication.keyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.view.inputmethod.InputConnection

/**
 * AI 기능들의 오버레이 UI를 관리하는 클래스
 * 리라이팅, 맞춤법 검사, 일정 추가 기능의 UI를 키보드 위에 표시
 */
class AIFeatureOverlay(private val context: Context) {
    
    private var container: FrameLayout? = null
    private var onCloseCallback: (() -> Unit)? = null
    private val aiFeatureManager: AIFeatureManager
    private val rewritingManager: RewritingManager
    private val spellCheckManager: SpellCheckManager
    private val scheduleManager: ScheduleManager
    
    init {
        aiFeatureManager = AIFeatureManager(context)
        rewritingManager = RewritingManager(context)
        spellCheckManager = SpellCheckManager(context)
        scheduleManager = ScheduleManager(context)
    }
    
    /**
     * 외부 컨테이너에 부착 (키보드와 교대로 표시될 영역)
     */
    fun attach(container: FrameLayout, onClose: () -> Unit) {
        this.container = container
        this.onCloseCallback = onClose
    }
    
    /**
     * AI 기능 버튼들을 생성하여 반환
     */
    fun createAIFeatureButtons(
        onRewritingClick: () -> Unit,
        onSpellCheckClick: () -> Unit,
        onScheduleAddClick: () -> Unit
    ): LinearLayout {
        return aiFeatureManager.createAIFeatureButtons(
            onRewritingClick = onRewritingClick,
            onSpellCheckClick = onSpellCheckClick,
            onScheduleAddClick = onScheduleAddClick
        )
    }
    
    /**
     * 리라이팅 옵션 UI 표시
     */
    fun showRewritingOptions(inputConnection: InputConnection?, currentText: String) {
        val rewritingUI = rewritingManager.createRewritingOptions(inputConnection, currentText)
        showOverlay(rewritingUI)
    }
    
    /**
     * 맞춤법 검사 결과 UI 표시
     */
    fun showSpellCheckResult(inputConnection: InputConnection?, currentText: String) {
        val spellCheckUI = spellCheckManager.createSpellCheckResult(inputConnection, currentText)
        showOverlay(spellCheckUI)
    }
    
    /**
     * 일정 추가 UI 표시
     */
    fun showScheduleAddUI(inputConnection: InputConnection?, currentText: String) {
        val scheduleUI = scheduleManager.createScheduleAddUI(inputConnection, currentText)
        showOverlay(scheduleUI)
    }
    
    /**
     * 오버레이에 UI 표시
     */
    private fun showOverlay(contentView: View) {
        val target = container ?: return
        target.removeAllViews()

        // 헤더 + 콘텐츠를 수직으로 담을 래퍼 레이아웃
        val wrapper = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val headerLayout = createHeaderLayout()
        wrapper.addView(headerLayout)
        wrapper.addView(contentView)

        target.addView(wrapper)
    }
    
    /**
     * 헤더 레이아웃 생성 (닫기 버튼 포함)
     */
    private fun createHeaderLayout(): LinearLayout {
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 8, 16, 8)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        // 키보드로 돌아가기 버튼
        val backToKeyboardButton = android.widget.Button(context).apply {
            text = "키보드로 돌아가기"
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { hideOverlay() }
            setPadding(12, 8, 12, 8)
            textSize = 14f
            setTextColor(Color.parseColor("#424242"))
            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 16f
                setColor(Color.parseColor("#E0E0E0"))
            }
        }
        headerLayout.addView(backToKeyboardButton)

        // 제목
        val titleText = android.widget.TextView(context).apply {
            text = "AI 기능"
            textSize = 16f
            setTextColor(Color.parseColor("#424242"))
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        headerLayout.addView(titleText)
        
        // 닫기 버튼
        val closeButton = android.widget.Button(context).apply {
            text = "✕"
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { hideOverlay() }
            setPadding(8, 8, 8, 8)
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 16f
                setColor(Color.parseColor("#E0E0E0"))
            }
        }
        headerLayout.addView(closeButton)
        
        return headerLayout
    }
    
    /**
     * 오버레이 숨기기
     */
    fun hideOverlay() {
        container?.removeAllViews()
        onCloseCallback?.invoke()
    }
    
    /**
     * 오버레이가 표시되어 있는지 확인
     */
    fun isOverlayVisible(): Boolean {
        val target = container ?: return false
        return target.childCount > 0
    }
}
