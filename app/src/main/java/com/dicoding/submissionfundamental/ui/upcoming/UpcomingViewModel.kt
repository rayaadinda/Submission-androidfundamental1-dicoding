package com.dicoding.submissionfundamental.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.submissionfundamental.data.response.EventResponse
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel : ViewModel() {

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchActiveEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getActiveEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    if (eventResponse != null) {
                        _activeEvents.value = eventResponse.listEvents
                    }
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error: ${t.message}"
            }
        })
    }
}