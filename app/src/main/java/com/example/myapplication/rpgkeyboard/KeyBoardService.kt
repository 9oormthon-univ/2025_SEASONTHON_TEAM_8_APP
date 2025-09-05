package com.example.myapplication.rpgkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.util.Log
import com.example.myapplication.rpgkeyboard.keyboardview.*
import com.example.myapplication.R


class KeyBoardService : InputMethodService(){
    companion object {
        private const val TAG = "RPGKeyBoardService"
    }
    
    lateinit var keyboardView:LinearLayout
    lateinit var keyboardFrame:FrameLayout
    lateinit var aiButtonsFrame:FrameLayout
    lateinit var keyboardKorean:KeyboardKorean
    lateinit var keyboardEnglish:KeyboardEnglish
    lateinit var keyboardSimbols:KeyboardSimbols
    var isQwerty = 0 // shared preference에 데이터를 저장하고 불러오는 기능 필요
    
    // AI 기능 관련
    private lateinit var aiFeatureOverlay: AIFeatureOverlay
    private var currentInputText = ""


    val keyboardInterationListener = object:KeyboardInterationListener{
        //inputconnection이 null일경우 재요청하는 부분 필요함
        override fun modechange(mode: Int) {
            // 키보드 프레임만 초기화 (AI 버튼은 별도 프레임에 있음)
            keyboardFrame.removeAllViews()
            
            // 키보드 모드만 변경
            showKeyboardMode(mode)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "RPGKeyBoardService onCreate")
        
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)
        
        // AI 기능 오버레이 초기화
        aiFeatureOverlay = AIFeatureOverlay(this)
        aiFeatureOverlay.attach(
            container = keyboardFrame,
            onClose = {
                // AI 오버레이 닫힐 때 키보드 다시 표시
                renderKeyboard()
            }
        )
    }

    override fun onCreateInputView(): View {
        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardSimbols = KeyboardSimbols(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardKorean.inputConnection = currentInputConnection
        keyboardKorean.init()
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardEnglish.init()
        keyboardSimbols.inputConnection = currentInputConnection
        keyboardSimbols.init()

        // AI 버튼 프레임 초기화
        aiButtonsFrame = keyboardView.findViewById(R.id.ai_buttons_frame)

        return keyboardView
    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            // AI 버튼 프레임 초기화
            aiButtonsFrame.removeAllViews()
            
            // AI 기능 버튼들 추가
            val aiButtons = aiFeatureOverlay.createAIFeatureButtons(
                onRewritingClick = { showRewritingOptions() },
                onSpellCheckClick = { showSpellCheckResult() },
                onScheduleAddClick = { showScheduleAddUI() }
            )
            aiButtonsFrame.addView(aiButtons)
            
            // 키보드 프레임 초기화
            keyboardFrame.removeAllViews()
            
            // 숫자 자판 추가
            keyboardFrame.addView(KeyboardNumpad.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
        }
        else{
            renderKeyboard()
        }
    }
    
    // ==================== AI 기능 메서드들 ====================
    
    /**
     * 키보드 렌더링 (AI 기능 버튼 포함)
     */
    private fun renderKeyboard() {
        // AI 버튼 프레임 초기화
        aiButtonsFrame.removeAllViews()
        
        // AI 기능 버튼들 추가
        val aiButtons = aiFeatureOverlay.createAIFeatureButtons(
            onRewritingClick = { showRewritingOptions() },
            onSpellCheckClick = { showSpellCheckResult() },
            onScheduleAddClick = { showScheduleAddUI() }
        )
        aiButtonsFrame.addView(aiButtons)
        
        // 키보드 프레임 초기화
        keyboardFrame.removeAllViews()
        
        // 기본 키보드 표시
        showKeyboardMode(1)
    }
    
    /**
     * AI 버튼을 유지하면서 키보드 모드만 변경
     */
    private fun showKeyboardMode(mode: Int) {
        currentInputConnection.finishComposingText()
        
        when(mode){
            0 ->{
                keyboardEnglish.inputConnection = currentInputConnection
                keyboardFrame.addView(keyboardEnglish.getLayout())
            }
            1 -> {
                if(isQwerty == 0){
                    keyboardKorean.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardKorean.getLayout())
                }
                else{
                    keyboardFrame.addView(KeyboardChunjiin.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
                }
            }
            2 -> {
                keyboardSimbols.inputConnection = currentInputConnection
                keyboardFrame.addView(keyboardSimbols.getLayout())
            }
            3 -> {
                keyboardFrame.addView(KeyboardEmoji.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
            }
        }
    }
    
    /**
     * 리라이팅 옵션 표시
     */
    private fun showRewritingOptions() {
        val text = getSelectedOrContextText()
        aiFeatureOverlay.showRewritingOptions(currentInputConnection, text)
    }
    
    /**
     * 맞춤법 검사 결과 표시
     */
    private fun showSpellCheckResult() {
        val text = getSelectedOrContextText()
        aiFeatureOverlay.showSpellCheckResult(currentInputConnection, text)
    }
    
    /**
     * 일정 추가 UI 표시
     */
    private fun showScheduleAddUI() {
        val text = getSelectedOrContextText()
        aiFeatureOverlay.showScheduleAddUI(currentInputConnection, text)
    }
    
    /**
     * 현재 선택된 텍스트가 있으면 해당 범위를, 없으면 커서 주변 컨텍스트를 추출
     */
    private fun getSelectedOrContextText(): String {
        val ic = currentInputConnection ?: return currentInputText

        // 1) 선택된 텍스트 우선
        try {
            val selected = ic.getSelectedText(0)
            if (selected != null && selected.isNotEmpty()) {
                return selected.toString()
            }
        } catch (_: Exception) {}

        // 2) ExtractedText로 전체/부분 텍스트 가져오기
        try {
            val req = ExtractedTextRequest()
            val extracted: ExtractedText? = ic.getExtractedText(req, 0)
            if (extracted != null) {
                val text = extracted.text?.toString() ?: ""
                val start = extracted.selectionStart.coerceAtLeast(0)
                val end = extracted.selectionEnd.coerceAtLeast(start)
                if (text.isNotEmpty()) {
                    if (start == end) {
                        val left = (start - 60).coerceAtLeast(0)
                        val right = (start + 60).coerceAtMost(text.length)
                        return text.substring(left, right)
                    } else {
                        return text.substring(start, end)
                    }
                }
            }
        } catch (_: Exception) {}

        // 3) 주변 텍스트 API
        try {
            val before = ic.getTextBeforeCursor(60, 0) ?: ""
            val after = ic.getTextAfterCursor(60, 0) ?: ""
            val combined = before.toString() + after.toString()
            if (combined.isNotEmpty()) return combined
        } catch (_: Exception) {}

        return currentInputText
    }

}
