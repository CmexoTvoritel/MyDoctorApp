package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class ChangeUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String?, birth: String?, login: String?): Boolean {
        return authRepository.changeUserInfo(name, birth, login)
    }
}