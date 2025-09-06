
package com.example.myapplication.keyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import android.widget.LinearLayout

/** AI 기능들의 오버레이 UI를 관리하는 클래스 리라이팅, 맞춤법 검사, 일정 추가 기능의 UI를 키보드 위에 표시 */
/** 키보드 상단의 기능 버튼과, 선택된 기능의 상세 화면(오버레이)을 키보드 레이아웃과 교대로 보여주는 컨트롤러 */
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

    /** 외부 컨테이너에 부착 (키보드와 교대로 표시될 영역) */
    /** 외부에서 제공한 컨테이너에 오버레이를 표시할 수 있도록 연결 onClose: 오버레이 닫힐 때 키보드를 다시 렌더링하는 콜백 */
    fun attach(container: FrameLayout, onClose: () -> Unit) {
        this.container = container
        this.onCloseCallback = onClose
    }

    /** AI 기능 버튼들을 생성하여 반환 */
    fun createAIFeatureButtons(
            onRewritingClick: () -> Unit,
            onSpellCheckClick: () -> Unit,
            onScheduleAddClick: () -> Unit,
            onBackToKeyboardClick: () -> Unit
    ): LinearLayout {
        return aiFeatureManager.createAIFeatureButtons(
                onRewritingClick = onRewritingClick,
                onSpellCheckClick = onSpellCheckClick,
                onScheduleAddClick = onScheduleAddClick,
                onBackToKeyboardClick = onBackToKeyboardClick
        )
    }

    /** 리라이팅 옵션 UI 표시 */
    fun showRewritingOptions(inputConnection: InputConnection?, currentText: String) {
        val rewritingUI = rewritingManager.createRewritingOptions(inputConnection, currentText)
        showOverlay(rewritingUI)
    }

    /** 맞춤법 검사 결과 UI 표시 */
    fun showSpellCheckResult(inputConnection: InputConnection?, currentText: String) {
        val spellCheckUI = spellCheckManager.createSpellCheckResult(inputConnection, currentText)
        showOverlay(spellCheckUI)
    }

    /** 일정 추가 UI 표시 */
    fun showScheduleAddUI(inputConnection: InputConnection?, currentText: String) {
        val scheduleUI = scheduleManager.createScheduleAddUI(inputConnection, currentText)
        showOverlay(scheduleUI)
    }

    /** 오버레이에 UI 표시 */
    /** 헤더(뒤로/닫기) + 콘텐츠를 래퍼에 담아 컨테이너에 추가 */
    private fun showOverlay(contentView: View) {
        val target = container ?: return
        target.removeAllViews()

        // 콘텐츠만 직접 컨테이너에 추가 (헤더 없음)
        target.addView(contentView)
    }

    /** 오버레이 숨기기 */
    /** 오버레이 숨김 및 콜백 호출로 키보드 복원 */
    fun hideOverlay() {
        container?.removeAllViews()
        onCloseCallback?.invoke()
    }

    /** 오버레이가 표시되어 있는지 확인 */
    fun isOverlayVisible(): Boolean {
        val target = container ?: return false
        return target.childCount > 0
    }
}
