package com.example.pong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.UiThread
import com.example.pong.database.AppDatabase
import com.example.pong.database.Score
import com.example.pong.database.ScoreDao
import com.example.pong.surface.GameView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameActivity : AppCompatActivity() {
    private lateinit var score : TextView
    private lateinit var surface : GameView
    private lateinit var database: AppDatabase
    private lateinit var scoreDao: ScoreDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        score = findViewById(R.id.score)
        surface = findViewById(R.id.gameSurface)
        database = AppDatabase.getInstance(this)!!
        scoreDao = database.scoreDao()
        updateScore()
    }

    private fun updateScore() {
        Thread {
            while(!surface.getFinished()) {
                Thread.sleep(50)
                runOnUiThread {
                    score.text = "Score: ${surface.getScore()}"
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        // write score to database
        GlobalScope.launch {
            scoreDao.add(Score(surface.getScore()))
        }
    }

}