package com.asc.mydoctorapp.core.data.repository

import com.asc.mydoctorapp.core.data.mapper.toDomain
import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.domain.model.ChatMessage
import com.asc.mydoctorapp.core.domain.repository.ChatRepository
import com.asc.mydoctorapp.core.utils.PreferencesManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : ChatRepository {

    private val mediaTypeJson = "application/json"

    override suspend fun sendPrompt(prompt: String): ChatMessage {
        return try {
            val token = preferencesManager.userToken
                ?: throw AppException("Unauthorized. Please login first.")
                
            val requestBody = formRequestBodyForChat(prompt, token)
            val response = apiService.prompt(requestBody)
            
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw AppException("Empty response from server")
            } else {
                throw AppException("Failed to send prompt: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw AppException("Failed to send prompt: ${e.message}")
        }
    }
    
    private fun formRequestBodyForChat(
        prompt: String,
        token: String
    ): RequestBody {
        val parameters = JSONObject().apply {
            put("token", token)
            put("prompt_text", prompt)
        }.toString()
        return parameters.toRequestBody(mediaTypeJson.toMediaTypeOrNull())
    }
}
