package com.boris.boriswmedia.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.boris.boriswmedia.Constants
import com.boris.boriswmedia.data.models.News
import com.boris.placeanorder.network.FetchNewsSerice
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception


private const val NEWS_STARTING_PAGE_INDEX = 1

class NewsPagingSource(
    private val newsService: FetchNewsSerice,
    private val query: String,
    private val selectedCategory: String
) : PagingSource<Int, News>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX

        return try {
            val response = newsService.fetchNews(Constants.API_KEY,query,position,"us",params.loadSize,selectedCategory)
            val news = response.articles

            LoadResult.Page(
                data = news,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (news.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception){
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, News>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}