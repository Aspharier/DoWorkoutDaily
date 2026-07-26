package com.aspharier.doworkoutdaily.data.repository

import com.aspharier.doworkoutdaily.data.local.WorkoutDao
import com.aspharier.doworkoutdaily.data.model.WorkoutEntry
import com.aspharier.doworkoutdaily.data.model.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutRepository(private val dao: WorkoutDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Sessions
    suspend fun insertSession(session: WorkoutSession): Long =
        withContext(Dispatchers.IO) { dao.insertSession(session) }

    suspend fun updateSession(session: WorkoutSession) =
        withContext(Dispatchers.IO) { dao.updateSession(session) }

    suspend fun deleteSession(session: WorkoutSession) =
        withContext(Dispatchers.IO) { dao.deleteSession(session) }

    fun getSessionByDate(date: String): Flow<WorkoutSession?> =
        dao.getSessionByDate(date)

    suspend fun getSessionByDateOnce(date: String): WorkoutSession? =
        withContext(Dispatchers.IO) { dao.getSessionByDateOnce(date) }

    fun getAllSessions(): Flow<List<WorkoutSession>> =
        dao.getAllSessions()

    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<WorkoutSession>> =
        dao.getSessionsInRange(startDate, endDate)

    fun getCompletedSessionCount(): Flow<Int> =
        dao.getCompletedSessionCount()

    // Entries
    suspend fun insertEntry(entry: WorkoutEntry): Long =
        withContext(Dispatchers.IO) { dao.insertEntry(entry) }

    suspend fun updateEntry(entry: WorkoutEntry) =
        withContext(Dispatchers.IO) { dao.updateEntry(entry) }

    suspend fun deleteEntry(entry: WorkoutEntry) =
        withContext(Dispatchers.IO) { dao.deleteEntry(entry) }

    fun getEntriesForSession(sessionId: Long): Flow<List<WorkoutEntry>> =
        dao.getEntriesForSession(sessionId)

    fun getEntriesByExerciseName(name: String): Flow<List<WorkoutEntry>> =
        dao.getEntriesByExerciseName(name)

    fun getAllExerciseNames(): Flow<List<String>> =
        dao.getAllExerciseNames()

    fun getTotalKm(): Flow<Double> =
        dao.getTotalKm().map { it ?: 0.0 }

    // Streak calculation
    suspend fun calculateStreak(): Int = withContext(Dispatchers.IO) {
        val sessions = dao.getAllSessions().first()
        val completedDates = sessions
            .filter { it.isCompleted }
            .map { LocalDate.parse(it.date, dateFormatter) }
            .sortedDescending()

        if (completedDates.isEmpty()) return@withContext 0

        var streak = 0
        var expectedDate = LocalDate.now()

        // If today hasn't been completed yet, start from yesterday
        if (!completedDates.contains(expectedDate)) {
            expectedDate = expectedDate.minusDays(1)
        }

        for (date in completedDates) {
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date.isBefore(expectedDate)) {
                break
            }
        }
        streak
    }

    // Week sessions
    suspend fun getWeekSessions(): List<WorkoutSession> = withContext(Dispatchers.IO) {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val endOfWeek = startOfWeek.plusDays(6)
        dao.getSessionsInRange(
            startOfWeek.format(dateFormatter),
            endOfWeek.format(dateFormatter)
        ).first()
    }

    // Yesterday's entries
    suspend fun getYesterdayEntries(): List<WorkoutEntry> = withContext(Dispatchers.IO) {
        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
        val session = dao.getSessionByDateOnce(yesterday)
        if (session != null) {
            dao.getEntriesForSession(session.id).first()
        } else {
            emptyList()
        }
    }
}
