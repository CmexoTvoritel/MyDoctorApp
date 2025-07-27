package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import javax.inject.Inject

class GetClinicByQueryUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    suspend operator fun invoke(query: String): List<Clinic> {
        return doctorRepository.getClinicByName(query)
    }
}