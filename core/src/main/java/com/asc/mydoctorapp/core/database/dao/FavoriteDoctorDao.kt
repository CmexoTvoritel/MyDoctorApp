package com.asc.mydoctorapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asc.mydoctorapp.core.database.entity.FavoriteDoctorEntity

@Dao
interface FavoriteDoctorDao {
    
    @Query("SELECT * FROM favorite_doctors WHERE userEmail = :userEmail")
    suspend fun getFavoriteDoctorsByUser(userEmail: String): List<FavoriteDoctorEntity>
    
    @Query("SELECT COUNT(*) FROM favorite_doctors WHERE userEmail = :userEmail")
    suspend fun getFavoriteDoctorsCountByUser(userEmail: String): Int
    
    @Query("SELECT * FROM favorite_doctors WHERE userEmail = :userEmail AND favoriteKey = :favoriteKey LIMIT 1")
    suspend fun getFavoriteDoctor(userEmail: String, favoriteKey: String): FavoriteDoctorEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteDoctor(favoriteDoctor: FavoriteDoctorEntity)
    
    @Query("DELETE FROM favorite_doctors WHERE userEmail = :userEmail AND favoriteKey = :favoriteKey")
    suspend fun deleteFavoriteDoctor(userEmail: String, favoriteKey: String)
    
    @Query("DELETE FROM favorite_doctors WHERE userEmail = :userEmail")
    suspend fun deleteAllFavoriteDoctorsByUser(userEmail: String)
    
    @Query("UPDATE favorite_doctors SET userEmail = :newEmail WHERE userEmail = :oldEmail")
    suspend fun updateUserEmail(oldEmail: String, newEmail: String)
}
