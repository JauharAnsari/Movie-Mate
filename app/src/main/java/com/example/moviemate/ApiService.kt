package com.example.moviemate

import com.google.gson.annotations.SerializedName
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query


data class MediaItem(
    @SerializedName("imdbID")
    val id: String,
    
    @SerializedName("Title")
    val title: String,
    
    @SerializedName("Plot")
    val overview: String?,
    
    @SerializedName("Poster")
    val poster: String?,
    
    @SerializedName("Year")
    val year: String?,
    
    @SerializedName("Released")
    val releaseDate: String?,
    
    @SerializedName("imdbRating")
    val rating: String?,
    
    @SerializedName("Rated")
    val rated: String?,
    
    @SerializedName("Runtime")
    val runtime: String?,
    
    @SerializedName("Genre")
    val genre: String?,
    
    @SerializedName("Director")
    val director: String?,
    
    @SerializedName("Actors")
    val actors: String?,
    
    @SerializedName("Language")
    val language: String?,
    
    @SerializedName("Country")
    val country: String?,
    
    @SerializedName("Awards")
    val awards: String?,
    
    @SerializedName("BoxOffice")
    val boxOffice: String?,
    
    @SerializedName("Type")
    val type: String?
) {

    val finalPosterUrl: String?
        get() = if (poster != null && poster != "N/A") poster else null
    

    val placeholderPosterUrl: String
        get() = "https://via.placeholder.com/300x450/6366F1/FFFFFF?text=${title.take(10).replace(" ", "+")}"
}


data class OMDBSearchResponse(
    @SerializedName("Search")
    val search: List<MediaItem>?,
    
    @SerializedName("totalResults")
    val totalResults: String?,
    
    @SerializedName("Response")
    val response: String,
    
    @SerializedName("Error")
    val error: String?
)

interface ApiService {

    @GET(".")
    fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchTerm: String,
        @Query("type") type: String = "movie",
        @Query("page") page: Int = 1
    ): Single<OMDBSearchResponse>


    @GET(".")
    fun searchTvShows(
        @Query("apikey") apiKey: String,
        @Query("s") searchTerm: String,
        @Query("type") type: String = "series",
        @Query("page") page: Int = 1
    ): Single<OMDBSearchResponse>


    @GET(".")
    fun getDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): Single<MediaItem>

    @GET(".")
    fun getPopularMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchTerm: String = "movie",
        @Query("type") type: String = "movie",
        @Query("page") page: Int = 1
    ): Single<OMDBSearchResponse>

    @GET(".")
    fun getPopularTvShows(
        @Query("apikey") apiKey: String,
        @Query("s") searchTerm: String = "series",
        @Query("type") type: String = "series",
        @Query("page") page: Int = 1
    ): Single<OMDBSearchResponse>
}
