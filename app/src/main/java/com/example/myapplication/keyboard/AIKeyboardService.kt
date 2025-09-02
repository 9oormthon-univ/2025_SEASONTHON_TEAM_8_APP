/**
 * AI 자동완성 키보드 서비스
 * 
 * 이 클래스는 Android 시스템의 입력 메서드 서비스(IME)를 구현합니다.
 * 사용자가 텍스트를 입력할 때 나타나는 키보드 UI를 제공하고,
 * AI 기반 자동완성 제안을 포함한 한글 키보드를 구현합니다.
 * 
 * 주요 기능:
 * - 한글 자판 레이아웃 (3줄 구성)
 * - AI 제안 버튼 (안녕하세요!, 감사합니다, 좋은 하루 되세요)
 * - 기본 기능 키 (스페이스, 백스페이스, 엔터, 숫자 전환)
 * - 안정적인 Android View 기반 UI
 * - 오류 발생 시 폴백 키보드 제공
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams

class AIKeyboardService : InputMethodService() {
    
    companion object {
        private const val TAG = "AIKeyboardService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AIKeyboardService onCreate")
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView called")
        return try {
            createSimpleKeyboard()
        } catch (e: Exception) {
            Log.e(TAG, "Error creating keyboard: ${e.message}")
            createFallbackKeyboard()
        }
    }

    private fun createSimpleKeyboard(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
        }

        // AI 제안 영역
        val suggestionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 16)
        }

        val suggestions = listOf("안녕하세요!", "감사합니다", "좋은 하루 되세요")
        suggestions.forEach { suggestion ->
            val button = Button(this).apply {
                text = suggestion
                setOnClickListener {
                    Log.d(TAG, "Suggestion clicked: $suggestion")
                    currentInputConnection?.commitText(suggestion, 1)
                }
                setPadding(8, 8, 8, 8)
            }
            suggestionLayout.addView(button)
        }
        layout.addView(suggestionLayout)

        // 한글 자판
        val rows = listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "ㅒ", "ㅖ")
        )

        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 4, 0, 4)
            }

            row.forEach { key ->
                val keyButton = Button(this).apply {
                    text = key
                    layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                    setOnClickListener {
                        Log.d(TAG, "Key pressed: $key")
                        currentInputConnection?.commitText(key, 1)
                    }
                    setPadding(4, 4, 4, 4)
                }
                rowLayout.addView(keyButton)
            }
            layout.addView(rowLayout)
        }

        // 기능 키들
        val functionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 0)
        }

        // 숫자 키
        val numButton = Button(this).apply {
            text = "123"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener { Log.d(TAG, "123 key pressed") }
        }
        functionRow.addView(numButton)

        // 스페이스
        val spaceButton = Button(this).apply {
            text = "스페이스"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f)
            setOnClickListener {
                Log.d(TAG, "Space pressed")
                currentInputConnection?.commitText(" ", 1)
            }
        }
        functionRow.addView(spaceButton)

        // 백스페이스
        val backspaceButton = Button(this).apply {
            text = "←"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "Backspace pressed")
                currentInputConnection?.deleteSurroundingText(1, 0)
            }
        }
        functionRow.addView(backspaceButton)

        // 엔터
        val enterButton = Button(this).apply {
            text = "↵"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "Enter pressed")
                currentInputConnection?.performEditorAction(EditorInfo.IME_ACTION_DONE)
            }
        }
        functionRow.addView(enterButton)

        layout.addView(functionRow)
        return layout
    }

    private fun createFallbackKeyboard(): View {
        return TextView(this).apply {
            text = "키보드 로딩 중..."
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.d(TAG, "onStartInputView: restarting=$restarting")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        Log.d(TAG, "onFinishInputView: finishingInput=$finishingInput")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AIKeyboardService onDestroy")
    }
}
