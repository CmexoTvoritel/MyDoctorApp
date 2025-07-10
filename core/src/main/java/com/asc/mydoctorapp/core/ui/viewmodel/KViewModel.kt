package com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class KViewModel: ViewModel() {

    private val mainCoroutineExceptionHandler: CoroutineExceptionHandler? by lazy(LazyThreadSafetyMode.NONE) {
        getCoroutineExceptionHandler()
    }
    private val coroutineTags = hashMapOf<String, CoroutineScope>()
    private val mainCoroutineContext = (SupervisorJob() + Dispatchers.Main.immediate).run {
        val exceptionHandler = mainCoroutineExceptionHandler ?: return@run this
        this + exceptionHandler
    }

    val viewModelScope: CoroutineScope
        get() = coroutineTags[MAIN_JOB_KEY] ?: launchNewScope()

    protected open fun getCoroutineExceptionHandler(): CoroutineExceptionHandler? = sharedExceptionHandler

    override fun onCleared() {

    }

    fun clear() {
        coroutineTags.forEach { it.value.cancel() }
        onCleared()
    }

    // Launch view model scope except you provide a new key
    fun launchNewScope(
        key: String = MAIN_JOB_KEY,
        coroutineContext: CoroutineContext = mainCoroutineContext
    ): CoroutineScope =
        coroutineTags.getOrPut(key) {
            CoroutineScope(coroutineContext)
        }

    companion object {
        private var sharedExceptionHandler: CoroutineExceptionHandler? = null
        private const val MAIN_JOB_KEY = "main.viewmodel.shared.coroutine.job"

        public fun setupSharedExceptionHandler(exceptionHandler: CoroutineExceptionHandler) {
            sharedExceptionHandler = exceptionHandler
        }
    }

}