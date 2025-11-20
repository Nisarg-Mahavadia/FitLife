package com.example.project

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class BMICircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var bmiValue: Float = 0f
    private var sweepAngle: Float = 0f
    private val maxBMI = 40f

    private val backgroundPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 35f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaintValue = Paint().apply {
        color = Color.BLACK
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    private val textPaintLabel = Paint().apply {
        color = Color.DKGRAY
        textSize = 38f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun setBMI(bmi: Float) {
        bmiValue = bmi
        sweepAngle = (bmi / maxBMI) * 360f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val radius = size / 2f - 40f
        val cx = width / 2f
        val cy = height / 2f

        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        // Background arc
        canvas.drawArc(rect, -90f, 360f, false, backgroundPaint)

        // Color based on BMI
        progressPaint.color = when {
            bmiValue < 18.5 -> Color.parseColor("#64B5F6") // Blue
            bmiValue <= 24.9 -> Color.parseColor("#81C784") // Green
            else -> Color.parseColor("#E57373") // Red
        }

        // Progress arc
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)

        // Draw "Your BMI:" label ABOVE the number
        canvas.drawText(
            "Your BMI:",
            cx,
            cy - 40f,   // moved up
            textPaintLabel
        )

        // Draw BMI value
        canvas.drawText(
            String.format("%.1f", bmiValue),
            cx,
            cy + 30f,
            textPaintValue
        )
    }
}
