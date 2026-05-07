package com.aspharier.doworkoutdaily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,              // ISO format: "2026-05-07"
    val workoutType: String,       // WorkoutType name
    val durationMinutes: Int,
    val notes: String = "",
    val sets: Int? = null,         // Optional — for gym users
    val reps: Int? = null,         // Optional — for gym users
    val weightKg: Float? = null,   // Optional — for gym users
    val timestamp: Long = System.currentTimeMillis()
)
