/**
 * AI 자동완성 키보드 서비스
 * 
 * 이 클래스는 Android 시스템의 입력 메서드 서비스(IME)를 구현합니다.
 * 사용자가 텍스트를 입력할 때 나타나는 키보드 UI를 제공하고,
 * AI 기반 자동완성 제안을 포함한 완벽한 다국어 키보드를 구현합니다.
 * 
 * 주요 기능:
 * - 완벽한 한글 자판 레이아웃 (3줄 구성, 쉬프트키로 쌍자음/쌍모음)
 * - QWERTY 영어 키보드 (대소문자 전환 지원)
 * - 확장된 숫자/기호 키보드 (기본 + 확장 기호)
 * - AI 기능 버튼 (리라이팅, 맞춤법 검사, 일정 추가)
 * - 한/영/숫자 모드 전환
 * - 안정적인 Android View 기반 UI
 * - 오류 발생 시 폴백 키보드 제공
 * 
 * @author SEASONTHON TEAM 8
 * @version 3.0.0
 */
package com.example.myapplication.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.graphics.drawable.GradientDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

/**
 * AI 자동완성 키보드의 핵심 서비스 클래스
 * 
 * 3가지 키보드 모드를 지원합니다:
 * 1. 한글 키보드: 3줄 구성, 쉬프트키로 쌍자음/쌍모음 전환
 * 2. 영어 키보드: QWERTY 레이아웃, 쉬프트키로 대소문자 전환
 * 3. 숫자 키보드: 기본 기호 + 확장 기호를 Alt 모드로 전환
 * 
 * AI 기능:
 * 1. 리라이팅: 공손체, 친근체, 단답체 등 문체 변환
 * 2. 맞춤법 검사: 작성 중인 텍스트의 맞춤법 검사 및 수정
 * 3. 일정 추가: 텍스트에서 일정 정보 추출하여 캘린더 등록
 */
class AIKeyboardService : InputMethodService() {
    
    companion object {
        private const val TAG = "AIKeyboardService"
        
        // ==================== 디자인 상수 ====================
        // 색상
        private const val COLOR_PRIMARY = "#2196F3"           // 파란색 (쉬프트키 활성화)
        private const val COLOR_WHITE = "#FFFFFF"             // 흰색 (일반 키)
        private const val COLOR_LIGHT_GRAY = "#E0E0E0"        // 연한 회색 (기능 키)
        private const val COLOR_DARK_GRAY = "#424242"         // 진한 회색 (텍스트)
        private const val COLOR_BLACK = "#000000"             // 검은색 (텍스트)
        
        // 크기
        private const val BUTTON_HEIGHT_DP = 48               // 모든 버튼의 높이 (dp)
        private const val BUTTON_PADDING_HORIZONTAL_DP = 4    // 좌우 패딩 (dp)
        private const val BUTTON_PADDING_VERTICAL_DP = 12     // 상하 패딩 (dp)
        private const val BUTTON_MARGIN_DP = 2                // 버튼 간격 (dp)
        private const val BUTTON_CORNER_RADIUS_DP = 8f        // 모서리 반지름 (dp)
        private const val BUTTON_ELEVATION_DP = 2f            // 그림자 (dp)
        
        // 폰트 크기
        private const val TEXT_SIZE_LARGE = 18f               // 큰 텍스트 (주요 키)
        private const val TEXT_SIZE_MEDIUM = 16f              // 중간 텍스트 (기능 키)
        private const val TEXT_SIZE_SMALL = 12f               // 작은 텍스트 (AI 제안)
    }

    // ==================== 키보드 상태 변수 ====================
    private var isNumberMode = false      // 숫자/기호 키보드 모드
    private var isShiftPressed = false    // 쉬프트키 눌림 상태
    private var isEnglishMode = false     // 영어 키보드 모드
    private var isSymbolAltMode = false  // 숫자 키보드의 확장 기호 모드

    private lateinit var rootLayout: LinearLayout  // 메인 키보드 레이아웃
    private lateinit var aiFeatureOverlay: AIFeatureOverlay  // AI 기능 오버레이
    private lateinit var contentContainer: FrameLayout  // 키보드/오버레이가 교대로 들어갈 컨테이너
    private var currentInputText = ""  // 현재 입력 중인 텍스트

    // ==================== 유틸리티 함수들 ====================
    
