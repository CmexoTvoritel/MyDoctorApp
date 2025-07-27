package com.asc.mydoctorapp.core.data.repository

import com.asc.mydoctorapp.core.data.mapper.toDomain
import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.domain.model.UserInfo
import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import com.asc.mydoctorapp.core.utils.PreferencesManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    
    private val mediaTypeJson = "application/json"
    private var userInfo: UserInfo? = null
    
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

    override suspend fun getUserInfo(): UserInfo {
        return try {
            if (userInfo != null) {
                return userInfo!!
            }
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")

            val requestBody = formRequestBodyForUserInfo(token)
            val response = apiService.getUser(requestBody)

            if (response.isSuccessful) {
                val userServerInfo = response.body()?.toDomain() ?: throw AppException("Empty response from server")
                userInfo = userServerInfo
                userServerInfo
            } else {
                UserInfo("", "", "")
            }
        } catch (e: Exception) {
            UserInfo("", "", "")
        }
    }

    override suspend fun getUpdateUserInfo(): UserInfo {
        hardUpdateUserInfo()
        return userInfo!!
    }

    private suspend fun hardUpdateUserInfo() {
        try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")

            val requestBody = formRequestBodyForUserInfo(token)
            val response = apiService.getUser(requestBody)

            if (response.isSuccessful) {
                val userServerInfo = response.body()?.toDomain() ?: throw AppException("Empty response from server")
                userInfo = userServerInfo
            } else {
                userInfo = UserInfo("", "", "")
            }
        } catch (e: Exception) {
           userInfo = UserInfo("", "", "")
        }
    }

    override suspend fun changeUserInfo(name: String?, birth: String?, login: String?): Boolean {
        return try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
            val requestBody = formRequestBodyForChangeUserInfo(
                token = token,
                name = name,
                birth = birth,
                login = login
            )
            val response = apiService.changeUserData(requestBody)
            if (response.isSuccessful) {
                hardUpdateUserInfo()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            return false
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

    private fun formRequestBodyForUserInfo(token: String): RequestBody {
        val parameters = JSONObject().apply {
            put("token", token)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }

    private fun formRequestBodyForChangeUserInfo(
        token: String?,
        name: String?,
        birth: String?,
        login: String?
    ): RequestBody {
        val root = JSONObject()
        token?.let { tk ->
            root.put("token", JSONObject().put("token", tk))
        }
        val req = JSONObject().apply {
            name?.let { put("name", it) }
            login?.let { put("login", it) }
            birth?.let { put("birth", it) }
//            put("address", "")
//            put("main_doctor_id", "")
//            put("clinic_id", "")
        }
        if (req.length() > 0) {
            root.put("req", req)
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return root.toString().toRequestBody(mediaType)
    }
}
