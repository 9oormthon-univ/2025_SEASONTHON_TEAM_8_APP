package com.example.myapplication.rpgkeyboard

import android.content.Context
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import com.example.myapplication.R
import android.graphics.*
import kotlin.math.*
import com.skydoves.balloon.*
import androidx.core.content.ContextCompat

// ====== 커스텀 드로어블: 보라색 베이스 + 234° 오버레이 그라데이션 + 네온 글로우 ======
class OverlayGlowButtonDrawable(
    private val cornerRadiusPx: Float,
    private val baseColor: Int = Color.parseColor("#8670BD"),      // 보라색 베이스
    private val glowColor: Int = Color.parseColor("#B3D1FFFF"),     // 청록빛 네온 (#D1FFFF + 알파)
    private val glowBlurPx: Float = 5.9f                            // CSS 5.9px 대응
) : Drawable() {

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = glowColor
        strokeWidth = glowBlurPx
        maskFilter = BlurMaskFilter(glowBlurPx, BlurMaskFilter.Blur.NORMAL)
    }
    private val rect = RectF()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        rect.set(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())

        // 234° 선형 그라데이션 (CSS 기준 각도 재현)
        val angleDeg = 234.0
        val angleRad = Math.toRadians(angleDeg)
        val cx = rect.centerX()
        val cy = rect.centerY()
        val r = hypot(rect.width().toDouble(), rect.height().toDouble()).toFloat() / 2f
        val x1 = cx - (cos(angleRad) * r).toFloat()
        val y1 = cy - (sin(angleRad) * r).toFloat()
        val x2 = cx + (cos(angleRad) * r).toFloat()
        val y2 = cy + (sin(angleRad) * r).toFloat()

        // CSS linear-gradient(234deg, rgba(52, 194, 232, 0.80) 4.75%, rgba(169, 239, 249, 0.80) 27.52%, rgba(209, 255, 255, 0.80) 48.5%, rgba(178, 243, 250, 0.80) 66.36%, rgba(52, 194, 232, 0.80) 96.27%)
        overlayPaint.shader = LinearGradient(
            x1, y1, x2, y2,
            intArrayOf(
                Color.parseColor("#CC34C2E8"), // rgba(52, 194, 232, 0.80)
                Color.parseColor("#CCA9EFF9"), // rgba(169, 239, 249, 0.80)
                Color.parseColor("#CCD1FFFF"), // rgba(209, 255, 255, 0.80)
                Color.parseColor("#CCB2F3FA"), // rgba(178, 243, 250, 0.80)
                Color.parseColor("#CC34C2E8")  // rgba(52, 194, 232, 0.80)
            ),
            floatArrayOf(0.0475f, 0.2752f, 0.4850f, 0.6636f, 0.9627f), // CSS 스톱 위치
            Shader.TileMode.CLAMP
        )
    }

    override fun draw(canvas: Canvas) {
        // 네온 글로우(외곽선 블러) — 소프트웨어 레이어에서만 보임
        val glowRect = RectF(rect).apply { inset(-glowBlurPx, -glowBlurPx) }
        canvas.drawRoundRect(glowRect, cornerRadiusPx + glowBlurPx, cornerRadiusPx + glowBlurPx, glowPaint)

        // 보라색 베이스
        basePaint.color = baseColor
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, basePaint)

        // overlay 유사 효과의 투명 그라데이션
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, overlayPaint)
    }

    override fun setAlpha(alpha: Int) {
        basePaint.alpha = alpha
        overlayPaint.alpha = alpha
        glowPaint.alpha = (alpha * (Color.alpha(glowColor) / 255f)).toInt()
        invalidateSelf()
    }
    override fun setColorFilter(colorFilter: ColorFilter?) {
        basePaint.colorFilter = colorFilter
        overlayPaint.colorFilter = colorFilter
        glowPaint.colorFilter = colorFilter
        invalidateSelf()
    }
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

/**
 * 상단 기능 버튼 바를 생성/관리하는 헬퍼
 */
class AIFeatureManager(private val context: Context) {

    companion object {
        private const val COLOR_SECONDARY = "#FF000000" // 검정 배경 (이미 검정색)
    }

    // dp -> px
    private fun Float.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
    private fun Int.dp(): Int = (this * context.resources.displayMetrics.density).toInt()

    fun createAIFeatureButtons(
        onRewritingClick: () -> Unit,
        onSpellCheckClick: () -> Unit,
        onScheduleAddClick: () -> Unit,
    ): LinearLayout {
        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            // setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundColor(Color.parseColor(COLOR_SECONDARY))
        }

        // 메인 레이아웃: 왼쪽 아바타 + 오른쪽 수직 레이아웃
        val mainHorizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        
        // 왼쪽: 동영상 캐릭터 (고정 크기)
        mainHorizontalLayout.addView(createVideoAvatar())
        
