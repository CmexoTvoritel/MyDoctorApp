package com.asc.mydoctorapp.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.asc.mydoctorapp.core.extensions.saveBoolean
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private var preferenceManager: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val PREFERENCES_KEY_ONBOARDING_SHOWN = "PREFERENCES_KEY_ONBOARDING_SHOWN"
    }

    var isOnboardingShown: Boolean
        get() = preferenceManager.getBoolean(PREFERENCES_KEY_ONBOARDING_SHOWN, false)
        set(value) = preferenceManager.saveBoolean(key = PREFERENCES_KEY_ONBOARDING_SHOWN, value = value)
}