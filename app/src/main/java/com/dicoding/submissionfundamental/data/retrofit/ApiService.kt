package com.dicoding.submissionfundamental.data.retrofit

import com.google.gson.JsonObject
import com.dicoding.submissionfundamental.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getListEvenItems(@Query("active") status: Int): Call<EventResponse>

    @GET("events")
    fun getActiveEvents(@Query("active") active: Int): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetail(@Path("id") id: Int): Call<JsonObject>

    @GET("events")
    fun searchEvents(@Query("active") active: Int, @Query("q") query: String): Call<EventResponse>

    @GET("events")
    fun getNearestActiveEvent(@Query("active") active: Int, @Query("limit") limit: Int): Call<EventResponse>
}