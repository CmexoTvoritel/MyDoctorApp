package com.asc.mydoctorapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.asc.mydoctorapp.core.database.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    
    @Query("SELECT * FROM records WHERE userEmail = :userEmail")
    suspend fun getAllRecordsByUser(userEmail: String): List<RecordEntity>
    
    @Query("SELECT * FROM records WHERE userEmail = :userEmail AND isCancelled = 1")
    suspend fun getCancelledRecordsByUser(userEmail: String): List<RecordEntity>
    
    @Query("SELECT * FROM records WHERE userEmail = :userEmail AND isCancelled = 0")
    suspend fun getActiveRecordsByUser(userEmail: String): List<RecordEntity>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecord(record: RecordEntity)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecords(records: List<RecordEntity>)
    
    @Update
    suspend fun updateRecord(record: RecordEntity)
    
    @Query("UPDATE records SET isCancelled = 1 WHERE id = :recordId AND userEmail = :userEmail")
    suspend fun markRecordAsCancelled(recordId: String, userEmail: String)
    
    @Query("UPDATE records SET isCancelled = 1 WHERE id IN (:recordIds) AND userEmail = :userEmail")
    suspend fun markRecordsAsCancelled(recordIds: List<String>, userEmail: String)
    
    @Query("DELETE FROM records WHERE id = :recordId AND userEmail = :userEmail")
    suspend fun deleteRecord(recordId: String, userEmail: String)
    
    @Query("SELECT id FROM records WHERE userEmail = :userEmail AND isCancelled = 0")
    suspend fun getActiveRecordIdsByUser(userEmail: String): List<String>
    
    @Query("UPDATE records SET userEmail = :newEmail WHERE userEmail = :oldEmail")
    suspend fun updateUserEmail(oldEmail: String, newEmail: String)
}
