package com.dicoding.submissionfundamental.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionfundamental.data.EventRepository
import com.dicoding.submissionfundamental.data.local.AppDatabase
import com.dicoding.submissionfundamental.data.local.FavoriteEvent
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {
    val favorites: LiveData<List<FavoriteEvent>> = repository.getAllFavorites()

    fun removeFromFavorites(eventId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(eventId)
        }
    }
}
