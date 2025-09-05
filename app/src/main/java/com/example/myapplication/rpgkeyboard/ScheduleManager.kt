package com.example.myapplication.rpgkeyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.DatePicker
import android.widget.TimePicker
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.graphics.drawable.GradientDrawable
import android.view.inputmethod.InputConnection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * 일정 추가 UI 생성 및 더미 추출/등록 처리
 * - 자연어 텍스트에서 장소/날짜/시간/메모를 추출(샘플)
 * - 사용자가 필드를 수정 후 "일정 등록" 시 결과를 입력창에 커밋
 * - 무채색 기반의 통일된 UI 디자인 적용
 * - 키보드 크기에 맞춘 컴팩트한 디자인
 */
class ScheduleManager(private val context: Context) {
    
    companion object {
        // AOS 다크 테마 색상 팔레트
        private const val COLOR_PRIMARY = "#FF2A2A2A"      // AOS/Dark/Primary
        private const val COLOR_SECONDARY = "#FF424242"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#FFE0E0E0"   // AOS/Dark/On Primary
        private const val COLOR_DARK_GRAY = "#FF000000"    // AOS/Dark/Secondary
        private const val COLOR_WHITE = "#FFE0E0E0"        // 밝은 회색
        private const val COLOR_BLACK = "#FF000000"        // 검은색
        private const val COLOR_ACCENT = "#FF4CAF50"       // 강조색 (에메랄드)
        
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
        private const val FIELD_MARGIN_DP = 6
        private const val INPUT_HEIGHT_DP = 32
        
        // 폰트 크기
        private const val TEXT_SIZE_TITLE = 14f
        private const val TEXT_SIZE_BUTTON = 12f
        private const val TEXT_SIZE_FIELD = 12f
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
     * 일정 추가 UI를 생성 (사진 스타일)
     */
    fun createScheduleAddUI(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        // 전체 컨테이너 (검은 배경 + 양쪽 패딩)
        val outerContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp()) // 양쪽 검은 패딩
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK) // 검은 배경
        }
        
        // 내부 컨테이너 (다크 그레이)
        val scheduleLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20.dp(), 20.dp(), 20.dp(), 20.dp())
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            background = roundedBg(Color.parseColor("#FF333333"), 16f) // 다크 그레이, 둥근 모서리
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "일정 추가"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 20.dp())
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        scheduleLayout.addView(titleText)
        
        // 추출된 일정 정보 표시
        val extractedSchedule = extractScheduleInfo(currentText)
        
        // 메인 컨테이너 (좌우 분할)
        val mainContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20.dp())
            }
        }
        
        // 왼쪽: 입력 필드들
        val leftContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 0, 10.dp(), 0)
            }
        }
        
        // 장소 입력
        val locationLayout = createScheduleInputField("장소", extractedSchedule.location)
        leftContainer.addView(locationLayout)
        
        // 날짜 입력 (DatePicker)
        val dateLayout = createScheduleInputField("날짜", "9월 5일")
        leftContainer.addView(dateLayout)
        
        // 시간 입력 (TimePicker)
        val timeLayout = createScheduleInputField("시간", "오후 11시")
        leftContainer.addView(timeLayout)
        
        // 오른쪽: 메모 입력
        val memoContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        
        val memoField = EditText(context).apply {
            hint = "추가메모 입력 창"
            textSize = 12f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#FF888888"))
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                140.dp() // 통일된 높이
            )
            setPadding(12.dp(), 12.dp(), 12.dp(), 12.dp())
            background = roundedBg(Color.parseColor("#FF2A2A2A"), 8f)
            setTypeface(null, android.graphics.Typeface.NORMAL)
            gravity = android.view.Gravity.TOP or android.view.Gravity.START
        }
        memoContainer.addView(memoField)
        
        mainContainer.addView(leftContainer)
        mainContainer.addView(memoContainer)
        scheduleLayout.addView(mainContainer)
        
        // 일정 등록 버튼
        val addButton = createScheduleAddButton {
            // 더미 데이터로 일정 등록 완료 메시지
            val successMessage = "✅ 일정 등록됨: ${extractedSchedule.location} ${extractedSchedule.date}"
            inputConnection?.commitText(successMessage, 1)
        }
        scheduleLayout.addView(addButton)
        
        // outerContainer에 scheduleLayout 추가
        outerContainer.addView(scheduleLayout)
        
        return outerContainer
    }
    
    /**
     * 일정 입력 필드 생성 (사진 스타일)
     */
    private fun createScheduleInputField(label: String, defaultValue: String): LinearLayout {
        val fieldLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 6.dp(), 0, 6.dp())
            }
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(12.dp(), 10.dp(), 12.dp(), 10.dp())
            background = roundedBg(Color.parseColor("#FF2A2A2A"), 8f)
        }
        
        // 라벨 (연한 파란색)
        val labelText = TextView(context).apply {
            text = label
            textSize = 12f
            setTextColor(Color.parseColor("#FF87CEEB")) // 연한 파란색
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        fieldLayout.addView(labelText)
        
        // 값 (흰색, 오른쪽 정렬)
        val valueText = TextView(context).apply {
            text = defaultValue
            textSize = 12f
            setTextColor(Color.WHITE)
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            )
            gravity = android.view.Gravity.END or android.view.Gravity.CENTER_VERTICAL
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        fieldLayout.addView(valueText)
        
        // 클릭 이벤트 (날짜/시간 필드에만)
        if (label == "날짜" || label == "시간") {
            fieldLayout.setOnClickListener {
                if (label == "날짜") {
                    showDatePicker(valueText)
                } else if (label == "시간") {
                    showTimePicker(valueText)
                }
            }
        }
        
        return fieldLayout
    }
    
    /**
     * DatePicker 표시
     */
    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePicker(context)
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val selectedDate = "${month + 1}월 ${day}일"
            textView.text = selectedDate
        }
        
        // 실제로는 Dialog나 PopupWindow로 표시해야 함
        // 여기서는 간단히 텍스트만 변경
        val calendar2 = Calendar.getInstance()
        val month = calendar2.get(Calendar.MONTH) + 1
        val day = calendar2.get(Calendar.DAY_OF_MONTH)
        textView.text = "${month}월 ${day}일"
    }
    
    /**
     * TimePicker 표시
     */
    private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        // 실제로는 Dialog나 PopupWindow로 표시해야 함
        // 여기서는 간단히 텍스트만 변경
        val timeString = if (hour >= 12) {
            "오후 ${if (hour > 12) hour - 12 else hour}시"
        } else {
            "오전 ${if (hour == 0) 12 else hour}시"
        }
        textView.text = timeString
    }
    
    /**
     * 입력 필드 생성 (기존 함수 유지)
     */
    private fun createInputField(label: String, defaultValue: String): LinearLayout {
        val fieldLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, FIELD_MARGIN_DP, 0, FIELD_MARGIN_DP)
            }
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        
        // 라벨
        val labelText = TextView(context).apply {
            text = "$label:"
            textSize = TEXT_SIZE_FIELD
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                width = 60.dp()
            }
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        fieldLayout.addView(labelText)
        
        // 입력 필드
        val inputField = EditText(context).apply {
            setText(defaultValue)
            textSize = TEXT_SIZE_FIELD
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                height = INPUT_HEIGHT_DP.dp()
            }
            setPadding(8.dp(), 8.dp(), 8.dp(), 8.dp())
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), 4f)
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        fieldLayout.addView(inputField)
        
        return fieldLayout
    }
    
    /**
     * 일정 등록 버튼 생성 (사진 스타일)
     */
    private fun createScheduleAddButton(onClick: () -> Unit): Button {
        return Button(context).apply {
            text = "일정 등록"
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = 40.dp()
                setMargins(0, 0, 0, 0)
            }
            setOnClickListener { onClick() }
            setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            textSize = 14f
            background = roundedBg(Color.parseColor("#FF87CEEB"), 8f) // 연한 파란색
            setTextColor(Color.WHITE)
            elevation = 2f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
    
    /**
     * 일정 등록 버튼 생성 (기존 함수 유지)
     */
    private fun createAddButton(onClick: () -> Unit): Button {
        return Button(context).apply {
            text = "일정 등록"
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                height = BUTTON_HEIGHT_DP.dp()
                setMargins(0, 16.dp(), 0, 0)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp(), 
                      BUTTON_PADDING_HORIZONTAL_DP.dp(), BUTTON_PADDING_VERTICAL_DP.dp())
            textSize = TEXT_SIZE_BUTTON
            background = roundedBg(Color.parseColor(COLOR_ACCENT), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
            elevation = BUTTON_ELEVATION_DP.dp().toFloat()
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
    
    /**
     * 텍스트에서 일정 정보 추출 (더미 데이터)
     */
    private fun extractScheduleInfo(@Suppress("UNUSED_PARAMETER") text: String): ScheduleInfo {
        // 실제로는 AI로 텍스트 분석하여 일정 정보 추출
        // text 매개변수는 향후 AI 분석에 사용될 예정
        return ScheduleInfo(
            location = "카페",
            date = "내일",
            time = "15:00",
            memo = "친구와 만남"
        )
    }
    
    /**
     * 일정 정보 데이터 클래스
     */
    data class ScheduleInfo(
        val location: String,
        val date: String,
        val time: String,
        val memo: String
    )
}
