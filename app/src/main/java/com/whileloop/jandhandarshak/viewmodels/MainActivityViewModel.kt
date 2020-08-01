package com.whileloop.jandhandarshak.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.whileloop.jandhandarshak.API.APIService
import com.whileloop.jandhandarshak.API.ServiceBuilder
import com.whileloop.jandhandarshak.data.Model
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    fun getUrl(
        latitude: Double,
        longitude: Double,
        nearbyPlace: String
    ): String {
        val googlePlacesUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=" + 5000)
        // googlePlacesUrl.append("&rankBy=DISTANCE");
        googlePlacesUrl.append("&types=$nearbyPlace")
        googlePlacesUrl.append("&key=" + "AIzaSyA1FRQtoHeiMiSrXwAtaYHT7bwNIiN999k")
        Log.d("getUrl", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }

    fun getData(currentLocation: LatLng, type: String) {
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getPlaces(
            latlng = listOf(currentLocation.latitude, currentLocation.longitude),
            nearbyPlace = type,
            radius = 5000,
            api = "AIzaSyA1FRQtoHeiMiSrXwAtaYHT7bwNIiN999k"
        )

        call.enqueue(object : Callback<Model> {
            override fun onResponse(
                call: Call<Model>,
                response: Response<Model>
            ) {
                if (response.isSuccessful) {

                    println(response.body())

                }
            }

            override fun onFailure(call: Call<Model>, t: Throwable) {
                println(t.message)
            }

        })
    }

}