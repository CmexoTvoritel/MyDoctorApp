package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): UserToken {
        return authRepository.login(login, password)
    }
}
