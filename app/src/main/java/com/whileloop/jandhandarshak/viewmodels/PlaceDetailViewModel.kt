package com.whileloop.jandhandarshak.viewmodels

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*

class PlaceDetailViewModel : ViewModel() {

    val placeDetail by lazy { MutableLiveData<Place>() }


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

                getPhoto(place,placesClient,placeImage)

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

    fun getPhoto(place: Place, placesClient: PlacesClient,placeImage:ImageView) {
        val metada = place.photoMetadatas
        if (metada == null || metada.isEmpty()) {
            println("no photo")

        }else {
            val photoMetadata = metada!!.first()

            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
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
}