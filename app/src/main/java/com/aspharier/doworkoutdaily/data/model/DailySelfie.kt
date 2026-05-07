package com.aspharier.doworkoutdaily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_selfies")
data class DailySelfie(
    @PrimaryKey
    val date: String, // ISO format: "2026-05-07"
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
