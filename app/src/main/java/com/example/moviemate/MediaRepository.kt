package com.example.moviemate

import android.util.Log
import io.reactivex.rxjava3.core.Single

class MediaRepository(private val api: ApiService, private val apiKey: String = R.string.imdb_api_key.toString()) {

    companion object {
        private const val TAG = "MediaRepository"
    }


    fun fetchMovies(): Single<List<MediaItem>> =
        api.getPopularMovies(apiKey, "action")
            .doOnSubscribe {
                Log.d(TAG, "Starting to fetch movies...")
            }
            .doOnSuccess {

            }
            .doOnError { error ->
                Log.e(TAG, "Movies API call failed", error)

                if (error is retrofit2.HttpException) {
                    Log.e(TAG, "HTTP error code: ${error.code()}")

                }
            }
            .map { response ->
                if (response.response == "True" && response.search != null) {
                    Log.d(TAG, "Movies response: ${response.search.size} items")
                    response.search.forEach { item ->
                        Log.d(TAG, "Movie: ${item.title}")

                    }
                    response.search
                } else {
                    Log.e(TAG, "Movies API error: ${response.error}")
                    emptyList()
                }
            }

    fun fetchTvShows(): Single<List<MediaItem>> =
        api.getPopularTvShows(apiKey, "drama")
            .doOnSubscribe {
                Log.d(TAG, "Starting to fetch TV shows...")
            }
            .doOnSuccess {

            }
            .doOnError { error ->
                Log.e(TAG, "TV Shows API call failed", error)

                if (error is retrofit2.HttpException) {
                    Log.e(TAG, "HTTP error code: ${error.code()}")
                }
            }
            .map { response ->
                if (response.response == "True" && response.search != null) {
                    Log.d(TAG, "TV Shows response: ${response.search.size} items")
                    response.search.forEach { item ->
                        Log.d(TAG, "TV Show: ${item.title}")
                    }
                    response.search
                } else {
                    Log.e(TAG, "TV Shows API error: ${response.error}")
                    emptyList()
                }
            }

    fun getDetails(imdbId: String): Single<MediaItem> =
        api.getDetails(apiKey, imdbId)
            .doOnSubscribe {
                Log.d(TAG, "Getting details for: $imdbId")
            }
            .doOnSuccess {}
            .doOnError { error ->
                Log.e(TAG, "Failed to get details for: $imdbId", error)
            }


}
