package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.UserInfo
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): UserInfo {
        return authRepository.getUserInfo()
    }
    
    suspend fun getUserEmail(): String {
        return authRepository.getUserInfo().login ?: ""
    }
}