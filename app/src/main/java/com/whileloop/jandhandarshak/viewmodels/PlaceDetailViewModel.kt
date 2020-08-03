package com.whileloop.jandhandarshak.viewmodels

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.utils.infoToast

class PlaceDetailViewModel : ViewModel() {

    val placeDetail by lazy { MutableLiveData<Place>() }
    private val preferences = com.whileloop.jandhandarshak.utils.SharedPreferences()

    //this gets places details form google maps places api
    fun getPlaceDetails(placeId: String, placesClient: PlacesClient, placeImage: ImageView) {

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.OPENING_HOURS,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.TYPES,
            Place.Field.LAT_LNG
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                placeDetail.value = place

                getPhoto(place, placesClient, placeImage)

                println(place)

                println(place.latLng)
                println(place.phoneNumber)

            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    println("Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                    println(statusCode)
                }
            }
    }

    //this gets photo for a place
    private fun getPhoto(place: Place, placesClient: PlacesClient, placeImage: ImageView) {
        val metada = place.photoMetadatas
        if (metada == null || metada.isEmpty()) {
            println("no photo")

        } else {
            val photoMetadata = metada!!.first()

            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(400) // Optional.
                .setMaxHeight(250) // Optional.
                .build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    placeImage.setImageBitmap(bitmap)
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        println("Place not found: " + exception.message)

                    }
                }
        }
    }

    fun markFavourite(
        name: String,
        location: String,
        placeId: String,
        address: String,
        context: Context
    ) {
        val string = "$name,$location,$placeId,$address"
        val locations = preferences.getArrayPrefs("locations", context)
        locations.add(string)
        preferences.setArrayPrefs("locations", locations, context)

        context.infoToast(context.getString(R.string.markedFavoourite))

    }
}