import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.moviemate.MediaItem
import com.example.moviemate.MediaRepository
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

sealed class UiState {
    object Loading : UiState()
    data class Success(val items: List<MediaItem>) : UiState()
    data class Error(val message: String) : UiState()
}

class HomeViewModel(private val repo: MediaRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    companion object {
        private const val TAG = "HomeViewModel"
    }


    fun load(isMovies: Boolean) {
        Log.d(TAG, "Loading ${if (isMovies) "movies" else "TV shows"}")
        _uiState.value = UiState.Loading
        val single = if (isMovies) repo.fetchMovies() else repo.fetchTvShows()

        single
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidScheduler.mainThread)
            .subscribeBy(
                onSuccess = { list ->
                    Log.d(TAG, "Successfully loaded ${list.size} items")
                    _uiState.value = UiState.Success(list)
                },
                onError = { err ->
                    Log.e(TAG, "Error loading data", err)
                    val errorMessage = when {
                        err.message?.contains("Unable to resolve host") == true ->
                            "No internet connection. Please check your network."
                        err.message?.contains("timeout") == true ->
                            "Request timed out. Please try again."
                        err.message?.contains("404") == true ->
                            "Service not found. Please try again later."
                        err.message?.contains("500") == true ->
                            "Server error. Please try again later."
                        else -> err.localizedMessage ?: "Unknown error occurred"
                    }
                    _uiState.value = UiState.Error(errorMessage)
                }
            )
            .addTo(disposables)
    }
    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}