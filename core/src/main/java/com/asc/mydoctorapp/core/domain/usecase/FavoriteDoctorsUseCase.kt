package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.data.remote.DoctorUIItem
import com.asc.mydoctorapp.core.database.dao.FavoriteDoctorDao
import com.asc.mydoctorapp.core.database.entity.FavoriteDoctorEntity
import javax.inject.Inject

class FavoriteDoctorsUseCase @Inject constructor(
    private val favoriteDoctorDao: FavoriteDoctorDao
) {
    
    // Генерирует уникальный email для врача на основе имени и фамилии
    fun generateDoctorEmail(doctorName: String, doctorSurname: String): String {
        val fullName = "$doctorName $doctorSurname"
        return "doctor${fullName.hashCode()}@clinic.com"
    }
    
    suspend fun getFavoriteDoctorsByUser(userEmail: String): List<DoctorUIItem> {
        return favoriteDoctorDao.getFavoriteDoctorsByUser(userEmail).map { entity ->
            DoctorUIItem(
                id = entity.doctorEmail, // ОРИГИНАЛЬНЫЙ email для навигации!
                name = entity.doctorName,
                surname = entity.doctorSurname,
                specialty = entity.specialty,
                rating = entity.rating,
                photoRes = entity.photoRes,
                isFavorite = true,
                clinic = entity.clinic,
                favoriteKey = entity.favoriteKey
            )
        }
    }
    
    suspend fun getFavoriteDoctorsCountByUser(userEmail: String): Int {
        return favoriteDoctorDao.getFavoriteDoctorsCountByUser(userEmail)
    }
    
    suspend fun isFavoriteDoctor(userEmail: String, doctorEmail: String): Boolean {
        return favoriteDoctorDao.getFavoriteDoctor(userEmail, doctorEmail) != null
    }
    
    suspend fun addFavoriteDoctor(userEmail: String, doctorEmail: String, favoriteKey: String, name: String, surname: String, specialty: String, rating: Float, photoRes: Int?, clinic: String) {
        val id = "${userEmail}_${favoriteKey}"
        val entity = FavoriteDoctorEntity(id, userEmail, doctorEmail, favoriteKey, name, surname, specialty, rating, photoRes, clinic)
        favoriteDoctorDao.insertFavoriteDoctor(entity)
    }
    
    suspend fun removeFavoriteDoctor(userEmail: String, favoriteKey: String) {
        favoriteDoctorDao.deleteFavoriteDoctor(userEmail, favoriteKey)
    }
    
    suspend fun getFavoriteDoctor(userEmail: String, favoriteKey: String): FavoriteDoctorEntity? {
        return favoriteDoctorDao.getFavoriteDoctor(userEmail, favoriteKey)
    }
    
    suspend fun updateUserEmail(oldEmail: String, newEmail: String) {
        favoriteDoctorDao.updateUserEmail(oldEmail, newEmail)
    }
}
