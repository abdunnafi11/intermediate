package com.nafi.nafstory.utils

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data = data)
    class Error<T>(val errorMessage: String, data: T? = null) : NetworkResult<T>(data = data, message = errorMessage)
    class Loading<T> : NetworkResult<T>()
}
