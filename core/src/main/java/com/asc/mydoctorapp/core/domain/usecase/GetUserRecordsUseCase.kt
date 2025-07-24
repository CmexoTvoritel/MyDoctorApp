package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.RecordInfo
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import javax.inject.Inject

class GetUserRecordsUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    suspend operator fun invoke(): List<RecordInfo> {
        return doctorRepository.getUserRecords()
    }
}