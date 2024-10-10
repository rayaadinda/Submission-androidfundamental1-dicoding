package com.dicoding.submissionfundamental.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.submissionfundamental.data.response.EventResponse
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchFinishedEvents() {
        _isLoading.value = true
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

    fun searchEvents(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(0, query)
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