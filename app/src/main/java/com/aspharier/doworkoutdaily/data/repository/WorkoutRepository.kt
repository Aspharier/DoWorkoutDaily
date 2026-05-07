package com.aspharier.doworkoutdaily.data.repository

import com.aspharier.doworkoutdaily.data.local.WorkoutDao
import com.aspharier.doworkoutdaily.data.local.SelfieDao
import com.aspharier.doworkoutdaily.data.model.DailySelfie
import com.aspharier.doworkoutdaily.data.model.WorkoutLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutRepository(
    private val dao: WorkoutDao,
    private val selfieDao: SelfieDao
) {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun logWorkout(workout: WorkoutLog): Long {
        return dao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutLog) {
        dao.deleteWorkout(workout)
    }

    fun getWorkoutsForDate(date: LocalDate): Flow<List<WorkoutLog>> {
        return dao.getWorkoutsByDate(date.format(dateFormatter))
    }

    fun getWorkoutsForToday(): Flow<List<WorkoutLog>> {
        return getWorkoutsForDate(LocalDate.now())
    }

    fun getAllWorkouts(): Flow<List<WorkoutLog>> {
        return dao.getAllWorkouts()
    }

    fun getRecentWorkouts(limit: Int = 10): Flow<List<WorkoutLog>> {
        return dao.getRecentWorkouts(limit)
    }

    fun getTotalWorkoutDays(): Flow<Int> {
        return dao.getTotalWorkoutDays()
    }

    fun getTotalWorkouts(): Flow<Int> {
        return dao.getTotalWorkouts()
    }

    fun getWorkoutCountForToday(): Flow<Int> {
        return dao.getWorkoutCountForDate(LocalDate.now().format(dateFormatter))
    }

    /**
     * Returns a map of date strings to workout counts for heatmap display.
     */
    fun getHeatmapData(startDate: LocalDate, endDate: LocalDate): Flow<Map<String, Int>> {
        return dao.getWorkoutDatesBetween(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        ).map { dates ->
            dates.groupingBy { it }.eachCount()
        }
    }

    /**
     * Calculates the current streak — consecutive days with workouts ending today.
     */
    fun getCurrentStreak(): Flow<Int> {
        return dao.getAllWorkoutDates().map { dateStrings ->
            calculateStreak(dateStrings)
        }
    }

    /**
     * Calculates the longest streak ever.
     */
    fun getLongestStreak(): Flow<Int> {
        return dao.getAllWorkoutDates().map { dateStrings ->
            calculateLongestStreak(dateStrings)
        }
    }

    private fun calculateStreak(dateStrings: List<String>): Int {
        if (dateStrings.isEmpty()) return 0

        val dates = dateStrings
            .mapNotNull { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
            .distinct()
            .sortedDescending()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // Streak must include today or yesterday
        if (dates.isEmpty() || (dates.first() != today && dates.first() != yesterday)) return 0

        var streak = 1
        for (i in 0 until dates.size - 1) {
            if (dates[i].minusDays(1) == dates[i + 1]) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateLongestStreak(dateStrings: List<String>): Int {
        if (dateStrings.isEmpty()) return 0

        val dates = dateStrings
            .mapNotNull { runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull() }
            .distinct()
            .sorted()

        var longest = 1
        var current = 1

        for (i in 1 until dates.size) {
            if (dates[i - 1].plusDays(1) == dates[i]) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 1
            }
        }
        return longest
    }

    /**
     * Returns all workout dates for heatmap rendering.
     */
    fun getAllWorkoutDatesWithCounts(): Flow<Map<LocalDate, Int>> {
        return dao.getAllWorkouts().map { logs ->
            logs.groupBy { LocalDate.parse(it.date, dateFormatter) }
                .mapValues { it.value.size }
        }
    }

    /**
     * Returns workouts count for the current week.
     */
    fun getThisWeekCount(): Flow<Int> {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return dao.getWorkoutDatesBetween(
            startOfWeek.format(dateFormatter),
            today.format(dateFormatter)
        ).map { it.size }
    }

    suspend fun saveSelfie(date: LocalDate, imagePath: String) {
        selfieDao.insertSelfie(DailySelfie(date.format(dateFormatter), imagePath))
    }

    suspend fun getSelfieByDate(date: LocalDate): DailySelfie? {
        return selfieDao.getSelfieByDate(date.format(dateFormatter))
    }

    fun getSelfieFlowByDate(date: LocalDate): Flow<DailySelfie?> {
        return selfieDao.getSelfieFlowByDate(date.format(dateFormatter))
    }

    fun getAllSelfies(): Flow<List<DailySelfie>> {
        return selfieDao.getAllSelfies()
    }
}
