package com.aspharier.doworkoutdaily.data.local

import androidx.room.*
import com.aspharier.doworkoutdaily.data.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLog): Long

    @Delete
    suspend fun deleteWorkout(workout: WorkoutLog)

    @Query("SELECT * FROM workout_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getWorkoutsByDate(date: String): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentWorkouts(limit: Int = 10): Flow<List<WorkoutLog>>

    @Query("SELECT DISTINCT date FROM workout_logs ORDER BY date DESC")
    fun getAllWorkoutDates(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM workout_logs WHERE date = :date")
    fun getWorkoutCountForDate(date: String): Flow<Int>

    @Query("SELECT DISTINCT date FROM workout_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getWorkoutDatesBetween(startDate: String, endDate: String): Flow<List<String>>

    @Query("SELECT COUNT(DISTINCT date) FROM workout_logs")
    fun getTotalWorkoutDays(): Flow<Int>

    @Query("SELECT COUNT(*) FROM workout_logs")
    fun getTotalWorkouts(): Flow<Int>

    @Query("SELECT date, COUNT(*) as count FROM workout_logs WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    fun getWorkoutCountsBetween(startDate: String, endDate: String): Flow<List<DateCount>>
}

data class DateCount(
    val date: String,
    val count: Int
)
