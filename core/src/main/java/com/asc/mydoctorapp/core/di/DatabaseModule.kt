package com.asc.mydoctorapp.core.di

import android.content.Context
import com.asc.mydoctorapp.core.database.AppDatabase
import com.asc.mydoctorapp.core.database.dao.RecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideRecordDao(database: AppDatabase): RecordDao {
        return database.recordDao()
    }
}
