package com.whileloop.jandhandarshak.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.whileloop.jandhandarshak.API.APIService
import com.whileloop.jandhandarshak.API.ServiceBuilder
import com.whileloop.jandhandarshak.API.ServiceBuilder1
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.utils.infoToast
import com.whileloop.jandhandarshak.views.MainActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MainActivityViewModel : ViewModel() {

    val loading by lazy { MutableLiveData<Boolean>() }
    val audioMessage by lazy { MutableLiveData<String>() }
    private val preferences = com.whileloop.jandhandarshak.utils.SharedPreferences()
    val placesList by lazy { MutableLiveData<ArrayList<HashMap<String?, String?>?>>() }
    val apiKey = "YOUR_API_KEY_HERE"

    //this gets nearby locations on the basis of relevance
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
            radius = 10000,
            language = language,
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }

    //this gets nearby locations on voice search
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
            radius = 9000,
            language = language,
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {

                    println(response.raw().request().url())

                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    //this filters locations on the basis of location
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
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    //this filters text search results on the basis of location
    fun getFilterByDistanceKeyword(
        currentLocation: LatLng,
        query: String,
        context: Context,
        map: GoogleMap,
        language: String,
        rankBy: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.filterByDistanceKeyword(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            query = query,
            language = language,
            rankyby = rankBy,
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }

    //this filters results on the basis of working hours
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
            radius = 10000,
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                } else {
                    context.infoToast(context.getString(R.string.noresults))
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    //this filters text or voice search results on the basis of working hours
    fun getFilterByOpenNowKeyword(
        currentLocation: LatLng,
        context: Context,
        map: GoogleMap,
        language: String,
        query: String
    ) {
        loading.value = true
        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.filterByOpenNowKeyword(
            latlng = "${currentLocation.latitude},${currentLocation.longitude}",
            language = language,
            opennow = "true",
            radius = 10000,
            query = query,
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    showNearbyPlaces(parseJSON(response.body()), context, map, currentLocation)
                } else {
                    context.infoToast("No results found.")
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }

    //gets distance of a location from user location
    private fun getDistance(
        origin: LatLng,
        destination: LatLng,
        placeName: String
    ) {
        loading.value = true
        val request = ServiceBuilder1.buildService(APIService::class.java)
        val call = request.getDistance(
            origin = "${origin.latitude},${origin.longitude}",
            destinations = "${destination.latitude},${destination.longitude}",
            api = apiKey
        )
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                if (response != null) {
                    println(response.raw().request().url())
                    try {
                        val jsonObject = JSONObject(response.body()!!)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)

                        val distance = jsonObject.getJSONObject("distance")
                            .get("text").toString()

                        val duration = jsonObject.getJSONObject("duration")
                            .get("text").toString()

                        val message =
                            "The nearest location is $placeName which is  $distance and will take around $duration to reach there"

                        audioMessage.value = message
                        //   getEnglishVoice(message)


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                println(t?.message)
            }

        })

    }


    // this parse the JSON data from api into readable format
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

    //this gets places from parsed JSON data
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

    //this gets individual place details from places data
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
            if (!googlePlaceJson.isNull("opening_hours") && googlePlaceJson.isNull("opening_hours")
                    .toString().isNotEmpty()
            ) {
                isOpen = googlePlaceJson.getJSONObject("opening_hours").getString("open_now")
                googlePlaceMap["isOpen"] = isOpen
            } else {
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

    //this marks the search results as markers on the map
    private fun showNearbyPlaces(
        nearbyPlacesList: ArrayList<HashMap<String?, String?>?>,
        context: Context,
        map: GoogleMap,
        currentLocation: LatLng
    ) {


        try {
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
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker2)
                val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                val marker = map.addMarker(markerOptions)


                if (!googlePlace["isOpen"].isNullOrEmpty()) {
                    marker.tag = googlePlace["isOpen"] + " " + placeId
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }



        if (nearbyPlacesList.isNotEmpty()) {
            try {

                println(nearbyPlacesList[0]?.get("lng"))
                val latLng = LatLng(
                    java.lang.Double.valueOf(nearbyPlacesList[0]?.get("lat")!!),
                    java.lang.Double.valueOf(nearbyPlacesList[0]?.get("lng")!!)
                )
                val placeName = nearbyPlacesList[0]?.get("place_name").toString()
                getDistance(currentLocation, latLng, placeName)
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                map.animateCamera(CameraUpdateFactory.zoomTo(11f))
                loading.value = false

                if (context is MainActivity)
                    context.speakOut("")

            } catch (e: Exception) {
                loading.value = false
                context.infoToast("No results found.")
                e.printStackTrace()
            }
        } else {
            if (context is MainActivity)
                context.hideResultList()
            loading.value = false
            context.infoToast("No results found.")
        }
    }


    fun markFavourites(map: GoogleMap, context: Context) {

        map.clear()
        val list = preferences.getArrayPrefs("locations", context)

        if(list.isEmpty()){
            context.infoToast(context.getString(R.string.noFavoourite))
            return
        }

        var latitude:Double? = null
        var longitude:Double? = null


        try {

            for (item in list) {
                val latlng = item?.split(",")
                println(latlng)

                val markerOptions = MarkerOptions()
                val lat = latlng?.get(1)!!.toDouble()
                latitude = lat
                val lng = latlng[2].toDouble()
                longitude = lng
                val placeName = latlng[0]
                val latLng = LatLng(lat, lng)
                val placeId = latlng[3]
                markerOptions.position(latLng)
                markerOptions.title(placeName)
                markerOptions.snippet(latlng[4])

                val height = 100
                val width = 100
                val bitmap =
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker2)
                val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                val marker = map.addMarker(markerOptions)
                marker.tag = "Unknown $placeId"
            }

            if(latitude!=null && longitude!=null){
                val location = LatLng(latitude, longitude)
                println("going to $location")
                val cameraPosition = CameraPosition.Builder()
                    .target(location)
                    .zoom(11f)
                    .build()
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }


}

