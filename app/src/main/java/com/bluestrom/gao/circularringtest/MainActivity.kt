package com.bluestrom.gao.circularringtest

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import com.bluestrom.gao.customview.CircularRingPercentageView

class MainActivity : Activity() {
    private var animator: ValueAnimator? = null
    private lateinit var slider: SeekBar
    private lateinit var rings: List<CircularRingPercentageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_main)
        val content = findViewById<View>(R.id.content)
        val base = (24 * resources.displayMetrics.density).toInt()
        content.setOnApplyWindowInsetsListener { view, insets ->
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                val bars = insets.getInsets(android.view.WindowInsets.Type.systemBars() or android.view.WindowInsets.Type.displayCutout())
                view.setPadding(base + bars.left, base + bars.top, base + bars.right, base + bars.bottom)
            } else {
                @Suppress("DEPRECATION")
                view.setPadding(base + insets.systemWindowInsetLeft, base + insets.systemWindowInsetTop, base + insets.systemWindowInsetRight, base + insets.systemWindowInsetBottom)
            }
            insets
        }
        content.requestApplyInsets()
        rings = listOf(findViewById(R.id.complete), findViewById(R.id.quarter))
        slider = findViewById(R.id.progress)
        slider.progress = savedInstanceState?.getInt("progress") ?: 65
        val ticks = savedInstanceState?.getInt("ticks") ?: 200
        rings.forEach { it.scaleNumbers = ticks }
        fun showProgress(value: Int) {
            rings.forEach { it.progress = value }
            findViewById<TextView>(R.id.progress_label).text = getString(R.string.progress, value)
        }
        showProgress(slider.progress)
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) animator?.cancel()
                showProgress(progress)
            }
            override fun onStartTrackingTouch(bar: SeekBar) { animator?.cancel() }
            override fun onStopTrackingTouch(bar: SeekBar) = Unit
        })
        findViewById<View>(R.id.animate).setOnClickListener {
            animator?.cancel()
            animator = ValueAnimator.ofInt(slider.progress, 100).apply {
                duration = 1500
                addUpdateListener { slider.progress = it.animatedValue as Int }
                start()
            }
        }
        findViewById<View>(R.id.reset).setOnClickListener { animator?.cancel(); slider.progress = 0 }
        fun showTicks() { findViewById<TextView>(R.id.tick_label).text = getString(R.string.ticks, rings.first().scaleNumbers) }
        showTicks()
        findViewById<View>(R.id.density).setOnClickListener {
            val count = if (rings.first().scaleNumbers == 200) 80 else 200
            rings.forEach { it.scaleNumbers = count }
            showTicks()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("progress", slider.progress)
        outState.putInt("ticks", rings.first().scaleNumbers)
        super.onSaveInstanceState(outState)
    }
    override fun onStop() { animator?.cancel(); super.onStop() }
}
