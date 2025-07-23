package com.asc.mydoctorapp.core.domain.repository

import com.asc.mydoctorapp.core.domain.model.UserToken
import java.time.LocalDate

interface AuthRepository {
    suspend fun login(login: String, password: String): UserToken
    suspend fun register(name: String, birth: LocalDate, login: String, password: String): UserToken
}
