package com.example.pong.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score (@ColumnInfo(name = "value") var value: Int) {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
}
