package com.asc.mydoctorapp.ui.doctordetail.viewmodel

import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.data.remote.WorkingDays
import com.asc.mydoctorapp.core.domain.usecase.GetDoctorByEmailUseCase
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.ClinicInfo
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailAction
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailEvent
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailUIState
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailUi
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.ReviewUi
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorDetailViewModel @Inject constructor(
    private val getDoctorByEmailUseCase: GetDoctorByEmailUseCase
) : BaseSharedViewModel<DoctorDetailUIState, DoctorDetailAction, DoctorDetailEvent>(
    initialState = DoctorDetailUIState(
        isLoading = true
    )
) {
    private val dayRu = mapOf(
        "Monday" to "пн",
        "Tuesday" to "вт",
        "Wednesday" to "ср",
        "Thursday" to "чт",
        "Friday" to "пт",
        "Suturday" to "сб",
        "Sunday" to "вс"
    )

    private fun WorkingDays.toRuString(): String {
        val parts = buildList {
            monday?.let { add("${dayRu["Monday"]}: $it") }
            tuesday?.let { add("${dayRu["Tuesday"]}: $it") }
            wednesday?.let { add("${dayRu["Wednesday"]}: $it") }
            thursday?.let { add("${dayRu["Thursday"]}: $it") }
            friday?.let { add("${dayRu["Friday"]}: $it") }
            saturday?.let { add("${dayRu["Suturday"]}: $it") }
            sunday?.let { add("${dayRu["Sunday"]}: $it") }
        }
        return parts.joinToString(separator = ", ")
    }

    override fun obtainEvent(viewEvent: DoctorDetailEvent) {
        when (viewEvent) {
            is DoctorDetailEvent.OnBackClick -> {
                sendViewAction(action = DoctorDetailAction.NavigateBack)
            }
            is DoctorDetailEvent.OnBookClick -> {
                sendViewAction(DoctorDetailAction.NavigateToBooking(
                    doctorEmail = viewStates().value?.doctor?.id ?: ""
                ))
            }
            is DoctorDetailEvent.OnSupportClick -> {
                sendViewAction(DoctorDetailAction.NavigateToSupport)
            }
            is DoctorDetailEvent.OnReviewClick -> {
                sendViewAction(DoctorDetailAction.NavigateToReviewDetail(viewEvent.id))
            }
            is DoctorDetailEvent.LoadDoctor -> {
                loadDoctorByEmail(viewEvent.email)
            }
        }
    }

    private fun loadDoctorByEmail(email: String) {
        updateViewState { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val doctor = getDoctorByEmailUseCase(email)
                
                // Конвертируем модель Doctor из домена в UI-модель
                val doctorDetailUi = DoctorDetailUi(
                    id = doctor.email,
                    name = "${doctor.name} ${doctor.surname}",
                    rating = 5.0f,  // Можно будет расширить модель Doctor, чтобы включать рейтинг
                    specialty = doctor.specialty,
                    qualification = "Врач высшей категории", // Это поле можно будет добавить в модель Doctor
                    photoRes = R.drawable.ic_doctor_placeholder,
                    isFavorite = false
                )
                
                // Примерные данные для демонстрации
                val education = listOf(
                    "Московский медицинский университет им. Сеченова, 2007-2013",
                    "Кандидат медицинских наук, 2016"
                )
                
                val tags = listOf(
                    "Кардиология", "ЭКГ", "УЗИ сердца", "Холтер"
                )
                
                val reviews = listOf(
                    ReviewUi(
                        id = "1",
                        author = "Елена В.",
                        rating = 5.0f,
                        text = "Отличный врач, внимательный и профессиональный",
                        avatarRes = R.drawable.ic_doctor_placeholder
                    ),
                    ReviewUi(
                        id = "2",
                        author = "Андрей К.",
                        rating = 5.0f,
                        text = "Очень доволен консультацией, всё объяснил и назначил эффективное лечение",
                        avatarRes = R.drawable.ic_doctor_placeholder
                    )
                )
                
                val clinicInfo = ClinicInfo(
                    name = doctor.clinic,
                    address = "ул. Лесная, 5, Москва",
                    schedule = doctor.workingDays?.toRuString() ?: ""
                )
                
                updateViewState { 
                    it.copy(
                        doctor = doctorDetailUi,
                        education = education,
                        tags = tags,
                        reviews = reviews,
                        clinicInfo = clinicInfo,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Обработка ошибки
                updateViewState { it.copy(isLoading = false) }
            }
        }
    }
}

// Тестовые данные удалены, так как теперь мы загружаем реальные данные
