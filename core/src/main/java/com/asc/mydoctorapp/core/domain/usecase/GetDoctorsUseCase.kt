package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import javax.inject.Inject

class GetDoctorsUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    suspend operator fun invoke(clinicName: String): List<Doctor> {
        return doctorRepository.getDoctors(clinicName)
    }
}
