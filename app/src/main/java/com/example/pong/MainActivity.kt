package com.example.pong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.pong.database.AppDatabase
import com.example.pong.database.Score
import com.example.pong.database.ScoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var scoreDao: ScoreDao
    private lateinit var leaderboard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.start).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        database = AppDatabase.getInstance(this)!!
        scoreDao = database.scoreDao()
        GlobalScope.launch {
            scoreDao.deleteExpired()
        }
        leaderboard = findViewById(R.id.leaderboard)

    }

    override fun onResume() {
        super.onResume()
        var list : List<Score>? = null
        GlobalScope.launch(Dispatchers.IO) {
            list = scoreDao.getTopFive()
            withContext(Dispatchers.Main) {
                var s = ""
                for (i in list!!.indices) {
                    s += list!![i].value.toString() + "\n"
                }
                leaderboard.text = s
            }
        }
    }
}