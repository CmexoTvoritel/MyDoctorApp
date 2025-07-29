package com.asc.mydoctorapp.core.data.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("/user/login")
    suspend fun login(@Body request: RequestBody): Response<LoginResponse>
    
    @POST("/register")
    suspend fun register(@Body request: RequestBody): Response<ResponseBody>
    
    @POST("/get_doctors_by_clinic_name")
    suspend fun doctors(@Body request: RequestBody): Response<List<DoctorDto>>
    
    @PUT("/add_appointment_data")
    suspend fun book(@Body request: RequestBody): Response<ResponseBody>
    
    @POST("/promt_bot")
    suspend fun prompt(@Body request: RequestBody): Response<ChatDto>

    @POST("/user_confirmed_events")
    suspend fun doctorRecords(@Body request: RequestBody): Response<List<RecordDto>>

    @POST("/get_user")
    suspend fun getUser(@Body request: RequestBody): Response<UserDto>

    @PUT("/change_user_data")
    suspend fun changeUserData(@Body request: RequestBody): Response<ResponseBody>

    @POST("/get_clinics_by_name")
    suspend fun getClinicByName(@Body request: RequestBody): Response<List<ClinicInfoDto>>
}
