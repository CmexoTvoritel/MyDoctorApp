package com.asc.mydoctorapp.ui.home.viewmodel.model

data class HomeUIState(
    val query: String = "",
    val doctors: List<DoctorUi> = emptyList(),
    val favorites: Set<String> = emptySet(), // id докторов
    val isSearching: Boolean = false
)

data class DoctorUi(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Float,
    val photoRes: Int          // локальный drawable или URL
)

sealed interface HomeEvent {
    data class OnQueryChanged(val text: String) : HomeEvent          // ввод в поиске
    object OnSearchSubmit : HomeEvent                                // тап по иконке поиска
    object OnAiChatStartClick : HomeEvent                            // «Начать» в AI‑карточке
    object OnSeeAllSpecialistsClick : HomeEvent                      // стрелка «→»
    data class OnDoctorCardClick(val doctorId: String) : HomeEvent   // открытие профиля
    data class OnDoctorFavoriteToggle(
        val doctorId: String,
        val isFavorite: Boolean
    ) : HomeEvent
    object OnFaqClick : HomeEvent                                    // «Остались вопросы?»
}

sealed interface HomeAction {
    data class NavigateToChat(val route: String) : HomeAction
    data class NavigateToDoctorProfile(val doctorId: String) : HomeAction
    data class NavigateToSpecialistsList(val route: String) : HomeAction
    data class NavigateToFaq(val route: String) : HomeAction
}
