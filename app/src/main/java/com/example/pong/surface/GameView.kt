package com.example.pong.surface

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.VelocityTracker
import kotlin.random.Random

class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    private var batWidth = 250
    private var batHeight = 50
    private var batSpeed = 15F
    private var ballRadius = 20F
    private var vy = 20F
    private var vx = 10F
    private var vMax = 30
    private var vMin = 10
    private lateinit var bats: Array<Bat>
    private lateinit var ball: Ball
    private var thread: GameThread
    private var mVelocityTracker: VelocityTracker? = null
    private var score: Int = 0
    private var finished = false
    private val white = Paint().apply {
        color = Color.WHITE
        textSize = 100F
        textAlign = Paint.Align.CENTER
    }

    private val purple = Paint().apply {
        color = Color.parseColor("#B388FF")
    }

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.running = false
        thread.join()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        batWidth = width / 4
        ball = Ball(
            ballRadius,
            0F,
            (height / 10).toFloat(),
            vx,
            vy
        )
        val startW = (width / 2 - batWidth / 2).toFloat()
        val startH = (height / 15 - batHeight / 2).toFloat()
        bats = arrayOf(
            Bat(
                batWidth.toFloat(),
                batHeight.toFloat(),
                startW,
                startH,
                batSpeed
            ),
            Bat(
                batWidth.toFloat(),
                batHeight.toFloat(),
                startW,
                height - startH,
                batSpeed
            )
        )
        thread.running = true
        thread.start()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas == null) {
            return
        }
        canvas.drawCircle(ball.posX, ball.posY, ball.radius, white)
        // computer
        canvas.drawRect(bats[0].posX, bats[0].posY, bats[0].posX + bats[0].batWidth,
            bats[0].posY + bats[0].batHeight, white)
        // human
        canvas.drawRect(bats[1].posX, bats[1].posY, bats[1].posX + bats[1].batWidth,
            bats[1].posY - bats[1].batHeight, purple)

        if(finished) {
            // game over
            canvas.drawText("Game Over!", (width / 2).toFloat(), (height / 2).toFloat(), white)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mVelocityTracker?.clear()
                mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                mVelocityTracker?.addMovement(event)
                if (finished) {
                    (context as Activity).finish()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker?.run {
                    addMovement(event)
                    computeCurrentVelocity(1000)

                    val xVelocity = getXVelocity(event.getPointerId(event.actionIndex))
                    if (event.y >= height / 2) {
                        // move bat
                        if (xVelocity > 0 && bats[1].posX + bats[1].batWidth <= width) {
                            bats[1].posX += bats[1].speed
                        } else if (xVelocity < 0 && bats[1].posX >= 0) {
                            bats[1].posX -= bats[1].speed
                        }
                    }
                }
            }
            else -> {
                // make possible to reuse by others
                mVelocityTracker?.recycle()
                mVelocityTracker = null
            }
        }
        return true
    }

    fun update() {
        // check if ball bounces off bat
        for  (i in bats.indices) {
            if (kotlin.math.abs(
                    ball.posY + ball.radius - bats[i].posY + bats[i].batHeight + 2 * (i - 1) * bats[i].batHeight).toInt() <= ball.radius + bats[i].batHeight / 10 &&
                ball.posX - ball.radius <=  bats[i].posX + bats[i].batWidth && ball.posX + ball.radius >=  bats[i].posX) {
                // change velocity after bouncing
                vx = Random.nextInt(vMin, vMax) * vx / vx
                vy = - vy
                // increase score
                if (i == 1) {
                    score++
                }
            }
        }
        if (!(ball.posX - ballRadius > 0 && ball.posX + ball.radius <= width)) {
            vx = -vx
        }
        if (ball.posX - 10 <= 0) {
            ball.posX = 2 * ball.radius
        }
        ball.posX += vx
        ball.posY += vy

        if (ball.posY > bats[1].posY) {
            // game over
            finished = true
            return
        }

        // move the other bat according to ball position, when ball is on its half of screen
        bats[0].posX = ball.posX -  bats[0].batWidth / 2
        if (bats[0].posX + bats[0].batWidth > width) {
            bats[0].posX = width - bats[0].batWidth
        }
        if (bats[0].posX < 0) {
            bats[0].posX = 0F
        }
    }

    fun getScore() : Int {
        return score
    }

    fun getFinished() : Boolean {
        return finished
    }
}