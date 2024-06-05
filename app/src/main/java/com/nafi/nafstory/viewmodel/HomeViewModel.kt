package com.nafi.nafstory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nafi.nafstory.data.DataRepository
import kotlinx.coroutines.Job

class HomeViewModel constructor (private val dataRepository: DataRepository): ViewModel() {


    fun getStory(auth: String) = dataRepository.getQuote(auth).cachedIn(viewModelScope)
    private var job: Job? = null
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}