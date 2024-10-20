package com.dicoding.submissionfundamental.data

import androidx.lifecycle.LiveData
import com.dicoding.submissionfundamental.data.local.FavoriteEvent
import com.dicoding.submissionfundamental.data.local.FavoriteEventDao
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import com.dicoding.submissionfundamental.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {
    fun getAllFavorites(): LiveData<List<FavoriteEvent>> = favoriteEventDao.getAllFavorites()

    suspend fun addToFavorites(event: ListEventsItem) {
        val favoriteEvent = FavoriteEvent(
            id = event.id,
            name = event.name,
            imageLogo = event.imageLogo,
            beginTime = event.beginTime
        )
        favoriteEventDao.insert(favoriteEvent)
    }

    suspend fun removeFromFavorites(eventId: Int) {
        favoriteEventDao.delete(eventId)
    }

    fun isFavorite(id: Int): LiveData<FavoriteEvent?> = favoriteEventDao.getFavoriteById(id)

    suspend fun getEventDetail(eventId: Int): ListEventsItem {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEventDetail(eventId).execute()
            if (response.isSuccessful) {
                val jsonObject = response.body()
                if (jsonObject != null && jsonObject.has("event")) {
                    val eventJson = jsonObject.getAsJsonObject("event")
                    Gson().fromJson(eventJson, ListEventsItem::class.java)
                } else {
                    throw Exception("Response body is null or doesn't contain 'event'")
                }
            } else {
                throw Exception("Error: ${response.message()}")
            }
        }
    }

    suspend fun getNearestActiveEvent(): ListEventsItem? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNearestActiveEvent(1, 1).execute()
                if (response.isSuccessful) {
                    response.body()?.listEvents?.firstOrNull()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}