package com.whileloop.jandhandarshak.API


import com.whileloop.jandhandarshak.data.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("json?")
    fun getPlaces(@Query("location") latlng: List<Double>, @Query("radius") radius: Int, @Query("types") nearbyPlace: String, @Query("&key") api: String): Call<Model>
}