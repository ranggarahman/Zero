package com.example.zero.ui.utils

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.ContextCompat
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptFocal
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptOptions
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.CirclePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal


/**
 * Prompt background implementation that darkens behind the circle background.
 */
class CircleDimmedPromptBackground : CirclePromptBackground() {

    private val dimBounds = RectF()

    private val dimPaint: Paint = Paint()

    init {
        dimPaint.color = Color.BLACK
    }

    override fun prepare(
        options: PromptOptions<out PromptOptions<*>>,
        clipToBounds: Boolean,
        clipBounds: Rect
    ) {
        super.prepare(options, clipToBounds, clipBounds)
        val metrics = Resources.getSystem().displayMetrics
        // Set the bounds to display as dimmed to the screen bounds
        dimBounds[0f, 0f, metrics.widthPixels.toFloat()] = metrics.heightPixels.toFloat()
    }

    override fun update(
        options: PromptOptions<out PromptOptions<*>>,
        revealModifier: Float,
        alphaModifier: Float
    ) {
        super.update(options, revealModifier, alphaModifier)
        // Allow for the dimmed background to fade in and out
        dimPaint.alpha = (200 * alphaModifier).toInt()
    }

    override fun draw(canvas: Canvas) {
        // Draw the dimmed background
        canvas.drawRect(dimBounds, dimPaint)
        // Draw the background
        super.draw(canvas)
    }
}

class RectDimmedPromptBackground : RectanglePromptBackground() {

    private val dimBounds = RectF()

    private val dimPaint: Paint = Paint()

    init {
        dimPaint.color = Color.BLACK
    }

    override fun prepare(
        options: PromptOptions<out PromptOptions<*>>,
        clipToBounds: Boolean,
        clipBounds: Rect
    ) {
        super.prepare(options, clipToBounds, clipBounds)
        val metrics = Resources.getSystem().displayMetrics
        // Set the bounds to display as dimmed to the screen bounds
        dimBounds[0f, 0f, metrics.widthPixels.toFloat()] = metrics.heightPixels.toFloat()
    }

    override fun update(
        options: PromptOptions<out PromptOptions<*>>,
        revealModifier: Float,
        alphaModifier: Float
    ) {
        super.update(options, revealModifier, alphaModifier)
        // Allow for the dimmed background to fade in and out
        dimPaint.alpha = (200 * alphaModifier).toInt()
    }

    override fun draw(canvas: Canvas) {
        // Draw the dimmed background
        canvas.drawRect(dimBounds, dimPaint)
        // Draw the background
        super.draw(canvas)
    }
}


class TransparentPromptFocal : RectanglePromptFocal() {

    private val transparentPaint = Paint().apply {
        color = Color.TRANSPARENT
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(bounds, 15f, 15f, transparentPaint)
    }
}

//class RoundedGreenDimmedPromptBackground : RectanglePromptBackground() {
//
//    private val greenPaint: Paint = Paint().apply {
//        isAntiAlias = true
//        color = ContextCompat.getColor(context, R.color.green_1) // Use the specified green color
//    }
//
//    private val dimPaint: Paint = Paint().apply {
//        color = ContextCompat.getColor(context, R.color.dimmed_background) // Use the dimmed background color
//    }
//
//    private val rectF = RectF()
//    private val cornerRadius = 16f // Adjust as needed for the roundedness
//
//    override fun prepare(
//        options: PromptOptions<out PromptOptions<*>>,
//        clipToBounds: Boolean,
//        clipBounds: Rect
//    ) {
//        super.prepare(options, clipToBounds, clipBounds)
//        val metrics = Resources.getSystem().displayMetrics
//        // Set the bounds to display as dimmed to the screen bounds
//        dimBounds[0f, 0f, metrics.widthPixels.toFloat()] = metrics.heightPixels.toFloat()
//    }
//
//    override fun draw(canvas: Canvas) {
//        // Draw the dimmed background
//        canvas.drawRect(dimBounds, dimPaint)
//        // Draw the rounded green rectangle
//        rectF.set(bounds)
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, greenPaint)
//    }
//}

