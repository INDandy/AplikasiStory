package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.response.InvalidResponse
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.response.Story
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class UploadRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _detail = MutableLiveData<Story>()
    val detail: LiveData<Story> = _detail

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout(){
        userPreference.logout()
        instance = null
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultValue.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            val message = successResponse.message
            emit(ResultValue.Success(message))
        } catch (e: HttpException) {
            val errorMessage: String
            if (e.code() == 400) {
                errorMessage = "Email sudah ada"
                emit(ResultValue.Error(errorMessage))
            } else {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, InvalidResponse::class.java)
                errorMessage = errorBody.message.toString()
                emit(ResultValue.Error(errorMessage))
            }
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultValue.Loading)
        try {
            val successResponse = apiService.login(email, password)
            val data = successResponse.loginResult?.token
            emit(ResultValue.Success(data))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultValue.Error(errorResponse.message!!))
        }
    }

    fun getAllStories(token: String) {
        val client = apiService.getStories("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadImage(token: String, imageFile: File, description: String) = liveData {
        emit(ResultValue.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
        try {
            val successResponse = apiService.uploadImage("Bearer $token", multipartBody, requestBody)
            emit(ResultValue.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
            emit(ResultValue.Error(errorResponse.message))
        }
    }

    fun getStoryDetail(token: String, id: String) {
        val client = apiService.detailStory("Bearer $token", id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    _detail.value = response.body()?.story!!
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
        @Volatile
        private var instance: UploadRepository? = null
        fun getInstance(apiService: ApiService, pref: UserPreference) =
            instance ?: synchronized(this) {
                instance ?: UploadRepository(apiService, pref)
            }.also { instance = it }
    }
}