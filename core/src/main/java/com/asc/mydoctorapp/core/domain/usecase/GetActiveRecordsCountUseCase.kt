package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class GetActiveRecordsCountUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    
    companion object {
        const val MAX_ACTIVE_RECORDS = 10
    }
    
    suspend operator fun invoke(): Int {
        return try {
            val userRecords = doctorRepository.getUserRecords()
            val currentTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            
            // Подсчитываем активные (текущие) записи - те, которые запланированы на будущее или настоящее время
            val activeRecordsCount = userRecords.count { record ->
                try {
                    val recordTime = LocalDateTime.parse(record.start, formatter)
                    recordTime.isAfter(currentTime) || recordTime.isEqual(currentTime)
                } catch (e: DateTimeParseException) {
                    false
                }
            }
            
            activeRecordsCount
        } catch (e: Exception) {
            0 // В случае ошибки возвращаем 0
        }
    }
    
    suspend fun isRecordLimitReached(): Boolean {
        return invoke() >= MAX_ACTIVE_RECORDS
    }
}
