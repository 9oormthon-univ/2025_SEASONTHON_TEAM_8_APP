package com.example.myapplication.keyboard

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
 */
class ScheduleManager(private val context: Context) {
    
    companion object {
        // 색상
        private const val COLOR_PRIMARY = "#2196F3"
        private const val COLOR_LIGHT_GRAY = "#E0E0E0"
        private const val COLOR_DARK_GRAY = "#424242"
        private const val COLOR_WHITE = "#FFFFFF"
        private const val COLOR_GREEN = "#4CAF50"
        private const val COLOR_ORANGE = "#FF9800"
        
        // 크기
        private const val BUTTON_HEIGHT_DP = 48
        private const val BUTTON_PADDING_HORIZONTAL_DP = 8
        private const val BUTTON_PADDING_VERTICAL_DP = 12
        private const val BUTTON_MARGIN_DP = 4
        private const val BUTTON_CORNER_RADIUS_DP = 8f
        private const val BUTTON_ELEVATION_DP = 2f
        
        // 폰트 크기
        private const val TEXT_SIZE_MEDIUM = 14f
        private const val TEXT_SIZE_SMALL = 12f
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
     * 일정 추가 UI를 생성
     */
    fun createScheduleAddUI(
        inputConnection: InputConnection?,
        currentText: String
    ): LinearLayout {
        val scheduleLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor(COLOR_WHITE))
        }
        
        // 제목
        val titleText = TextView(context).apply {
            text = "일정 추가"
            textSize = 16f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 16)
        }
        scheduleLayout.addView(titleText)
        
        // 원본 텍스트 표시
        val originalText = TextView(context).apply {
            text = "원본: $currentText"
            textSize = 14f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            setPadding(0, 0, 0, 16)
        }
        scheduleLayout.addView(originalText)
        
        // 추출된 일정 정보 표시
        val extractedSchedule = extractScheduleInfo(currentText)
        
        // 장소 입력
        val locationLayout = createInputField("장소", extractedSchedule.location)
        scheduleLayout.addView(locationLayout)
        
        // 날짜 입력
        val dateLayout = createInputField("날짜", extractedSchedule.date)
        scheduleLayout.addView(dateLayout)
        
        // 시간 입력
        val timeLayout = createInputField("시간", extractedSchedule.time)
        scheduleLayout.addView(timeLayout)
        
        // 메모 입력
        val memoLayout = createInputField("메모", extractedSchedule.memo)
        scheduleLayout.addView(memoLayout)
        
        // 일정 등록 버튼
        val addButton = createAddButton {
            // 더미 데이터로 일정 등록 완료 메시지
            val successMessage = "✅ 일정이 등록되었습니다!\n" +
                    "장소: ${extractedSchedule.location}\n" +
                    "날짜: ${extractedSchedule.date}\n" +
                    "시간: ${extractedSchedule.time}\n" +
                    "메모: ${extractedSchedule.memo}"
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
                setMargins(0, BUTTON_MARGIN_DP, 0, BUTTON_MARGIN_DP)
            }
        }
        
        // 라벨
        val labelText = TextView(context).apply {
            text = "$label:"
            textSize = 14f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                width = 80.dp()
            }
        }
        fieldLayout.addView(labelText)
        
        // 입력 필드
        val inputField = EditText(context).apply {
            setText(defaultValue)
            textSize = 14f
            setTextColor(Color.parseColor(COLOR_DARK_GRAY))
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                height = BUTTON_HEIGHT_DP.dp()
            }
            setPadding(8, 8, 8, 8)
            background = roundedBg(Color.parseColor(COLOR_LIGHT_GRAY), 4f)
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
                setMargins(0, 16, 0, 0)
            }
            setOnClickListener { onClick() }
            setPadding(BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP, 
                      BUTTON_PADDING_HORIZONTAL_DP, BUTTON_PADDING_VERTICAL_DP)
            textSize = TEXT_SIZE_MEDIUM
            background = roundedBg(Color.parseColor(COLOR_GREEN), BUTTON_CORNER_RADIUS_DP)
            setTextColor(Color.parseColor(COLOR_WHITE))
            elevation = BUTTON_ELEVATION_DP
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