        // 오른쪽: 수직 레이아웃 (말풍선 + 버튼들)
        val rightVerticalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(12.dp(), 0, 0, 0)
            }
        }
        
        // 말풍선 (커스텀 드로어블 사용)
        rightVerticalLayout.addView(createSpeechBubble())
        
        // 버튼들 (수직 배치)
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            gravity = android.view.Gravity.START
        }
        buttonLayout.addView(createImageStyleButton("# 감정 기반 리라이팅", onRewritingClick))
        buttonLayout.addView(createImageStyleButton("# 맞춤법", onSpellCheckClick))
        buttonLayout.addView(createImageStyleButton("# 일정", onScheduleAddClick))
        
        rightVerticalLayout.addView(buttonLayout)
        mainHorizontalLayout.addView(rightVerticalLayout)
        mainLayout.addView(mainHorizontalLayout)

        return mainLayout
    }

    private fun createVideoAvatar(): VideoView {
        return VideoView(context).apply {
            layoutParams = LayoutParams(80.dp(), 80.dp()).apply {
                setMargins(0, 0, 0, 0) // 마진 제거
            }
            val videoUri = Uri.parse("android.resource://${context.packageName}/raw/image01")
            setVideoURI(videoUri)
            setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.setVolume(0f, 0f)
            }
            start()
        }
    }

    private fun createSpeechBubble(): LinearLayout {
        val bubble = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 12.dp())
            }
            setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            background = createBubbleBackground()
        }
        val bubbleText = TextView(context).apply {
            text = "사장님께 퇴사 요청 멘트를 도와드릴까요?"
            textSize = 10f
            setTextColor(Color.parseColor("#FF1E3A8A"))
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        bubble.addView(bubbleText)
        return bubble
    }

    private fun createBubbleBackground(): Drawable {
        // 말풍선 본체 + 꼬리를 위한 커스텀 드로어블
        return object : Drawable() {
            private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val path = Path()
            private val rect = RectF()
            
            override fun onBoundsChange(bounds: Rect) {
                super.onBoundsChange(bounds)
                rect.set(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
                
                // 그라데이션 설정
                bubblePaint.shader = LinearGradient(
                    rect.left, rect.centerY(),
                    rect.right, rect.centerY(),
                    intArrayOf(Color.WHITE, Color.parseColor("#FFE0F2FE")),
                    null,
                    Shader.TileMode.CLAMP
                )
                
                // 말풍선 경로 생성 (둥근 모서리 + 왼쪽 꼬리)
                path.reset()
                val cornerRadius = 16.dp().toFloat()
                val tailWidth = 12.dp().toFloat()
                val tailHeight = 16.dp().toFloat()
                
                // 말풍선 본체 (둥근 사각형)
                path.addRoundRect(
                    rect.left + tailWidth, rect.top,
                    rect.right, rect.bottom,
                    cornerRadius, cornerRadius,
                    Path.Direction.CW
                )
                
                // 왼쪽 아래 꼬리 (각진 삼각형)
                val tailStartX = rect.left + 20.dp()
                val tailEndX = tailStartX + tailWidth
                val tailBottomY = rect.bottom + tailHeight
                
                // 각진 삼각형 꼬리 (왼쪽 아래를 향함)
                path.moveTo(tailStartX, rect.bottom)
                path.lineTo(tailEndX, rect.bottom)
                path.lineTo(tailStartX, tailBottomY)
                path.close()
            }
            
            override fun draw(canvas: Canvas) {
                canvas.drawPath(path, bubblePaint)
            }
            
            override fun setAlpha(alpha: Int) {
                bubblePaint.alpha = alpha
                invalidateSelf()
            }
            
            override fun setColorFilter(colorFilter: ColorFilter?) {
                bubblePaint.colorFilter = colorFilter
                invalidateSelf()
            }
            
            @Deprecated("Deprecated in Java")
            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
        }
    }

    // ====== 버튼 생성 (메소드 이름 유지) ======
    private fun createImageStyleButton(text: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            this.text = text
            isAllCaps = false
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, 32.dp()).apply {
                setMargins(2.dp(), 0, 2.dp(), 0)
            }
            minHeight = 0; minWidth = 0 // Material 기본 최소값 제거
            setPadding(3.dp(), 3.dp(), 3.dp(), 3.dp())
            textSize = 10f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER

            // 머터리얼 틴트/리플이 배경을 덮지 않도록
            backgroundTintList = null

            // 중요: 여기서 반드시 createButtonGradient() 호출 (이름 유지)
            background = createButtonGradient()

            // 글로우(BlurMaskFilter) 보이게 소프트웨어 레이어
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            // 라운드에 맞게 클릭/클리핑
            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true

            setOnClickListener { onClick() }
        }
    }

    /**
     * 버튼 그라데이션 배경 생성 (메소드 이름 유지)
     * - 사진과 같은 연한 라벤더 블루에서 중간 파란색으로의 대각선 그라데이션
     */
    private fun createButtonGradient(): Drawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR, // Top-Left to Bottom-Right (대각선)
            intArrayOf(Color.parseColor("#DCE5F5"), Color.parseColor("#5AA0E6"))
        ).apply {
            cornerRadius = 20.dp().toFloat()
        }
    }
}
