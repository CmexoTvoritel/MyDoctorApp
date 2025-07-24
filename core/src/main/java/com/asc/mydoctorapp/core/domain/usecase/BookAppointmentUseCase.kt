package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import javax.inject.Inject

class BookAppointmentUseCase @Inject constructor(
    private val doctorRepository: DoctorRepository
) {
    suspend operator fun invoke(request: AppointmentRequest): Boolean {
        return doctorRepository.bookAppointment(request)
    }
}
