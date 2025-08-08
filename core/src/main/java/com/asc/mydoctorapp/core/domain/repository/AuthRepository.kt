package com.asc.mydoctorapp.core.domain.repository

import com.asc.mydoctorapp.core.domain.model.UserInfo
import com.asc.mydoctorapp.core.domain.model.UserToken
import java.time.LocalDate

interface AuthRepository {
    suspend fun login(login: String, password: String): UserToken
    suspend fun register(name: String, birth: LocalDate, login: String, password: String, phone: String): UserToken
    suspend fun getUserInfo(): UserInfo
    suspend fun getUpdateUserInfo(): UserInfo
    suspend fun changeUserInfo(name: String?, birth: String?, login: String?): Boolean
}
