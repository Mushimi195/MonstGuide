package com.example.monstguide

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TrajectoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintLine = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val paintBall = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
        alpha = 150
    }

    private val paintClose = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
        alpha = 100
    }
    
    private val paintText = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private var ballPosition = PointF(500f, 1000f)
    private var dragStartPosition = PointF()
    private var isDraggingBall = false
    private var isAiming = false
    private var aimVector = PointF(0f, 0f)

    private val ballRadius = 60f
    private val closeBtnSize = 100f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw Close Button
        canvas.drawRect(0f, 0f, closeBtnSize, closeBtnSize, paintClose)
        canvas.drawText("X", closeBtnSize / 2, closeBtnSize / 2 + 15f, paintText)

        // Draw Ball
        canvas.drawCircle(ballPosition.x, ballPosition.y, ballRadius, paintBall)

        // Draw Trajectory
        if (isAiming) {
            drawTrajectory(canvas)
        }
    }

    private fun drawTrajectory(canvas: Canvas) {
        var currentX = ballPosition.x
        var currentY = ballPosition.y
        
        // Normalize aim vector
        val length = sqrt(aimVector.x * aimVector.x + aimVector.y * aimVector.y)
        if (length == 0f) return
        
        var dirX = aimVector.x / length
        var dirY = aimVector.y / length

        val step = 10f // Step size for simulation
        val maxSteps = 2000 // Max length of line

        val path = android.graphics.Path()
        path.moveTo(currentX, currentY)

        for (i in 0 until maxSteps) {
            currentX += dirX * step
            currentY += dirY * step

            // Reflection logic
            if (currentX <= 0 || currentX >= width) {
                dirX = -dirX
                currentX = if (currentX <= 0) 0f else width.toFloat()
            }
            if (currentY <= 0 || currentY >= height) {
                dirY = -dirY
                currentY = if (currentY <= 0) 0f else height.toFloat()
            }

            path.lineTo(currentX, currentY)
        }
        canvas.drawPath(path, paintLine)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if touching close button
                if (event.x < closeBtnSize && event.y < closeBtnSize) {
                    context.stopService(Intent(context, OverlayService::class.java))
                    return true
                }

                // Check if touching ball
                val dx = event.x - ballPosition.x
                val dy = event.y - ballPosition.y
                if (dx * dx + dy * dy <= ballRadius * ballRadius) {
                    isDraggingBall = true
                } else {
                    isAiming = true
                    dragStartPosition.set(event.x, event.y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDraggingBall) {
                    ballPosition.set(event.x, event.y)
                } else if (isAiming) {
                    // Slingshot mechanic: Vector is Start - Current
                    aimVector.set(dragStartPosition.x - event.x, dragStartPosition.y - event.y)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isDraggingBall = false
                // Keep aiming true so the line stays visible? 
                // Or clear it? Usually you want it to stay or clear.
                // Let's keep it visible until next touch for now, or clear it if it was a "shot".
                // For a guide, maybe we want it to persist?
                // Let's clear aiming on UP to avoid clutter, or toggle?
                // User request: "Guide app". Usually you hold to aim.
                // Let's clear it on UP for now, but maybe add a "Lock" feature later.
                // Actually, if I clear it, I can't see the result after I lift my finger.
                // But if I'm playing the game, I'm using my finger to shoot in the game.
                // So this overlay is just a visual guide.
                // If I touch the screen, the game ALSO receives the touch (if I pass it through).
                // Wait, FLAG_NOT_TOUCH_MODAL?
                // If I consume the touch here, the game won't get it.
                // If I want to aim in the game, I need to touch the game.
                // But this overlay is "on top".
                // If I want to use the guide *simultaneously* with the game, I need to pass touches?
                // But if I pass touches, I can't interact with my overlay (move ball).
                
                // Current design: Overlay intercepts touches.
                // 1. Move Ball to character.
                // 2. Drag on overlay to see line.
                // 3. Memorize angle.
                // 4. Hide overlay or minimize? Or just ignore it?
                // 5. Shoot in game.
                
                // Ideally, the overlay should be "transparent" to touches except for specific controls.
                // But to "aim" the guide, we need to touch.
                
                // Let's stick to: Touch = Update Guide. 
                // If you want to shoot in game, you might need to toggle the overlay off or make it "pass through".
                // For this MVP, I'll just keep the line visible after UP so you can see it.
                // And tap the "X" to close.
            }
        }
        return true
    }
}
