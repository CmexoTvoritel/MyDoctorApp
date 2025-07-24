package com.asc.mydoctorapp.core.domain.repository

import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.RecordInfo

interface DoctorRepository {
    suspend fun getDoctors(clinicName: String): List<Doctor>
    suspend fun getDoctorByEmail(email: String): Doctor
    suspend fun bookAppointment(request: AppointmentRequest): Boolean
    suspend fun getUserRecords(): List<RecordInfo>
}
