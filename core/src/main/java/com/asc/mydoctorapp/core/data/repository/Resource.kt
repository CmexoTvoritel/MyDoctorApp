package com.asc.mydoctorapp.core.data.repository

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Exception) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

class AppException(message: String) : Exception(message)
