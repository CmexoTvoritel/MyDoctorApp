package com.asc.mydoctorapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.asc.mydoctorapp.core.database.entity.ChatSessionEntity

@Dao
interface ChatSessionDao {
    
    @Query("SELECT * FROM chat_sessions WHERE userEmail = :userEmail AND sessionDate = :sessionDate")
    suspend fun getSessionsByUserAndDate(userEmail: String, sessionDate: String): ChatSessionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)
    
    @Update
    suspend fun updateSession(session: ChatSessionEntity)
    
    @Query("UPDATE chat_sessions SET userEmail = :newEmail WHERE userEmail = :oldEmail")
    suspend fun updateUserEmail(oldEmail: String, newEmail: String)
}
