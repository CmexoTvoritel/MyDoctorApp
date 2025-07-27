package com.asc.mydoctorapp.core.data.di

import com.asc.mydoctorapp.core.data.remote.ApiService
import com.asc.mydoctorapp.core.data.repository.AuthRepositoryImpl
import com.asc.mydoctorapp.core.data.repository.ChatRepositoryImpl
import com.asc.mydoctorapp.core.data.repository.DoctorRepositoryImpl
import com.asc.mydoctorapp.core.domain.repository.AuthRepository
import com.asc.mydoctorapp.core.domain.repository.ChatRepository
import com.asc.mydoctorapp.core.domain.repository.DoctorRepository
import com.asc.mydoctorapp.core.utils.PreferencesManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://backend-3-4sig.onrender.com"
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService, preferencesManager: PreferencesManager): AuthRepository {
        return AuthRepositoryImpl(apiService, preferencesManager)
    }
    
    @Provides
    @Singleton
    fun provideDoctorRepository(apiService: ApiService, preferencesManager: PreferencesManager): DoctorRepository {
        return DoctorRepositoryImpl(apiService, preferencesManager)
    }
    
    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService, preferencesManager: PreferencesManager): ChatRepository {
        return ChatRepositoryImpl(apiService, preferencesManager)
    }
}
