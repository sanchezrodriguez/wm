package com.boris.placeanorder.network


import com.boris.boriswmedia.Constants
import com.boris.boriswmedia.data.models.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchNewsSerice {

    @GET(Constants.BASE_URL)
    suspend fun fetchNews(
        @Query("apiKey") apikey: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("country") country: String,
        @Query("pageSize") perPage: Int,
        @Query("category") selectedCategory: String
    ): NewsResponse






}