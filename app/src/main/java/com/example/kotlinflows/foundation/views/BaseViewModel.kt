package com.example.kotlinflows.foundation.views

import androidx.lifecycle.*
import com.example.kotlinflows.foundation.model.ErrorResult
import com.example.kotlinflows.foundation.model.Result
import com.example.kotlinflows.foundation.model.SuccessResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Base class for all view-models.
 */

@Suppress("UNREACHABLE_CODE")
open class BaseViewModel : ViewModel() {

    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    protected val viewModelScope: CoroutineScope = CoroutineScope(coroutineContext)


    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    /**
     * Override this method in child classes if you want to listen for results
     * from other screens
     */
    open fun onResult(result: Any) {

    }

    /**
     * Override this method in child classes if you want to control go-back behaviour.
     * Return `true` if you want to abort closing this screen
     */
    open fun onBackPressed(): Boolean {
        clearViewModelScope()
        return false
    }

    /**
     * Launch task asynchronously, listen for its result and
     * automatically unsubscribe the listener in case of view-model destroying.
     */


    /**
     * Launch task asynchronously and map its result to the specified
     * [liveResult].
     * Task is cancelled automatically if view-model is going to be destroyed.
     */
    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                liveResult.postValue(ErrorResult(e))
            }
        }
    }

    fun <T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                stateFlow.value = ErrorResult(e)
            }
        }
    }

    fun <T> SavedStateHandle.getStateFlow(key: String, initialValue: T): MutableStateFlow<T> {
        val savedStateHandle = this
        val mutableStateFlow = MutableStateFlow(savedStateHandle[key] ?: initialValue)

        viewModelScope.launch {
            mutableStateFlow.collect {
                savedStateHandle[key] = it
            }

            viewModelScope.launch {
                savedStateHandle.getLiveData<T>(key).asFlow().collect {
                    mutableStateFlow.value = it
                }
            }
        }
        return mutableStateFlow
    }

    private fun clearViewModelScope() {
        viewModelScope.cancel()
    }
}