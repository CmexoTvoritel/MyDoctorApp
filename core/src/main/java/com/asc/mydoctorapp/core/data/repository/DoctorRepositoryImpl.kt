package com.asc.mydoctorapp.core.data.repository

import com.asc.mydoctorapp.core.data.mapper.toDomain
import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.data.remote.DoctorDto
import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.domain.model.RecordInfo
import com.asc.mydoctorapp.core.utils.PreferencesManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : DoctorRepository {

    private val mediaTypeJson = "application/json"
    
    // Кэш докторов
    private var doctorCache: Map<String, List<DoctorDto>> = mapOf()

    override suspend fun getDoctors(clinicName: String): List<Doctor> {
        if (!doctorCache[clinicName].isNullOrEmpty()) {
            return doctorCache[clinicName]!!.map { it.toDomain(clinicName) }
        }
        
        // Иначе загружаем с сервера
        return try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            
            val requestBody = formRequestBodyForDoctors(clinicName, token)
            val response = apiService.doctors(requestBody)
            
            if (response.isSuccessful) {
                val doctorsInitial = response.body()
                doctorCache += (clinicName to (doctorsInitial ?: emptyList()))
                val doctors = doctorsInitial?.map { it.toDomain(clinicName) } ?: emptyList()
                doctors
            } else {
                throw AppException("Failed to get doctors: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Failed to get doctors: ${e.message}")
        }
    }
    
    override suspend fun getDoctorByEmail(email: String, clinicName: String): Doctor {
        doctorCache[clinicName]
            ?.firstOrNull { it.email == email }
            ?.let { return it.toDomain(clinicName) }

        val doctors = getDoctors(clinicName)
        return doctors.firstOrNull { it.email == email }
            ?: throw AppException("Doctor with email $email not found in clinic $clinicName")
    }

    override suspend fun bookAppointment(request: AppointmentRequest): Boolean {
        try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            
            val requestBody = formRequestBodyForAppointment(request, token)
            val response = apiService.book(requestBody)
            if (response.isSuccessful) {
                return true
            } else {
                throw AppException("Failed to book appointment: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun getUserRecords(): List<RecordInfo> {
        try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            val requestBody = formRequestBodyForUserEvents(token)
            val response = apiService.doctorRecords(requestBody)
            if (response.isSuccessful) {
                return (response.body()?.map { it.toDomain() }) ?: emptyList()
            } else {
                throw AppException("Failed to get user events: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun getClinicByName(query: String): List<Clinic> {
        return try {
            val requestBody = formRequestBodyForClinics(query)
            val response = apiService.getClinicByName(requestBody)
            if (response.isSuccessful) {
                (response.body()?.map { it.toDomain() }) ?: emptyList()
            } else {
                throw AppException("Failed to get clinics: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun formRequestBodyForDoctors(
        clinicName: String,
        token: String
    ): RequestBody {
        val parameters = JSONObject().apply {
            put("token", token)
            put("clinic_name", clinicName)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
    
    private fun formRequestBodyForAppointment(
        request: AppointmentRequest,
        token: String
    ): RequestBody {
        val parameters = JSONObject().apply {
            put("token", token)
            put("doctor_email", request.doctorEmail)
            put("day", request.day)
            put("month", request.month)
            put("year", request.year)
            put("hour", request.hour)
            put("minutes", request.minutes)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }

    private fun formRequestBodyForUserEvents(token: String): RequestBody {
        val parameters = JSONObject().apply {
            put("token", token)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }

    private fun formRequestBodyForClinics(query: String): RequestBody {
        val parameters = JSONObject().apply {
            put("clinic_name", query)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
}
