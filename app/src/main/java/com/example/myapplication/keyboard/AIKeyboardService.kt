/**
 * AI 자동완성 키보드 서비스
 * 
 * 이 클래스는 Android 시스템의 입력 메서드 서비스(IME)를 구현합니다.
 * 사용자가 텍스트를 입력할 때 나타나는 키보드 UI를 제공하고,
 * AI 기반 자동완성 제안을 포함한 완벽한 한글 키보드를 구현합니다.
 * 
 * 주요 기능:
 * - 완벽한 한글 자판 레이아웃 (4줄 구성)
 * - 숫자/기호 키보드 전환
 * - AI 제안 버튼 (안녕하세요!, 감사합니다, 좋은 하루 되세요)
 * - 기본 기능 키 (스페이스, 백스페이스, 엔터, 키보드 전환)
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

    private var isNumberMode = false
    private var isShiftPressed = false
    private lateinit var rootLayout: LinearLayout

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AIKeyboardService onCreate")
    }

    private fun renderKeyboard() {
    // 전체를 새로 그립니다.
    rootLayout.removeAllViews()

    // 1) AI 제안 영역
    val suggestionLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(0, 0, 0, 8)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    val suggestions = listOf("안녕하세요!", "감사합니다", "좋은 하루 되세요", "사랑해요")
    suggestions.forEach { suggestion ->
        val button = Button(this).apply {
            text = suggestion
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { currentInputConnection?.commitText(suggestion, 1) }
            setPadding(6, 6, 6, 6)
            textSize = 12f
        }
        suggestionLayout.addView(button)
    }
    rootLayout.addView(suggestionLayout)

    // 2) 본 키보드 (한글/숫자)
    if (isNumberMode) {
        createNumberKeyboard(rootLayout)
    } else {
        createHangulKeyboard(rootLayout)
    }

    // 3) 하단 기능 키들
    createFunctionKeys(rootLayout)
}


    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView called")
        return try {
            rootLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(8, 8, 8, 8)
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            renderKeyboard()  // <- 최초 렌더링
            rootLayout
        } catch (e: Exception) {
            Log.e(TAG, "Error creating keyboard: ${e.message}")
            createFallbackKeyboard()
        }
    }
    

    private fun createCompleteKeyboard(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(8, 8, 8, 8)
        }

        // AI 제안 영역
        val suggestionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 8)
        }

        val suggestions = listOf("안녕하세요!", "감사합니다", "좋은 하루 되세요", "사랑해요")
        suggestions.forEach { suggestion ->
            val button = Button(this).apply {
                text = suggestion
                setOnClickListener {
                    Log.d(TAG, "Suggestion clicked: $suggestion")
                    currentInputConnection?.commitText(suggestion, 1)
                }
                setPadding(6, 6, 6, 6)
                textSize = 12f
            }
            suggestionLayout.addView(button)
        }
        layout.addView(suggestionLayout)

        if (isNumberMode) {
            // 숫자/기호 키보드
            createNumberKeyboard(layout)
        } else {
            // 한글 자판
            createHangulKeyboard(layout)
        }

        // 하단 기능 키들
        createFunctionKeys(layout)
        
        return layout
    }

    private fun createHangulKeyboard(layout: LinearLayout) {
        // 기본 한글 자판 (3줄) - 쉬프트키 상태에 따라 변경
        val basicRows = listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")
        )

        val shiftRows = listOf(
            listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")
        )

        val currentRows = if (isShiftPressed) shiftRows else basicRows

        // 1줄: 자음들 (쉬프트키 상태에 따라 변경)
        val firstRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 2, 0, 2)
        }

        currentRows[0].forEach { key ->
            val keyButton = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.d(TAG, "Hangul key pressed: $key")
                    currentInputConnection?.commitText(key, 1)
                }
                setPadding(2, 2, 2, 2)
                textSize = 16f
            }
            firstRow.addView(keyButton)
        }
        layout.addView(firstRow)

        // 2줄: 모음들 (변경 없음)
        val secondRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 2, 0, 2)
        }

        currentRows[1].forEach { key ->
            val keyButton = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.d(TAG, "Hangul key pressed: $key")
                    currentInputConnection?.commitText(key, 1)
                }
                setPadding(2, 2, 2, 2)
                textSize = 16f
            }
            secondRow.addView(keyButton)
        }
        layout.addView(secondRow)

        // 3줄: 쉬프트키 + 자판 + 딜리트키
        val thirdRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 2, 0, 2)
        }

        // 쉬프트키 (왼쪽) - 디자인 개선
        val shiftButton = Button(this).apply {
            text = if (isShiftPressed) "⇧ ON" else "⇧"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            // setOnClickListener {
            //     Log.d(TAG, "Shift button clicked! Current state: $isShiftPressed")
            //     isShiftPressed = !isShiftPressed
            //     Log.d(TAG, "Shift state changed to: $isShiftPressed")
                
            //     // 키보드 재시작하여 레이아웃 변경
            //     try {
            //         Log.d(TAG, "Restarting keyboard view...")
            //         onFinishInputView(false)
            //         onStartInputView(null, true)
            //         Log.d(TAG, "Keyboard view restarted successfully")
            //     } catch (e: Exception) {
            //         Log.e(TAG, "Error restarting keyboard view", e)
            //     }
            // }
            setOnClickListener {
                Log.d(TAG, "Shift button clicked! Current state: $isShiftPressed")
                isShiftPressed = !isShiftPressed
                Log.d(TAG, "Shift state changed to: $isShiftPressed")
                // 기존의 onFinishInputView/onStartInputView 제거
                renderKeyboard() // <- 즉시 재렌더
            }
            
            setPadding(4, 8, 4, 8)
            textSize = 18f
            setBackgroundColor(if (isShiftPressed) Color.parseColor("#2196F3") else Color.parseColor("#E0E0E0"))
            setTextColor(if (isShiftPressed) Color.WHITE else Color.BLACK)
        }
        thirdRow.addView(shiftButton)

        // 3줄 자판 키들
        currentRows[2].forEach { key ->
            val keyButton = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.d(TAG, "Hangul key pressed: $key")
                    currentInputConnection?.commitText(key, 1)
                }
                setPadding(2, 2, 2, 2)
                textSize = 16f
            }
            thirdRow.addView(keyButton)
        }

        // 딜리트키 (오른쪽) - 디자인 개선
        val deleteButton = Button(this).apply {
            text = "⌫"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "Delete pressed")
                currentInputConnection?.deleteSurroundingText(1, 0)
            }
            setPadding(4, 8, 4, 8)
            textSize = 18f
            setBackgroundColor(Color.parseColor("#E0E0E0"))
            setTextColor(Color.BLACK)
        }
        thirdRow.addView(deleteButton)

        layout.addView(thirdRow)
    }

    private fun createNumberKeyboard(layout: LinearLayout) {
        // 숫자 키보드 (3줄)
        val numberRows = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\""),
            listOf(".", ",", "?", "!", "'", "~", "#", "%", "+", "=")
        )

        numberRows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 2, 0, 2)
            }

            row.forEach { key ->
                val keyButton = Button(this).apply {
                    text = key
                    layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                    setOnClickListener {
                        isNumberMode = !isNumberMode
                        Log.d(TAG, "Switching to ${if (isNumberMode) "number" else "hangul"} keyboard")
                        renderKeyboard() // <- 즉시 재렌더
                    }
                    
                    setPadding(2, 2, 2, 2)
                    textSize = 16f
                }
                rowLayout.addView(keyButton)
            }
            layout.addView(rowLayout)
        }
    }

    private fun createFunctionKeys(layout: LinearLayout) {
        val functionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 0)
        }

        // 숫자 키
        val numButton = Button(this).apply {
            text = "123"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                isNumberMode = !isNumberMode
                Log.d(TAG, "Switching to ${if (isNumberMode) "number" else "hangul"} keyboard")
                // 키보드 재시작
                onFinishInputView(false)
                onStartInputView(null, true)
            }
            setPadding(4, 4, 4, 4)
        }
        functionRow.addView(numButton)
        
        // 이모티콘
        val emojiButton = Button(this).apply {
            text = "😊"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "Emoji pressed")
                currentInputConnection?.commitText("😊", 1)
            }
            setPadding(4, 4, 4, 4)
        }
        functionRow.addView(emojiButton)
        
        // 스페이스
        val spaceButton = Button(this).apply {
            text = "스페이스"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 2f)
            setOnClickListener {
                Log.d(TAG, "Space pressed")
                currentInputConnection?.commitText(" ", 1)
            }
            setPadding(4, 4, 4, 4)
        }
        functionRow.addView(spaceButton)
        
        // 엔터
        val enterButton = Button(this).apply {
            text = "↵"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "Enter pressed")
                currentInputConnection?.performEditorAction(EditorInfo.IME_ACTION_DONE)
            }
            setPadding(4, 4, 4, 4)
        }
        functionRow.addView(enterButton)

        // 영어
        val englishButton = Button(this).apply {
            text = "영어"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f)
            setOnClickListener {
                Log.d(TAG, "English pressed")
                // 영어 키보드로 전환 (향후 구현)
            }
            setPadding(4, 4, 4, 4)
        }
        functionRow.addView(englishButton)

        layout.addView(functionRow)
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
        if (this::rootLayout.isInitialized) {
            renderKeyboard()
        }
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
