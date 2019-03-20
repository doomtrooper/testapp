package com.anandp.application.api

import com.anandp.application.model.NewsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    fun getNews(
        @Query("country") action: String,
        @Query("apiKey") format: String
    ): Observable<NewsResponse>
}