package com.asc.mydoctorapp.ui.reviews.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.reviews.model.ReviewUi
import com.asc.mydoctorapp.ui.reviews.model.ReviewsAction
import com.asc.mydoctorapp.ui.reviews.model.ReviewsEvent
import com.asc.mydoctorapp.ui.reviews.model.ReviewsUIState
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseSharedViewModel<ReviewsUIState, ReviewsAction, ReviewsEvent>(
    initialState = ReviewsUIState()
) {
    private val isMyReviews: Boolean = savedStateHandle["isMyReviews"] ?: false
    
    init {
        updateViewState { state ->
            state.copy(
                isMyReviews = isMyReviews,
                reviews = generateMockReviews(isMyReviews)
            )
        }
    }
    
    override fun obtainEvent(viewEvent: ReviewsEvent) {
        when (viewEvent) {
            is ReviewsEvent.OnBackClick -> {
                sendViewAction(ReviewsAction.NavigateBack)
            }
            is ReviewsEvent.OnReviewEdit -> {
                sendViewAction(ReviewsAction.NavigateToEditReview(viewEvent.id))
            }
            is ReviewsEvent.OnReviewDelete -> {
                // Здесь можно добавить подтверждение перед удалением
                // В реальном приложении тут был бы запрос в репозиторий для удаления отзыва
                deleteReview(viewEvent.id)
                sendViewAction(ReviewsAction.ShowDeleteConfirmation)
            }
        }
    }
    
    private fun deleteReview(id: Long) {
        // Удаление отзыва
        updateViewState { state ->
            state.copy(
                reviews = state.reviews.filter { it.id != id }
            )
        }
    }
    
    private fun generateMockReviews(isMyReviews: Boolean): List<ReviewUi> {
        val reviews = mutableListOf<ReviewUi>()
        
        // Общие отзывы для врача
        if (!isMyReviews) {
            for (i in 1..10) {
                reviews.add(
                    ReviewUi(
                        id = i.toLong(),
                        authorName = "Лариса",
                        rating = 5,
                        text = "Отличный врач!\nХодим всей семьей!",
                        avatarRes = null
                    )
                )
            }
        } else {
            // Мои отзывы
            reviews.add(
                ReviewUi(
                    id = 1,
                    authorName = "Лариса",
                    rating = 5,
                    text = "Отличный врач!\nХодим всей семьей!",
                    dateTime = LocalDateTime.of(2025, Month.APRIL, 10, 15, 50),
                    avatarRes = null
                )
            )
            
            reviews.add(
                ReviewUi(
                    id = 2,
                    authorName = "Лариса",
                    rating = 5,
                    text = "Отличный врач!\nХодим всей семьей!",
                    dateTime = LocalDateTime.of(2025, Month.APRIL, 10, 15, 50),
                    avatarRes = null
                )
            )
            
            reviews.add(
                ReviewUi(
                    id = 3,
                    authorName = "Лариса",
                    rating = 5,
                    text = "Отличный врач!\nХодим всей семьей!",
                    dateTime = LocalDateTime.of(2025, Month.APRIL, 10, 15, 50),
                    avatarRes = null
                )
            )
        }
        
        return reviews
    }
}
