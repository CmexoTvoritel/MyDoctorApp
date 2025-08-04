package com.asc.mydoctorapp.core.data.mapper

import com.asc.mydoctorapp.core.database.entity.RecordEntity
import com.asc.mydoctorapp.core.data.remote.RecordUI

// Note: RecordUI is now defined in core.data.remote, we'll create extension functions for mapping

fun RecordEntity.toRecordUi(): RecordUI {
    return RecordUI(
        id = this.id,
        doctorName = this.doctorName,
        specialty = this.specialty,
        time = this.time,
        address = this.address,
        clinic = this.clinic,
        photoRes = this.photoRes,
        isFavorite = this.isFavorite,
        isConfirmed = if (this.isCancelled) false else this.isConfirmed
    )
}

fun RecordUI.toEntity(userEmail: String): RecordEntity {
    return RecordEntity(
        id = this.id,
        userEmail = userEmail,
        doctorName = this.doctorName,
        specialty = this.specialty,
        time = this.time,
        address = this.address,
        clinic = this.clinic,
        photoRes = this.photoRes,
        isFavorite = this.isFavorite,
        isConfirmed = this.isConfirmed,
        isCancelled = false
    )
}
