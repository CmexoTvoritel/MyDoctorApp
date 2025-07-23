package com.asc.mydoctorapp.core.domain.repository

import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor

interface DoctorRepository {
    suspend fun getDoctors(clinicName: String): List<Doctor>
    suspend fun bookAppointment(request: AppointmentRequest)
}
