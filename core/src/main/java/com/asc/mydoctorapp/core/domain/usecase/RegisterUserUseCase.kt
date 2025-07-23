package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import java.time.LocalDate
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, birth: LocalDate, login: String, password: String): UserToken {
        return authRepository.register(name, birth, login, password)
    }
}
