package com.bluestrom.gao.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.bluestrom.gao.circularringpercentageview.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/** A determinate tick ring. Mutate on the UI thread; dimensions in the API are pixels. */
class CircularRingPercentageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    enum class DrawMode { COMPLETE, QUARTER }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var points = FloatArray(0)
    private var geometryDirty = true
    private var animator: ValueAnimator? = null
    private var updatingAnimation = false

    var max: Int = 100
        set(value) {
            require(value > 0) { "max must be positive" }
            if (field == value) return
            cancelAnimation()
            field = value
            progress = progress.coerceAtMost(value)
            changed()
        }
    var progress: Int = 0
        set(value) {
            if (!updatingAnimation) cancelAnimation()
            val clamped = value.coerceIn(0, max)
            if (field == clamped) return
            field = clamped
            changed()
        }
    var scaleNumbers: Int = 200
        set(value) {
            require(value in 4..MAX_TICK_COUNT) { "tick count must be in 4..$MAX_TICK_COUNT" }
            if (field == value) return
            field = value
            geometryChanged()
        }
    var strokeWidth: Float = dp(2f)
        set(value) {
            require(value.isFinite() && value > 0f) { "tick width must be finite and positive" }
            if (field == value) return
            field = value
            geometryChanged()
        }
    var ringWidth: Float = dp(24f)
        set(value) {
            require(value.isFinite() && value > 0f) { "tick length must be finite and positive" }
            if (field == value) return
            field = value
            geometryChanged()
        }
    var finishedStrokeColor: Int = Color.rgb(0, 127, 115)
        set(value) { if (field != value) { field = value; invalidate() } }
    var unfinishedStrokeColor: Int = 0x3367757D
        set(value) { if (field != value) { field = value; invalidate() } }
    var drawMode: DrawMode = DrawMode.QUARTER
        set(value) { if (field != value) { field = value; geometryChanged() } }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircularRingPercentageView, defStyleAttr, 0)
        try {
            max = a.getInt(R.styleable.CircularRingPercentageView_crpv_max, max)
            progress = a.getInt(R.styleable.CircularRingPercentageView_crpv_progress, progress)
            scaleNumbers = a.getInt(R.styleable.CircularRingPercentageView_crpv_tickCount, scaleNumbers)
            strokeWidth = a.getDimension(R.styleable.CircularRingPercentageView_crpv_tickWidth, strokeWidth)
            ringWidth = a.getDimension(R.styleable.CircularRingPercentageView_crpv_tickLength, ringWidth)
            finishedStrokeColor = a.getColor(R.styleable.CircularRingPercentageView_crpv_progressColor, finishedStrokeColor)
            unfinishedStrokeColor = a.getColor(R.styleable.CircularRingPercentageView_crpv_trackColor, unfinishedStrokeColor)
            val mode = a.getInt(R.styleable.CircularRingPercentageView_crpv_drawMode, 1)
            require(mode in 0..1) { "Unknown draw mode: $mode" }
            drawMode = DrawMode.entries[mode]
        } finally { a.recycle() }
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    private fun dp(value: Float) = value * resources.displayMetrics.density
    private fun changed() {
        postInvalidateOnAnimation()
        if (isAttachedToWindow) sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
    }
    private fun geometryChanged() { geometryDirty = true; invalidate() }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = dp(160f).roundToInt()
        setMeasuredDimension(
            resolveSizeAndState(maxOf(suggestedMinimumWidth, desired + paddingLeft + paddingRight), widthMeasureSpec, 0),
            resolveSizeAndState(maxOf(suggestedMinimumHeight, desired + paddingTop + paddingBottom), heightMeasureSpec, 0),
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { geometryDirty = true }
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        geometryDirty = true
    }
    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        geometryDirty = true
    }

    override fun onRtlPropertiesChanged(layoutDirection: Int) {
        super.onRtlPropertiesChanged(layoutDirection)
        geometryDirty = true
    }

    // Allocate and compute trigonometry only when geometry changes, never on progress updates.
    private fun rebuildGeometry() {
        val w = (width - paddingLeft - paddingRight).coerceAtLeast(0)
        val h = (height - paddingTop - paddingBottom).coerceAtLeast(0)
        val radius = ((min(w, h) - strokeWidth) / 2f).coerceAtLeast(0f)
        val inner = (radius - ringWidth).coerceAtLeast(0f)
        val cx = paddingLeft + w / 2f
        val cy = paddingTop + h / 2f
        val order = (0 until scaleNumbers).toList().let { indices ->
            if (drawMode == DrawMode.COMPLETE) indices
            else indices.sortedBy { i ->
                val phase = i.toDouble() / scaleNumbers
                min(min(phase, 1.0 - phase), kotlin.math.abs(phase - 0.5))
            }
        }
        points = FloatArray(scaleNumbers * 4)
        order.forEachIndexed { index, tick ->
            val angle = Math.PI + 2.0 * Math.PI * tick / scaleNumbers
            val x = cos(angle).toFloat()
            val y = sin(angle).toFloat()
            val offset = index * 4
            points[offset] = cx + x * inner
            points[offset + 1] = cy + y * inner
            points[offset + 2] = cx + x * radius
            points[offset + 3] = cy + y * radius
        }
        geometryDirty = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (geometryDirty) rebuildGeometry()
        paint.strokeWidth = strokeWidth
        val count = (progress.toDouble() / max * scaleNumbers).roundToInt() * 4
        if (count < points.size) {
            paint.color = unfinishedStrokeColor
            canvas.drawLines(points, count, points.size - count, paint)
        }
        if (count > 0) {
            paint.color = finishedStrokeColor
            canvas.drawLines(points, 0, count, paint)
        }
    }

    /** Optional explicit animation; direct progress assignments are immediate. */
    @JvmOverloads fun animateProgressTo(target: Int, durationMillis: Long = 400L) {
        require(durationMillis >= 0) { "duration must not be negative" }
        cancelAnimation()
        val end = target.coerceIn(0, max)
        if (durationMillis == 0L || !isAttachedToWindow || (android.os.Build.VERSION.SDK_INT >= 26 && !ValueAnimator.areAnimatorsEnabled())) {
            progress = end
            return
        }
        animator = ValueAnimator.ofInt(progress, end).apply {
            duration = durationMillis
            addUpdateListener {
                updatingAnimation = true
                try { progress = it.animatedValue as Int } finally { updatingAnimation = false }
            }
            start()
        }
    }

    fun cancelAnimation() { animator?.cancel(); animator = null }
    override fun onDetachedFromWindow() { cancelAnimation(); super.onDetachedFromWindow() }

    override fun getAccessibilityClassName(): CharSequence = android.widget.ProgressBar::class.java.name
    @Suppress("DEPRECATION")
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.rangeInfo = AccessibilityNodeInfo.RangeInfo.obtain(
            AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT, 0f, max.toFloat(), progress.toFloat(),
        )
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable("super", super.onSaveInstanceState())
        putInt("max", max); putInt("progress", progress); putInt("ticks", scaleNumbers)
        putFloat("width", strokeWidth); putFloat("length", ringWidth)
        putInt("finished", finishedStrokeColor); putInt("track", unfinishedStrokeColor)
        putInt("mode", drawMode.ordinal)
    }

    @Suppress("DEPRECATION")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is Bundle) { super.onRestoreInstanceState(state); return }
        cancelAnimation()
        super.onRestoreInstanceState(state.getParcelable("super"))
        max = state.getInt("max", 100)
        progress = state.getInt("progress")
        scaleNumbers = state.getInt("ticks", 200)
        strokeWidth = state.getFloat("width", dp(2f)); ringWidth = state.getFloat("length", dp(24f))
        finishedStrokeColor = state.getInt("finished"); unfinishedStrokeColor = state.getInt("track")
        drawMode = DrawMode.entries[state.getInt("mode", 1)]
        geometryChanged()
    }

    companion object { const val MAX_TICK_COUNT = 2000 }
}
