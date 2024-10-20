// FavoriteEventDao.kt
package com.dicoding.submissionfundamental.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun delete(eventId: Int)

    @Query("SELECT * FROM favorite_events WHERE id = :eventId")
    fun getFavoriteById(eventId: Int): LiveData<FavoriteEvent?>

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>
}