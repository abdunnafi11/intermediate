package com.nafi.nafstory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.model.home.ListStory
import kotlinx.coroutines.Job

class HomeViewModel constructor (private val dataRepository: DataRepository): ViewModel() {


    var quote: LiveData<PagingData<ListStory>> = MutableLiveData()

    fun getStory(auth: String) {
        quote = dataRepository.getQuote(auth).cachedIn(viewModelScope)
    }
    private var job: Job? = null
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}