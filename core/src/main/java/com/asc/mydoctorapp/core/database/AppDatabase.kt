package com.asc.mydoctorapp.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.asc.mydoctorapp.core.database.dao.RecordDao
import com.asc.mydoctorapp.core.database.dao.FavoriteDoctorDao
import com.asc.mydoctorapp.core.database.entity.RecordEntity
import com.asc.mydoctorapp.core.database.entity.FavoriteDoctorEntity

@Database(
    entities = [RecordEntity::class, FavoriteDoctorEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun recordDao(): RecordDao
    abstract fun favoriteDoctorDao(): FavoriteDoctorDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mydoctor_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
