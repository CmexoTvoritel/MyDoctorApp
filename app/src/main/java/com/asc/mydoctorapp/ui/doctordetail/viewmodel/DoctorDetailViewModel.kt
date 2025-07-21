package com.asc.mydoctorapp.ui.doctordetail.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.ClinicInfo
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailAction
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailEvent
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailUIState
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailUi
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.ReviewUi
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorDetailViewModel @Inject constructor(

) : BaseSharedViewModel<DoctorDetailUIState, DoctorDetailAction, DoctorDetailEvent>(
    initialState = DoctorDetailUIState(
        doctor = mockDoctor,
        education = mockEducation,
        tags = mockTags,
        reviews = mockReviews,
        clinicInfo = mockClinicInfo
    )
) {
    override fun obtainEvent(viewEvent: DoctorDetailEvent) {
        when (viewEvent) {
            is DoctorDetailEvent.OnBackClick -> {
                sendViewAction(action = DoctorDetailAction.NavigateBack)
            }
            is DoctorDetailEvent.OnBookClick -> {
                sendViewAction(DoctorDetailAction.NavigateToBooking(viewStates().value?.doctor?.id ?: ""))
            }
            is DoctorDetailEvent.OnSupportClick -> {
                sendViewAction(DoctorDetailAction.NavigateToSupport)
            }
            is DoctorDetailEvent.OnReviewClick -> {
                sendViewAction(DoctorDetailAction.NavigateToReviewDetail(viewEvent.id))
            }
        }
    }
}

// Тестовые данные
private val mockDoctor = DoctorDetailUi(
    id = "1",
    name = "Иван Сидоров",
    rating = 5.0f,
    specialty = "кардиолог",
    qualification = "Врач высшей категории",
    photoRes = R.drawable.ic_doctor_placeholder,
    isFavorite = true
)

private val mockEducation = listOf(
    "Ростовский государственный медицинский университет по специальности \"Лечебное дело\" 2015г.",
    "Ростовский государственный медицинский университет, ординатура по специальности \"Терапия\" 2017г.",
    "Ростовский государственный медицинский университет, профессиональная переподготовка по специальности \"Терапия\" 2018г."
)

private val mockTags = listOf(
    "Кардиолог",
    "Терапевт",
    "Взрослый",
    "Врач функциональной диагностики"
)

private val mockReviews = listOf(
    ReviewUi(
        id = "1",
        author = "Лариса",
        rating = 5.0f,
        text = "Отличный врач! Ходим всей семьей!",
        avatarRes = null
    ),
    ReviewUi(
        id = "2",
        author = "Александр",
        rating = 5.0f,
        text = "Очень внимательный специалист, подробно объясняет диагноз и лечение.",
        avatarRes = null
    ),
    ReviewUi(
        id = "3",
        author = "Елена",
        rating = 4.5f,
        text = "Профессионал своего дела, но немного задержался на приеме.",
        avatarRes = null
    )
)

private val mockClinicInfo = ClinicInfo(
    name = "Клиника \"Здоровье\"",
    address = "Вавилова, 15",
    schedule = "пн-пт 10:00-15:30"
)
