package com.whileloop.jandhandarshak.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.whileloop.jandhandarshak.API.APIService
import com.whileloop.jandhandarshak.API.ServiceBuilder
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.utils.infoToast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivityViewModel : ViewModel() {

    val loading by lazy { MutableLiveData<Boolean>() }
    val placesList by lazy { MutableLiveData<ArrayList<HashMap<String?, String?>?>>() }

    fun getData(
        currentLocation: LatLng,
        type: String,
        context: Context,
        map: GoogleMap,
        language: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getPlaces(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            nearbyPlace = type,
            radius = 8000,
            language = language,
            api = "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI"
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    fun getVoiceData(
        currentLocation: LatLng,
        query: String,
        context: Context,
        map: GoogleMap,
        language: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getVoiceSearch(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            nearbyPlace = query,
            radius = 8000,
            language = language,
            api = "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI"
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {

                    println(response.raw().request().url())

                    showNearbyPlaces(parseJSON(response.body()), context, map)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }

    fun getFilterByDistance(
        currentLocation: LatLng,
        type: String,
        context: Context,
        map: GoogleMap,
        language: String,
        rankBy: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.filterByDistance(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            nearbyPlace = type,
            language = language,
            rankyby = rankBy,
            api = "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI"
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }

    fun getFilterByOpenNow(
        currentLocation: LatLng,
        type: String,
        context: Context,
        map: GoogleMap,
        language: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.filterByOpenNow(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            nearbyPlace = type,
            language = language,
            opennow = "true",
            radius = 8000,
            api = "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI"
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map)
                }else{
                    context.infoToast("No results found.")
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    fun parseJSON(jsonData: String?): ArrayList<HashMap<String?, String?>?> {
        var jsonArray: JSONArray? = null
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(jsonData!!)
            jsonArray = jsonObject.getJSONArray("results")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return getPlaces(jsonArray)
    }

    private fun getPlaces(jsonArray: JSONArray?): ArrayList<HashMap<String?, String?>?> {
        val placesCount = jsonArray?.length()
        val placesList: ArrayList<HashMap<String?, String?>?> =
            ArrayList()
        var placeMap: HashMap<String?, String?>?

        for (i in 0 until placesCount!!) {
            try {
                placeMap = getPlace(jsonArray[i] as JSONObject)
                placesList.add(placeMap)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return placesList
    }

    private fun getPlace(googlePlaceJson: JSONObject): HashMap<String?, String?>? {

        val googlePlaceMap =
            HashMap<String?, String?>()
        var placeName: String? = "-NA-"
        var vicinity: String? = "-NA-"
        val latitude: String?
        val longitude: String?
        val reference: String?
        val isOpen: String?
        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }
            if (!googlePlaceJson.isNull("opening_hours")) {
                isOpen = googlePlaceJson.getJSONObject("opening_hours").getString("open_now")
                googlePlaceMap["isOpen"] = isOpen
            }else{
                googlePlaceMap["isOpen"] = "unknown"
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
                .getString("lat")
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
                .getString("lng")

            reference = googlePlaceJson.getString("place_id")
            googlePlaceMap["place_name"] = placeName
            googlePlaceMap["vicinity"] = vicinity
            googlePlaceMap["lat"] = latitude
            googlePlaceMap["lng"] = longitude
            googlePlaceMap["place_id"] = reference

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return googlePlaceMap
    }


    private fun showNearbyPlaces(
        nearbyPlacesList: ArrayList<HashMap<String?, String?>?>,
        context: Context,
        map: GoogleMap
    ) {

        map.clear()

        placesList.value = nearbyPlacesList
        for (i in nearbyPlacesList.indices) {
            val markerOptions = MarkerOptions()
            val googlePlace =
                nearbyPlacesList[i]
            val lat = googlePlace?.get("lat")!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()
            val placeName = googlePlace["place_name"]
            val latLng = LatLng(lat, lng)
            val placeId = googlePlace["place_id"]

            markerOptions.position(latLng)
            markerOptions.title(placeName)
            markerOptions.snippet(googlePlace["vicinity"])

            val height = 100
            val width = 100
            val bitmap =
                BitmapFactory.decodeResource(context.resources, R.drawable.marker1)
            val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            val marker = map.addMarker(markerOptions)


            if (!googlePlace["isOpen"].isNullOrEmpty()) {
                marker.tag = googlePlace["isOpen"] + " " + placeId
            }


        }
        try {
            println(nearbyPlacesList[0]?.get("lng"))
            val latLng = LatLng(
                java.lang.Double.valueOf(nearbyPlacesList[0]?.get("lat")!!),
                java.lang.Double.valueOf(nearbyPlacesList[0]?.get("lng")!!)
            )
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.zoomTo(11f))
            loading.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}