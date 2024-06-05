package com.nafi.nafstory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.model.home.ResponseHome
import com.nafi.nafstory.utils.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsViewModel constructor(private val dataRepository: DataRepository): ViewModel() {

    private val listStory = MutableLiveData<NetworkResult<ResponseHome>>()
    private var job: Job? = null

    fun fetchListStory(auth: String) {
        job = viewModelScope.launch {
            dataRepository.getStories(auth).collectLatest {
                listStory.value = it
            }
        }
    }

    val responseListStory: LiveData<NetworkResult<ResponseHome>> = listStory


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}