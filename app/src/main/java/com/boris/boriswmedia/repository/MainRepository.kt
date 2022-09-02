package com.boris.boriswmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.boris.boriswmedia.data.NewsPagingSource
import com.boris.placeanorder.network.FetchNewsSerice
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepository @Inject constructor(private val newsApi: FetchNewsSerice) {

    fun getSearchResults(query: String, selectedCategory: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource(newsApi, query,selectedCategory) }
        ).liveData
}