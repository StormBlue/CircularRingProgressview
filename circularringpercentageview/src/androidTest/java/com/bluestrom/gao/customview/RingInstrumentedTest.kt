package com.bluestrom.gao.customview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test

class RingInstrumentedTest {
    private fun onMain(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
    private fun ring() = CircularRingPercentageView(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test fun rangeValidationAndClamping() = onMain {
        val view = ring()
        view.max = 300; view.progress = 250
        assertEquals(250, view.progress)
        view.max = 50; assertEquals(50, view.progress)
        view.progress = -1; assertEquals(0, view.progress)
        view.progress = Int.MAX_VALUE; assertEquals(50, view.progress)
        for (invalid in listOf(0, -1)) {
            assertThrows(IllegalArgumentException::class.java) { view.max = invalid }
        }
        assertThrows(IllegalArgumentException::class.java) { view.scaleNumbers = 2001 }
        assertThrows(IllegalArgumentException::class.java) { view.strokeWidth = Float.NaN }
        assertThrows(IllegalArgumentException::class.java) { view.ringWidth = 0f }
    }

    @Test fun stateRestoresMaxBeforeProgressAndAppearance() = onMain {
        val original = ring().apply {
            id = 123; max = 500; progress = 420; scaleNumbers = 80
            strokeWidth = 3f; ringWidth = 17f; drawMode = CircularRingPercentageView.DrawMode.COMPLETE
            finishedStrokeColor = Color.RED; unfinishedStrokeColor = Color.BLUE
        }
        val state = SparseArray<Parcelable>()
        original.saveHierarchyState(state)
        val restored = ring().apply { id = 123; restoreHierarchyState(state) }
        assertEquals(500, restored.max); assertEquals(420, restored.progress)
        assertEquals(80, restored.scaleNumbers); assertEquals(3f, restored.strokeWidth)
        assertEquals(17f, restored.ringWidth); assertEquals(original.drawMode, restored.drawMode)
        assertEquals(Color.RED, restored.finishedStrokeColor); assertEquals(Color.BLUE, restored.unfinishedStrokeColor)
        val info = restored.createAccessibilityNodeInfo()
        assertEquals(420f, info.rangeInfo.current)
        assertEquals(500f, info.rangeInfo.max)
    }

    @Test fun measurementRespectsAtMostAndExactConstraints() = onMain {
        val view = ring()
        view.measure(View.MeasureSpec.makeMeasureSpec(120, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(90, View.MeasureSpec.EXACTLY))
        assertTrue(view.measuredWidth <= 120); assertEquals(90, view.measuredHeight)
    }

    @Test fun pixelsCoverEmptyHalfFullAndGeometryChanges() = onMain {
        val view = ring().apply {
            scaleNumbers = 80; strokeWidth = 3f; ringWidth = 20f
            finishedStrokeColor = Color.RED; unfinishedStrokeColor = Color.BLUE
            setPadding(30, 20, 10, 40)
            layout(0, 0, 320, 240)
        }
        fun pixels(): IntArray {
            val bitmap = Bitmap.createBitmap(320, 240, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val result = IntArray(320 * 240)
            bitmap.getPixels(result, 0, 320, 0, 0, 320, 240)
            bitmap.recycle()
            return result
        }
        for (mode in CircularRingPercentageView.DrawMode.entries) {
            view.drawMode = mode; view.progress = 0
            val empty = pixels(); assertFalse(empty.contains(Color.RED)); assertTrue(empty.contains(Color.BLUE))
            view.progress = 50
            val half = pixels(); assertTrue(half.contains(Color.RED)); assertTrue(half.contains(Color.BLUE))
            if (mode == CircularRingPercentageView.DrawMode.QUARTER) {
                assertEquals(Color.RED, half[110 * 320 + 249])
                assertEquals(Color.BLUE, half[31 * 320 + 170])
            } else {
                assertEquals(Color.BLUE, half[110 * 320 + 249])
                assertEquals(Color.RED, half[31 * 320 + 170])
            }
            view.progress = 100
            val full = pixels(); assertTrue(full.contains(Color.RED)); assertFalse(full.contains(Color.BLUE))
            assertTrue(full.take(20 * 320).all { it == Color.TRANSPARENT })
        }
        val before = pixels()
        view.strokeWidth = 8f; view.ringWidth = 35f; view.scaleNumbers = 40
        assertFalse(before.contentEquals(pixels()))
        view.setPadding(50, 40, 30, 60)
        assertFalse(before.contentEquals(pixels()))
    }
    @Test fun reportSoftwareDrawCostAgainstLegacyLoop() = onMain {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 2f }
        val legacy = object : View(context) {
            override fun onDraw(canvas: Canvas) {
                for (tick in 0 until 200) {
                    canvas.save()
                    paint.color = if (tick <= 100) Color.RED else Color.BLUE
                    canvas.rotate(360f / 200 * tick, 160f, 160f)
                    canvas.drawLine(0f, 160f, 24f, 160f, paint)
                    canvas.restore()
                }
            }
        }
        val modern = ring().apply {
            drawMode = CircularRingPercentageView.DrawMode.COMPLETE
            strokeWidth = 2f; ringWidth = 24f; progress = 50
        }
        legacy.layout(0, 0, 320, 320); modern.layout(0, 0, 320, 320)
        val bitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        repeat(100) { legacy.draw(canvas); modern.draw(canvas) }
        fun cost(view: View): Long {
            val start = System.nanoTime()
            repeat(200) { view.draw(canvas) }
            return (System.nanoTime() - start) / 200
        }
        val old = mutableListOf<Long>(); val new = mutableListOf<Long>()
        repeat(5) { old += cost(legacy); new += cost(modern) }
        android.util.Log.i("RingBenchmark", "software draw median ns: legacy=${old.sorted()[2]}, cached=${new.sorted()[2]}; 200 ticks, 320px, 50%, 5x200 draws after warmup")
        bitmap.recycle()
    }

}
