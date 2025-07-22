package com.asc.mydoctorapp.ui.reviews.model

import androidx.annotation.DrawableRes
import java.time.LocalDateTime

data class ReviewsUIState(
    val isMyReviews: Boolean = false,
    val reviews: List<ReviewUi> = emptyList()
)

data class ReviewUi(
    val id: Long,
    val authorName: String,
    val rating: Int,
    val text: String,
    val dateTime: LocalDateTime? = null,
    @DrawableRes val avatarRes: Int? = null
)

sealed interface ReviewsEvent {
    data object OnBackClick : ReviewsEvent
    data class OnReviewEdit(val id: Long) : ReviewsEvent
    data class OnReviewDelete(val id: Long) : ReviewsEvent
}

sealed interface ReviewsAction {
    data object NavigateBack : ReviewsAction
    data class NavigateToEditReview(val id: Long) : ReviewsAction
    data object ShowDeleteConfirmation : ReviewsAction
}
