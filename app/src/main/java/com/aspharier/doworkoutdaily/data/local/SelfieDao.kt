package com.aspharier.doworkoutdaily.data.local

import androidx.room.*
import com.aspharier.doworkoutdaily.data.model.DailySelfie
import kotlinx.coroutines.flow.Flow

@Dao
interface SelfieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelfie(selfie: DailySelfie)

    @Delete
    suspend fun deleteSelfie(selfie: DailySelfie)

    @Query("SELECT * FROM daily_selfies WHERE date = :date")
    suspend fun getSelfieByDate(date: String): DailySelfie?

    @Query("SELECT * FROM daily_selfies WHERE date = :date")
    fun getSelfieFlowByDate(date: String): Flow<DailySelfie?>

    @Query("SELECT * FROM daily_selfies")
    fun getAllSelfies(): Flow<List<DailySelfie>>
}
