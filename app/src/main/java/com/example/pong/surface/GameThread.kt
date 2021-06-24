package com.example.pong.surface

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    private var canvas: Canvas? = null
    var running = true
    private val targetFPS = 25

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var targetTime = (1000 / targetFPS).toLong()

        super.run()
        while(running) {
            startTime = System.nanoTime()
            canvas = surfaceHolder.lockCanvas()
            gameView.draw(canvas)
            gameView.update()
            surfaceHolder.unlockCanvasAndPost(canvas)
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            if (waitTime >= 0) {
                sleep(waitTime)
            }
        }
    }
}