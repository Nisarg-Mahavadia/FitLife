package com.marwadiuniversity.fitlife

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.min

class BMICircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var bmiValue: Float = 0f
    private var sweepAngle: Float = 0f
    private val maxBMI = 40f

    // Light gray track circle
    private val backgroundPaint = Paint().apply {
        color = 0xFFD3D3D3.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 35f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    // Progress circle paint
    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    // BMI number text paint
    private val textPaintValue = Paint().apply {
        color = Color.BLACK
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    // BMI label text paint
    private val textPaintLabel = Paint().apply {
        color = Color.DKGRAY
        textSize = 38f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Status arc color exposed to match text ✅
    var currentArcColor: Int = 0
        private set

    fun setBMI(bmi: Float) {
        bmiValue = bmi
        sweepAngle = (bmi / maxBMI) * 360f
        currentArcColor = getColorByBMI(bmi) // Save current color ✅
        progressPaint.color = currentArcColor
        invalidate()
    }

    // Get color from colors.xml based on BMI range ✅
    private fun getColorByBMI(bmi: Float): Int {
        return when {
            bmi < 18.5f -> context.getColor(R.color.bmi_underweight)
            bmi <= 24.9f -> context.getColor(R.color.bmi_normal)
            else -> context.getColor(R.color.bmi_overweight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val radius = size / 2f - 40f
        val cx = width / 2f
        val cy = height / 2f

        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        // Draw the background circle
        canvas.drawArc(rect, -90f, 360f, false, backgroundPaint)

        // Draw progress circle with dynamic color
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)

        // Draw BMI label above the number
        canvas.drawText("Your BMI:", cx, cy - 40f, textPaintLabel)

        // Draw BMI value
        canvas.drawText(String.format("%.1f", bmiValue), cx, cy + 30f, textPaintValue)
    }
}
