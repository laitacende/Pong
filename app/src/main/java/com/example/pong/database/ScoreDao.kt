package com.example.pong.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(score: Score) : Long

    @Query("SELECT * FROM scores ORDER BY value DESC LIMIT 5")
    fun getTopFive() : List<Score>

    @Query("DELETE FROM scores WHERE uid NOT IN (SELECT uid FROM scores ORDER BY value DESC LIMIT 5)" )
    fun deleteExpired()

    @Query("DELETE FROM scores WHERE value > 0")
    fun delete()
}