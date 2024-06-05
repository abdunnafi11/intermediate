package com.nafi.nafstory.viewmodel

import androidx.lifecycle.ViewModel
import com.nafi.nafstory.data.DataRepository

class LoginAndRegisterViewModel constructor(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = dataRepository.register(name, email, password)

    suspend fun login(email: String, password: String) = dataRepository.login(email, password)

}