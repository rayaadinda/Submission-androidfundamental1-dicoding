package com.dicoding.submissionfundamental.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import com.dicoding.submissionfundamental.data.response.EventResponse
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()

    fun fetchEvents() {
        _isLoading.value = true
        fetchActiveEvents()
        fetchFinishedEvents()
    }

    private fun fetchActiveEvents() {
        val client = ApiConfig.getApiService().getActiveEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    if (eventResponse != null) {
                        _upcomingEvents.value = eventResponse.listEvents
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

    private fun fetchFinishedEvents() {
        val client = ApiConfig.getApiService().getListEvenItems(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    if (eventResponse != null) {
                        _finishedEvents.value = eventResponse.listEvents
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