package com.whileloop.jandhandarshak.API


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("nearbysearch/json?")
    fun getPlaces(@Query("location") latlng: String, @Query("radius") radius: Int, @Query("types") nearbyPlace: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun getVoiceSearch(@Query("location") latlng: String, @Query("radius") radius: Int, @Query("keyword") nearbyPlace: String, @Query("key") api: String): Call<String>
}