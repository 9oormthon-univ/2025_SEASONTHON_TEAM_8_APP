package com.example.myapplication.rpgkeyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.graphics.drawable.GradientDrawable
import android.view.inputmethod.InputConnection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 일정 추가 UI 생성 및 더미 추출/등록 처리
 * - 자연어 텍스트에서 장소/날짜/시간/메모를 추출(샘플)
 * - 사용자가 필드를 수정 후 "일정 등록" 시 결과를 입력창에 커밋
 * - 무채색 기반의 통일된 UI 디자인 적용
 * - 키보드 크기에 맞춘 컴팩트한 디자인
 */
class ScheduleManager(private val context: Context) {
    
    companion object {
        // 무채색 색상 팔레트
        private const val COLOR_PRIMARY = "#424242"      // 진한 회색
        private const val COLOR_SECONDARY = "#757575"    // 중간 회색
        private const val COLOR_LIGHT_GRAY = "#EEEEEE"   // 연한 회색
        private const val COLOR_DARK_GRAY = "#212121"    // 매우 진한 회색
        private const val COLOR_WHITE = "#FFFFFF"        // 흰색
        private const val COLOR_BLACK = "#000000"        // 검은색
        private const val COLOR_ACCENT = "#757575"       // 강조색 (중간 회색)
        
        // 키보드 크기에 맞춘 크기 상수
        private const val SCREEN_HEIGHT_DP = 200         // 키보드 높이
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
     * 일정 추가 UI를 생성
     */
    fun createScheduleAddUI(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val scheduleLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp(), CARD_PADDING_DP.dp())
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SCREEN_HEIGHT_DP.dp()
            )
            setBackgroundColor(Color.parseColor(COLOR_WHITE))
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "일정 추가"
            textSize = TEXT_SIZE_TITLE
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, TITLE_MARGIN_BOTTOM_DP.dp())
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
        
        // 장소 입력
        val locationLayout = createInputField("장소", extractedSchedule.location)
        scheduleLayout.addView(locationLayout)
        
        // 날짜 입력
        val dateLayout = createInputField("날짜", extractedSchedule.date)
        scheduleLayout.addView(dateLayout)
        
        // 일정 등록 버튼
        val addButton = createAddButton {
            // 더미 데이터로 일정 등록 완료 메시지
            val successMessage = "✅ 일정 등록됨: ${extractedSchedule.location} ${extractedSchedule.date}"
            inputConnection?.commitText(successMessage, 1)
        }
        scheduleLayout.addView(addButton)
        
        return scheduleLayout
    }
    
    /**
     * 입력 필드 생성
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
     * 일정 등록 버튼 생성
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
    private fun extractScheduleInfo(text: String): ScheduleInfo {
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
