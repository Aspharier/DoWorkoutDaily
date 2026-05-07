package com.aspharier.doworkoutdaily.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aspharier.doworkoutdaily.data.model.WorkoutLog

@Database(
    entities = [WorkoutLog::class, com.aspharier.doworkoutdaily.data.model.DailySelfie::class],
    version = 2,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun selfieDao(): SelfieDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getInstance(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
