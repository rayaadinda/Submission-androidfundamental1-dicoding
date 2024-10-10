package com.dicoding.submissionfundamental.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getEventDetail(eventId: Int) {
        _isLoading.value = true
        _error.value = null
        val client = ApiConfig.getApiService().getEventDetail(eventId)
        client.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null && jsonObject.has("event")) {
                        val eventJson = jsonObject.getAsJsonObject("event")
                        val event = Gson().fromJson(eventJson, ListEventsItem::class.java)
                        Log.d("EventDetailViewModel", "Parsed event: $event")
                        _eventDetail.postValue(event)
                    } else {
                        Log.e("EventDetailViewModel", "Response body is null or doesn't contain 'event'")
                        _error.postValue("Failed to load event details")
                    }
                } else {
                    Log.e("EventDetailViewModel", "Response not successful. Code: ${response.code()}")
                    _error.postValue("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _isLoading.value = false
                Log.e("EventDetailViewModel", "Call failed", t)
                _error.postValue("Network error: ${t.message}")
            }
        })
    }
}