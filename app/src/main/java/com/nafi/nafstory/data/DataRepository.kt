package com.nafi.nafstory.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.nafi.nafstory.data.remote.local.QuotePagingSource
import com.nafi.nafstory.data.remote.model.home.ListStory
import com.nafi.nafstory.data.remote.model.home.ResponseHome
import com.nafi.nafstory.data.remote.model.login.ResponseLogin
import com.nafi.nafstory.data.remote.model.register.ResponseRegister
import com.nafi.nafstory.data.remote.model.upload.ResponseUploadStory
import com.nafi.nafstory.data.remote.network.ApiService
import com.nafi.nafstory.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class DataRepository(private val apiService: ApiService) {

    suspend fun getStories(auth: String): Flow<NetworkResult<ResponseHome>> = flow {
        try {
            val generateToken = generateAuthorization(auth)
            val response = apiService.getStories(generateToken)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            val ex = (e as? HttpException)?.response()?.errorBody()?.string()
            emit(NetworkResult.Error(ex.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadStory(auth: String, description: String, file: File): Flow<NetworkResult<ResponseUploadStory>> = flow {
        try {
            val generateToken = generateAuthorization(auth)
            val desc = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
            val response = apiService.uploadStory(generateToken, imageMultipart, desc)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            val ex = (e as? HttpException)?.response()?.errorBody()?.string()
            emit(NetworkResult.Error(ex.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun register(name: String, email: String, password: String): Flow<NetworkResult<ResponseRegister>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun login(email: String, password: String): Flow<NetworkResult<ResponseLogin>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getQuote(auth: String): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                QuotePagingSource(apiService, auth = auth)
            }
        ).liveData
    }

    private fun generateAuthorization(token: String): String {
        return "Bearer $token"
    }
}
