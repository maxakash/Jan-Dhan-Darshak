package com.whileloop.jandhandarshak.API


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("nearbysearch/json?")
    fun getPlaces(@Query("location") latlng: String, @Query("radius") radius: Int, @Query("type") nearbyPlace: String, @Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun getVoiceSearch(@Query("location") latlng: String, @Query("radius") radius: Int, @Query("keyword") nearbyPlace: String,@Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun filterByDistance( @Query("type") nearbyPlace: String,@Query("rankby") rankyby: String,@Query("location") latlng: String, @Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun filterByOpenNow( @Query("type") nearbyPlace: String,@Query("opennow") opennow: String,@Query("location") latlng: String, @Query("radius") radius: Int, @Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun filterByOpenNowKeyword( @Query("opennow") opennow: String,@Query("keyword") query: String,@Query("location") latlng: String, @Query("radius") radius: Int, @Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("nearbysearch/json?")
    fun filterByDistanceKeyword( @Query("keyword") query: String,@Query("rankby") rankyby: String,@Query("location") latlng: String, @Query("language") language: String, @Query("key") api: String): Call<String>

    @GET("json?")
    fun getDistance( @Query("origins") origin: String,@Query("destinations") destinations: String, @Query("key") api: String): Call<String>

}