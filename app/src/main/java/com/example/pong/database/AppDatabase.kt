package com.example.pong.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Score::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    companion object {
        private var instance: AppDatabase? = null
        // create singleton of class
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    "scores").fallbackToDestructiveMigration().build()
            }
            return instance
        }

        fun deleteInstanceOfDatabase() {
            instance = null
        }
    }
}