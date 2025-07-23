package com.asc.mydoctorapp.core.data.repository

import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    
    private val mediaTypeJson = "application/json"
    
    override suspend fun login(login: String, password: String): UserToken {
        return try {
            val requestBody = formRequestBodyForLogin(login, password)
            val response = apiService.login(requestBody)
            
            if (response.isSuccessful) {
                // Получаем токен как строку из тела ответа
                val tokenString =
                    response.body()?.accessToken ?: throw AppException(response.body()?.error ?: "")
                UserToken(tokenString)
            } else {
                throw AppException("Login failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Login failed: ${e.message}")
        }
    }
    
    override suspend fun register(name: String, birth: LocalDate, login: String, password: String): UserToken {
        return try {
            val requestBody = formRequestBodyForRegister(name, birth, login, password)
            val response = apiService.register(requestBody)
            
            if (response.isSuccessful) {
                // Получаем токен как строку из тела ответа
                val tokenString = response.body()?.string() ?: throw AppException("Empty response from server")
                // Создаем UserToken из полученной строки
                UserToken(tokenString)
            } else {
                throw AppException("Registration failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Registration failed: ${e.message}")
        }
    }
    
    private fun formRequestBodyForLogin(
        login: String,
        password: String
    ): RequestBody {
        val parameters = JSONObject().apply {
            put("login", login)
            put("password", password)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
    
    private fun formRequestBodyForRegister(
        name: String,
        birth: LocalDate,
        login: String,
        password: String
    ): RequestBody {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        val parameters = JSONObject().apply {
            put("name", name)
            put("birth", birth.format(dateFormatter))
            put("login", login)
            put("password", password)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
}
