package com.asc.mydoctorapp.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.asc.mydoctorapp.core.database.dao.RecordDao
import com.asc.mydoctorapp.core.database.dao.FavoriteDoctorDao
import com.asc.mydoctorapp.core.database.dao.ChatSessionDao
import com.asc.mydoctorapp.core.database.entity.RecordEntity
import com.asc.mydoctorapp.core.database.entity.FavoriteDoctorEntity
import com.asc.mydoctorapp.core.database.entity.ChatSessionEntity

@Database(
    entities = [RecordEntity::class, FavoriteDoctorEntity::class, ChatSessionEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun recordDao(): RecordDao
    abstract fun favoriteDoctorDao(): FavoriteDoctorDao
    abstract fun chatSessionDao(): ChatSessionDao
    
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
