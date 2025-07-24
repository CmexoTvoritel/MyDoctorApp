package com.asc.mydoctorapp.core.domain.model

import com.asc.mydoctorapp.core.data.remote.WorkingDays

data class Doctor(
    val name: String,
    val surname: String,
    val specialty: String,
    val email: String,
    val clinic: String,
    val workingDays: WorkingDays?
)
