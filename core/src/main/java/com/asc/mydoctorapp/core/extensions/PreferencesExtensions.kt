package com.asc.mydoctorapp.core.extensions

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.saveString(key: String, value: String?) {
    edit { putString(key, value) }
}

fun SharedPreferences.saveBoolean(key: String, value: Boolean) {
    edit { putBoolean(key, value) }
}

fun SharedPreferences.saveInt(key: String, value: Int) {
    edit { putInt(key, value) }
}

fun SharedPreferences.saveLong(key: String, value: Long) {
    edit { putLong(key, value) }
}