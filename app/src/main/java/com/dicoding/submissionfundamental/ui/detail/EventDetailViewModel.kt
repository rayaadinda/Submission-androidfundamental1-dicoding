package com.dicoding.submissionfundamental.ui.detail


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionfundamental.data.EventRepository
import com.dicoding.submissionfundamental.data.local.FavoriteEvent
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import kotlinx.coroutines.launch


class EventDetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getEventDetail(eventId: Int) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val event = repository.getEventDetail(eventId)
                _eventDetail.postValue(event)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _isLoading.value = false
            }
        }
    }




    fun addToFavorites(event: ListEventsItem) {
        viewModelScope.launch {
            repository.addToFavorites(event)
        }
    }

    fun removeFromFavorites(eventId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(eventId)
        }
    }



            fun isFavorite(id: Int): LiveData<FavoriteEvent?> = repository.isFavorite(id)
        }
