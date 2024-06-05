package com.nafi.nafstory.data.remote.local

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nafi.nafstory.data.remote.model.home.ListStory
import com.nafi.nafstory.data.remote.model.home.ResponseHome
import com.nafi.nafstory.data.remote.network.ApiService

class QuotePagingSource(private val apiService: ApiService,private val auth:String) : PagingSource<Int, ListStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer $auth",page, params.loadSize)

            Log.d("TAG", "load: $responseData")
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            Log.d("TAG", "load: ${exception.message}")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}