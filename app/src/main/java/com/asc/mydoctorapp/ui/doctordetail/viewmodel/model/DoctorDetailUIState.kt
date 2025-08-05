package com.asc.mydoctorapp.ui.doctordetail.viewmodel.model

data class DoctorDetailUIState(
    val doctor: DoctorDetailUi = DoctorDetailUi(),
    val education: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val reviews: List<ReviewUi> = emptyList(),
    val clinicInfo: ClinicInfo = ClinicInfo(),
    val isLoadingDoctor: Boolean = false,
    val isLoadingRecords: Boolean = false,
    val activeRecordsCount: Int = 0,
    val isRecordLimitReached: Boolean = false
)

data class DoctorDetailUi(
    val id: String = "",
    val name: String = "",
    val rating: Float = 0f,
    val specialty: String = "",
    val qualification: String = "",
    val photoRes: Int? = null,
    val isFavorite: Boolean = false
)

data class ReviewUi(
    val id: String = "",
    val author: String = "",
    val rating: Float = 0f,
    val text: String = "",
    val avatarRes: Int? = null
)

data class ClinicInfo(
    val name: String = "",
    val address: String = "",
    val schedule: String = ""
)

sealed interface DoctorDetailEvent {
    data object OnBackClick : DoctorDetailEvent
    data object OnBookClick : DoctorDetailEvent
    data object OnSupportClick : DoctorDetailEvent
    data class OnReviewClick(val id: String) : DoctorDetailEvent
    data class LoadDoctor(val email: String, val clinicName: String) : DoctorDetailEvent
}

sealed interface DoctorDetailAction {
    data object NavigateBack : DoctorDetailAction
    data class NavigateToBooking(val doctorEmail: String) : DoctorDetailAction
    data object NavigateToSupport : DoctorDetailAction
    data class NavigateToReviewDetail(val reviewId: String) : DoctorDetailAction
}
