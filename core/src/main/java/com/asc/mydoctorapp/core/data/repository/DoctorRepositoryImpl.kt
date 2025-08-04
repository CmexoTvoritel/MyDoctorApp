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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : DoctorRepository {

    private val mediaTypeJson = "application/json"
    
    // Кэш докторов
    private var doctorCache: Map<String, List<DoctorDto>> = mapOf()
    private var clinicCache: List<Clinic> = emptyList()

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
            val requestedResponse = apiService.patientRecords(requestBody)
            if (response.isSuccessful) {
                val confirmedEvents = (response.body()?.map { 
                    val formattedStart = formatDateTime(it.start)
                    val formattedEnd = formatDateTime(it.end)
                    RecordInfo(
                        start = formattedStart,
                        end = formattedEnd,
                        docName = it.docName,
                        docSurname = it.docSurname,
                        docSpecialty = it.docSpecialty,
                        email = it.email,
                        isConfirmed = true,
                        clinicName = it.clinicName
                    )
                }) ?: emptyList()
                
                val requestedEvents = (requestedResponse.body()?.map { 
                    val formattedStart = formatDateTime(it.start)
                    val formattedEnd = formatDateTime(it.end)
                    RecordInfo(
                        start = formattedStart,
                        end = formattedEnd,
                        docName = it.docName,
                        docSurname = it.docSurname,
                        docSpecialty = it.docSpecialty,
                        email = it.email,
                        isConfirmed = false,
                        clinicName = it.clinicName
                    )
                }) ?: emptyList()
                
                // Sort by date - earliest first
                val allEvents = (confirmedEvents + requestedEvents)
                return allEvents.sortedBy { parseDateTime(it.start) }
            } else {
                throw AppException("Failed to get user events: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    private fun formatDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""
        
        return try {
            // Parse input format: "2025-7-30T14:0:00"
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            val dateTime = LocalDateTime.parse(dateTimeString, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            dateTimeString // Return original if parsing fails
        }
    }
    
    private fun parseDateTime(dateTimeString: String?): LocalDateTime? {
        if (dateTimeString.isNullOrEmpty()) return null
        
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            LocalDateTime.parse(dateTimeString, formatter)
        } catch (e: DateTimeParseException) {
            null
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

    override suspend fun getAllClinics(): List<Clinic> {
        return clinicCache.ifEmpty {
            getClinicByName("")
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
            put("doctor_email", request.doctorEmail)
            put("token", token)
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
