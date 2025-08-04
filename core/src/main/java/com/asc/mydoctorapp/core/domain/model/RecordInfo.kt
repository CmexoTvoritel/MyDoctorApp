package com.asc.mydoctorapp.core.domain.model

data class RecordInfo (
    val start: String?,
    val end: String?,
    val docName: String?,
    val docSurname: String?,
    val docSpecialty: String?,
    val email: String?,
    val clinicName: String?,
    val isConfirmed: Boolean
)