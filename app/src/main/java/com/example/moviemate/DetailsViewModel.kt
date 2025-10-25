package com.example.moviemate

import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AndroidScheduler {
    private val handler = Handler(Looper.getMainLooper())

    val mainThread: Scheduler by lazy {
        Schedulers.from { command ->
            if (Looper.myLooper() == Looper.getMainLooper()) {
                command.run()
            } else {
                handler.post(command)
            }
        }
    }
}

sealed class DetailsUiState {
    object Loading : DetailsUiState()
    data class Success(val item: MediaItem) : DetailsUiState()
    data class Error(val message: String) : DetailsUiState()
}

class DetailsViewModel(private val repo: MediaRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState

    companion object {
        private const val TAG = "DetailsViewModel"
    }

    fun loadDetails(imdbId: String) {
        _uiState.value = DetailsUiState.Loading

        repo.getDetails(imdbId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidScheduler.mainThread)
            .subscribeBy(
                onSuccess = { item ->
                    Log.d(TAG, "Successfully loaded details for: ${item.title}")
                    _uiState.value = DetailsUiState.Success(item)
                },
                onError = { err ->
                    Log.e(TAG, "Error loading details", err)
                    val errorMessage = when {
                        err.message?.contains("Unable to resolve host") == true -> 
                            "No internet connection. Please check your network."
                        err.message?.contains("timeout") == true -> 
                            "Request timed out. Please try again."
                        err.message?.contains("404") == true -> 
                            "Item not found. It may have been removed."
                        err.message?.contains("500") == true -> 
                            "Server error. Please try again later."
                        else -> err.localizedMessage ?: "Unknown error occurred"
                    }
                    _uiState.value = DetailsUiState.Error(errorMessage)
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}