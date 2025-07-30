package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import javax.inject.Inject

class GetDoctorByEmailUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    suspend operator fun invoke(email: String, clinicName: String): Doctor {
        return doctorRepository.getDoctorByEmail(email, clinicName)
    }
}
