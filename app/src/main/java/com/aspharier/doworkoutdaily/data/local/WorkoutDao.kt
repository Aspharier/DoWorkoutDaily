package com.aspharier.doworkoutdaily.data.local

import androidx.room.*
import com.aspharier.doworkoutdaily.data.model.WorkoutEntry
import com.aspharier.doworkoutdaily.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    fun getSessionByDate(date: String): Flow<WorkoutSession?>

    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDateOnce(date: String): WorkoutSession?

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<WorkoutSession>>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE isCompleted = 1")
    fun getCompletedSessionCount(): Flow<Int>

    // Entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WorkoutEntry): Long

    @Update
    suspend fun updateEntry(entry: WorkoutEntry)

    @Delete
    suspend fun deleteEntry(entry: WorkoutEntry)

    @Query("SELECT * FROM workout_entries WHERE sessionId = :sessionId ORDER BY orderIndex")
    fun getEntriesForSession(sessionId: Long): Flow<List<WorkoutEntry>>

    @Query("SELECT * FROM workout_entries WHERE exerciseName = :name ORDER BY sessionId")
    fun getEntriesByExerciseName(name: String): Flow<List<WorkoutEntry>>

    @Query("SELECT DISTINCT exerciseName FROM workout_entries")
    fun getAllExerciseNames(): Flow<List<String>>

    @Query("SELECT SUM(value) FROM workout_entries WHERE unit = 'km'")
    fun getTotalKm(): Flow<Double?>
}
