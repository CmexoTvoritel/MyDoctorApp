package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.database.dao.RecordDao
import com.asc.mydoctorapp.core.database.entity.RecordEntity
import javax.inject.Inject

class RecordsDatabaseUseCase @Inject constructor(
    private val recordDao: RecordDao
) {
    
    suspend fun saveRecords(records: List<RecordEntity>) {
        recordDao.insertRecords(records)
    }
    
    suspend fun getCancelledRecordsByUser(userEmail: String): List<RecordEntity> {
        return recordDao.getCancelledRecordsByUser(userEmail)
    }
    
    suspend fun getActiveRecordIdsByUser(userEmail: String): List<String> {
        return recordDao.getActiveRecordIdsByUser(userEmail)
    }
    
    suspend fun markRecordsAsCancelled(recordIds: List<String>, userEmail: String) {
        recordDao.markRecordsAsCancelled(recordIds, userEmail)
    }
    
    suspend fun updateCancelledStatus(serverRecordIds: List<String>, userEmail: String) {
        val activeRecordIds = getActiveRecordIdsByUser(userEmail)
        val cancelledIds = activeRecordIds.filter { it !in serverRecordIds }
        if (cancelledIds.isNotEmpty()) {
            markRecordsAsCancelled(cancelledIds, userEmail)
        }
    }
    
    suspend fun updateUserEmail(oldEmail: String, newEmail: String) {
        recordDao.updateUserEmail(oldEmail, newEmail)
    }
}