    /**
     * dp 단위를 픽셀로 변환하는 확장 함수
     */
    private fun Float.dp(): Int = (this * resources.displayMetrics.density).toInt()
    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    /**
     * 둥근 모서리를 가진 배경을 생성하는 함수
     * @param color 배경 색상
     * @param radiusDp 모서리 반지름 (dp 단위)
     */
    private fun roundedBg(color: Int, radiusDp: Float = 10f): GradientDrawable =
        GradientDrawable().apply {
            cornerRadius = radiusDp.dp().toFloat()
            setColor(color)
        }

    /**
     * 오류 발생 시 표시할 폴백 키보드
     */
    private fun createFallbackKeyboard(): View {
        return TextView(this).apply {
            text = "키보드 로딩 중..."
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
    }
    
    // ==================== 생명주기 메서드 ====================
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AIKeyboardService onCreate")
        
        // AI 기능 오버레이 초기화
        aiFeatureOverlay = AIFeatureOverlay(this)
    }

    // ==================== 키보드 렌더링 메서드 ====================
    
    /**
     * 전체 키보드를 새로 그리는 메서드
     * 모드 변경 시 호출되어 UI를 즉시 업데이트합니다.
     */
    private fun renderKeyboard() {
        // 루트에는 항상: 상단 기능 버튼 + 콘텐츠 컨테이너
        rootLayout.removeAllViews()

        // 1) AI 기능 버튼들
        renderAIFeatureButtons()

        // 2) 콘텐츠 컨테이너 (키보드 또는 오버레이가 들어감)
        if (!this::contentContainer.isInitialized) {
            contentContainer = FrameLayout(this).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
        // 오버레이가 보여지는 중이 아니라면 키보드 렌더링
        rootLayout.addView(contentContainer)
        contentContainer.removeAllViews()

        // 키보드 전용 레이아웃을 만들어 컨테이너에 넣음
        val keyboardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        // 메인 키보드 + 하단 기능키를 키보드 레이아웃에 렌더링
        renderMainKeyboardInto(keyboardLayout)
        createFunctionKeys(keyboardLayout)

        contentContainer.addView(keyboardLayout)
    }

    /**
     * AI 기능 버튼들을 렌더링
     * 리라이팅, 맞춤법 검사, 일정 추가 기능 제공
     */
    private fun renderAIFeatureButtons() {
        val featureButtons = aiFeatureOverlay.createAIFeatureButtons(
            onRewritingClick = { showRewritingOptions() },
            onSpellCheckClick = { showSpellCheckResult() },
            onScheduleAddClick = { showScheduleAddUI() }
        )
        rootLayout.addView(featureButtons)
    }

    /**
     * 메인 키보드를 모드에 따라 렌더링
     */
    private fun renderMainKeyboard() {
        when {
            isNumberMode   -> createNumberKeyboard(rootLayout)
            isEnglishMode  -> createEnglishKeyboard(rootLayout)
            else           -> createHangulKeyboard(rootLayout)
        }
    }

    /**
     * 메인 키보드를 외부 레이아웃으로 렌더링 (컨테이너용)
     */
    private fun renderMainKeyboardInto(targetLayout: LinearLayout) {
        when {
            isNumberMode   -> createNumberKeyboard(targetLayout)
            isEnglishMode  -> createEnglishKeyboard(targetLayout)
            else           -> createHangulKeyboard(targetLayout)
        }
    }
    
    // ==================== AI 기능 메서드들 ====================
    
    /**
     * 리라이팅 옵션 표시
     */
    private fun showRewritingOptions() {
        aiFeatureOverlay.showRewritingOptions(currentInputConnection, currentInputText)
    }
    
    /**
     * 맞춤법 검사 결과 표시
     */
    private fun showSpellCheckResult() {
        aiFeatureOverlay.showSpellCheckResult(currentInputConnection, currentInputText)
    }
    
    /**
     * 일정 추가 UI 표시
     */
    private fun showScheduleAddUI() {
        aiFeatureOverlay.showScheduleAddUI(currentInputConnection, currentInputText)
    }

    // ==================== 키보드 생성 메서드들 ====================
    
    /**
     * 한글 키보드를 생성하는 메서드
     * 3줄 구성: 자음줄, 모음줄, 기능키줄(쉬프트+자음+삭제)
     */
    private fun createHangulKeyboard(layout: LinearLayout) {
        // 기본 한글 자판 (3줄) - 쉬프트키 상태에 따라 변경
        val basicRows = listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),  // 1줄: 자음들
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),      // 2줄: 모음들
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")                  // 3줄: 자음들
        )

        // 쉬프트키 눌림 시 쌍자음/쌍모음으로 변경
        val shiftRows = listOf(
            listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ"),  // 1줄: 쌍자음 + 쌍모음
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),      // 2줄: 모음들 (변경 없음)
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")                  // 3줄: 자음들 (변경 없음)
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

        // 쉬프트키 (왼쪽) - 칩 스타일 + 상태 색상
        val shiftButton = Button(this).apply {
            text = if (isShiftPressed) "⇧" else "⇧"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.7f)
            background = roundedBg(
                if (isShiftPressed) Color.parseColor("#2196F3") else Color.parseColor("#EDEDED"),
                12f
            )
            setTextColor(if (isShiftPressed) Color.WHITE else Color.BLACK)
            setPadding(4, 10, 4, 10)
            textSize = 18f
            setOnClickListener {
                isShiftPressed = !isShiftPressed
                renderKeyboard() // 즉시 재렌더로 시각 반영
            }
            setOnLongClickListener {
                // 길게 누르면 쉬프트 상태 유지 (CAPS LOCK 유사)
                isShiftPressed = true
                renderKeyboard()
                true
            }
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

        // 딜리트키 (오른쪽) - 칩 스타일
        val deleteButton = Button(this).apply {
            text = "⌫"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.7f)
            background = roundedBg(Color.parseColor("#EDEDED"), 12f)
            setTextColor(Color.BLACK)
            setPadding(4, 10, 4, 10)
            textSize = 18f
            setOnClickListener { currentInputConnection?.deleteSurroundingText(1, 0) }
            setOnLongClickListener {
                // 길게 누르면 연속 삭제
                currentInputConnection?.deleteSurroundingText(5, 0)
                true
            }
        }
        thirdRow.addView(deleteButton)

        layout.addView(thirdRow)
    }

    /**
     * 영어 키보드를 생성하는 메서드
     * QWERTY 레이아웃, 쉬프트키로 대소문자 전환
     */
    private fun createEnglishKeyboard(layout: LinearLayout) {
        // QWERTY 3줄 레이아웃
        val basicRows = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),  // 1줄: q~p
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),      // 2줄: a~l
            listOf("z", "x", "c", "v", "b", "n", "m")                 // 3줄: z~m
        )

        // 쉬프트 상태에 따라 대소문자 변환
        val rows = if (isShiftPressed) {
            basicRows.map { row -> row.map { it.uppercase() } }
        } else basicRows

        // 1줄: q~p
        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }
        rows[0].forEach { key ->
            val btn = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { 
                    setMargins(2, 2, 2, 2) 
                }
                setOnClickListener { currentInputConnection?.commitText(key, 1) }
                setPadding(4, 12, 4, 12)
                textSize = 18f
                background = roundedBg(Color.parseColor("#FFFFFF"), 8f)
                setTextColor(Color.parseColor("#212121"))
                elevation = 2f
            }
            row1.addView(btn)
        }
        layout.addView(row1)

        // 2줄: a~l
        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }
        rows[1].forEach { key ->
            val btn = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { 
                    setMargins(2, 2, 2, 2) 
                }
                setOnClickListener { currentInputConnection?.commitText(key, 1) }
                setPadding(4, 12, 4, 12)
                textSize = 18f
                background = roundedBg(Color.parseColor("#FFFFFF"), 8f)
                setTextColor(Color.parseColor("#212121"))
                elevation = 2f
            }
            row2.addView(btn)
        }
        layout.addView(row2)

        // 3줄: Shift + z~m + Delete
        val row3 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }

        // Shift 키
        val shiftBtn = Button(this).apply {
            text = "⇧"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.6f).apply { 
                setMargins(2, 2, 2, 2) 
            }
            background = roundedBg(
                if (isShiftPressed) Color.parseColor("#2196F3") else Color.parseColor("#E0E0E0"),
                10f
            )
            setTextColor(if (isShiftPressed) Color.WHITE else Color.BLACK)
            setPadding(4, 12, 4, 12)
            textSize = 18f
            setOnClickListener {
                isShiftPressed = !isShiftPressed
                renderKeyboard()
            }
            setOnLongClickListener {
                // 길게 누르면 쉬프트 상태 유지
                isShiftPressed = true
                renderKeyboard()
                true
            }
        }
        row3.addView(shiftBtn)

        // z~m 키들
        rows[2].forEach { key ->
            val btn = Button(this).apply {
                text = key
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { 
                    setMargins(2, 2, 2, 2) 
                }
                setOnClickListener { currentInputConnection?.commitText(key, 1) }
                setPadding(4, 12, 4, 12)
                textSize = 18f
                background = roundedBg(Color.parseColor("#FFFFFF"), 8f)
                setTextColor(Color.parseColor("#212121"))
                elevation = 2f
            }
            row3.addView(btn)
        }

        // Delete 키
        val deleteBtn = Button(this).apply {
            text = "⌫"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.6f).apply { 
                setMargins(2, 2, 2, 2) 
            }
            background = roundedBg(Color.parseColor("#E0E0E0"), 10f)
            setTextColor(Color.BLACK)
            setPadding(4, 12, 4, 12)
            textSize = 18f
            setOnClickListener { currentInputConnection?.deleteSurroundingText(1, 0) }
            setOnLongClickListener {
                // 길게 누르면 연속 삭제
                currentInputConnection?.deleteSurroundingText(5, 0)
                true
            }
        }
        row3.addView(deleteBtn)

        layout.addView(row3)
    }

    /**
     * 숫자/기호 키보드를 생성하는 메서드
     * 기본 기호와 확장 기호를 Alt 모드로 전환
     */
    private fun createNumberKeyboard(layout: LinearLayout) {
        // 기본 숫자/기호 레이아웃
        val numberRowsPrimary = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),           // 1줄: 숫자
            listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\""),          // 2줄: 기본 기호
            listOf(".", ",", "?", "!", "'", "\"")                               // 3줄: 문장 부호
        )
        
        // 확장 기호 레이아웃 (Alt 모드)
        val numberRowsAlt = listOf(
            listOf("[", "]", "{", "}", "#", "%", "^", "*", "+", "="),            // 1줄: 괄호, 수학 기호
            listOf("_", "\\", "|", "~", "<", ">", "€", "£", "¥", "•"),         // 2줄: 특수 기호, 통화
            listOf(".", ",", "?", "!", "'", "\"")                               // 3줄: 문장 부호
        )

        val rows = if (isSymbolAltMode) numberRowsAlt else numberRowsPrimary

        rows.forEachIndexed { rowIdx, keys ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(4, 4, 4, 4)
            }

            // 3번째 줄(맨 아래) 왼쪽에 "#+= / 123" 토글 버튼
            if (rowIdx == 2) {
                val altToggle = Button(this).apply {
                    text = if (isSymbolAltMode) "123" else "#+="
                    layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.6f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    background = roundedBg(Color.parseColor("#E0E0E0"), 8f)
                    setPadding(4, 12, 4, 12)
                    textSize = 16f
                    setTextColor(Color.parseColor("#424242"))
                    elevation = 2f
                    setOnClickListener {
                        isSymbolAltMode = !isSymbolAltMode
                        renderKeyboard()
                    }
                }
                rowLayout.addView(altToggle)
            }

            // 키들 렌더링
            keys.forEach { key ->
                val keyButton = Button(this).apply {
                    text = key
                    layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener {
                        currentInputConnection?.commitText(key, 1)
                    }
                    setPadding(4, 12, 4, 12)
                    textSize = 18f
                    background = roundedBg(Color.parseColor("#FFFFFF"), 8f)
                    setTextColor(Color.parseColor("#212121"))
                    elevation = 2f
                }
                rowLayout.addView(keyButton)
            }

            // 3번째 줄(맨 아래) 오른쪽에 삭제 버튼
            if (rowIdx == 2) {
                val deleteBtn = Button(this).apply {
                    text = "⌫"
                    layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.6f).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    background = roundedBg(Color.parseColor("#E0E0E0"), 10f)
                    setTextColor(Color.BLACK)
                    setPadding(4, 12, 4, 12)
                    textSize = 18f
                    setOnClickListener { currentInputConnection?.deleteSurroundingText(1, 0) }
                    setOnLongClickListener {
                        currentInputConnection?.deleteSurroundingText(5, 0)
                        true
                    }
                }
                rowLayout.addView(deleteBtn)
            }

            layout.addView(rowLayout)
        }
    }

    /**
     * 하단 기능 키들을 생성하는 메서드
     * 언어 전환, 모드 전환, 스페이스, 엔터 등
     */
    private fun createFunctionKeys(layout: LinearLayout) {
        val functionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 8, 4, 4)
        }

        // 🌐 한/영 전환 버튼
        val globeButton = Button(this).apply {
            text = if (isEnglishMode) "🌐 ENG" else "🌐 한글"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.2f).apply { 
                setMargins(2, 2, 2, 2) 
            }
            background = roundedBg(Color.parseColor("#E0E0E0"), 8f)
            setPadding(4, 12, 4, 12)
            textSize = 16f
            setTextColor(Color.parseColor("#424242"))
            elevation = 2f
            setOnClickListener {
                // 내부 한/영 토글
                isEnglishMode = !isEnglishMode
                // 모드 변경 시 쉬프트 초기화 (아이폰 스타일)
                isShiftPressed = false
                // 숫자 모드였다면 해제 (사용자 혼란 방지)
                if (isNumberMode) isNumberMode = false
                renderKeyboard()
            }
            setOnLongClickListener {
                // 길게 누르면 시스템 IME 선택 피커
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                true
            }
        }
        functionRow.addView(globeButton)

        // 123/가나다/ABC 토글 버튼
        val numButton = Button(this).apply {
            text = if (isNumberMode) {
                if (isEnglishMode) "ABC" else "가나다"
            } else "123"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.2f).apply {
                setMargins(2, 2, 2, 2)
            }
            background = roundedBg(Color.parseColor("#E0E0E0"), 8f)
            setPadding(4, 12, 4, 12)
            textSize = 16f
            setTextColor(Color.parseColor("#424242"))
            elevation = 2f
            setOnClickListener {
                isNumberMode = !isNumberMode
                Log.d(TAG, "Switching to ${if (isNumberMode) "number" else "hangul"} keyboard")
                if (!isNumberMode) isSymbolAltMode = false  // 문자→숫자 진입 시 false로 맞춤
                renderKeyboard()
            }
        }
        functionRow.addView(numButton)

        // 스페이스 버튼 (넓게)
        val spaceButton = Button(this).apply {
            text = "스페이스"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 3.5f).apply {
                setMargins(2, 2, 2, 2)
            }
            background = roundedBg(Color.parseColor("#FFFFFF"), 8f)
            setPadding(4, 12, 4, 12)
            textSize = 16f
            setTextColor(Color.parseColor("#212121"))
            elevation = 2f
            setOnClickListener { currentInputConnection?.commitText(" ", 1) }
        }
        functionRow.addView(spaceButton)

        // 엔터 버튼
        val enterButton = Button(this).apply {
            text = "↵"
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.6f).apply {
                setMargins(2, 2, 2, 2)
            }
            background = roundedBg(Color.parseColor("#E0E0E0"), 8f)
            setPadding(4, 12, 4, 12)
            textSize = 16f
            setTextColor(Color.parseColor("#424242"))
            elevation = 2f
            setOnClickListener {
                val handled = currentInputConnection?.performEditorAction(EditorInfo.IME_ACTION_DONE)
                if (handled != true) currentInputConnection?.commitText("\n", 1)
            }
        }
        functionRow.addView(enterButton)

        layout.addView(functionRow)
    }

    // ==================== InputMethodService 생명주기 ====================
    
    /**
     * 키보드 입력 뷰를 생성하는 메서드
     * 키보드가 처음 나타날 때 호출됩니다.
     */
    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView called")
        return try {
            // 메인 키보드 레이아웃 생성
            rootLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(8, 8, 8, 8)
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // 시스템 UI 인셋 처리 (노치, 상태바 등)
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
                val sys = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, 
                           max(v.paddingBottom, sys.bottom + 12.dp()))
                insets
            }
            
            // AI 기능 오버레이 초기화 및 컨테이너 연결
            if (!this::aiFeatureOverlay.isInitialized) {
                aiFeatureOverlay = AIFeatureOverlay(this)
            }
            if (!this::contentContainer.isInitialized) {
                contentContainer = FrameLayout(this).apply {
                    layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
            aiFeatureOverlay.attach(
                container = contentContainer,
                onClose = {
                    // 닫기 시 키보드 다시 렌더링
                    renderKeyboard()
                }
            )
            
            renderKeyboard()  // 최초 렌더링
            rootLayout
        } catch (e: Exception) {
            Log.e(TAG, "Error creating keyboard: ${e.message}")
            createFallbackKeyboard()
        }
    }

    /**
     * 키보드 입력 뷰가 시작될 때 호출
     */
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.d(TAG, "onStartInputView: restarting=$restarting")
        
        // 현재 입력 텍스트 초기화
        currentInputText = ""
        
        if (this::rootLayout.isInitialized) {
            renderKeyboard()
        }
    }

    /**
     * 키보드 입력 뷰가 종료될 때 호출
     */
    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        Log.d(TAG, "onFinishInputView: finishingInput=$finishingInput")
    }

    /**
     * 서비스가 소멸될 때 호출
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AIKeyboardService onDestroy")
    }
}
