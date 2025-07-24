package com.asc.mydoctorapp.core.data.repository

import com.asc.mydoctorapp.core.data.mapper.toDomain
import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.data.remote.DoctorDto
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
    private var doctorCache: List<DoctorDto> = emptyList()

    override suspend fun getDoctors(clinicName: String): List<Doctor> {
        // Если кэш не пустой, возвращаем его
        if (doctorCache.isNotEmpty()) {
            return doctorCache.map { it.toDomain() }
        }
        
        // Иначе загружаем с сервера
        return try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            
            val requestBody = formRequestBodyForDoctors(clinicName, token)
            val response = apiService.doctors(requestBody)
            
            if (response.isSuccessful) {
                val doctorsInitial = response.body()
                doctorCache = doctorsInitial ?: emptyList()
                val doctors = doctorsInitial?.map { it.toDomain() } ?: emptyList()
                doctors
            } else {
                throw AppException("Failed to get doctors: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Failed to get doctors: ${e.message}")
        }
    }
    
    override suspend fun getDoctorByEmail(email: String): Doctor {
        // Проверяем, есть ли доктор в кэше
        val cachedDoctor = doctorCache.find { it.email == email }
        if (cachedDoctor != null) {
            return cachedDoctor.toDomain()
        }
        
        // Если кэш пуст или доктора нет в кэше, загружаем список докторов с сервера
        val doctors = getDoctors("Clinic1")
        
        // Ищем доктора в обновленном кэше
        return doctors.find { it.email == email }
            ?: throw AppException("Doctor with email $email not found")
    }

    override suspend fun bookAppointment(request: AppointmentRequest) {
        try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            
            val requestBody = formRequestBodyForAppointment(request, token)
            val response = apiService.book(requestBody)
            
            if (!response.isSuccessful) {
                throw AppException("Failed to book appointment: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Failed to book appointment: ${e.message}")
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
        val dateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        
        val parameters = JSONObject().apply {
            put("token", token)
            put("doctor_email", request.doctorEmail)
            put("date_time", request.dateTime.format(dateTimeFormat))
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
}
