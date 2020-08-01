package com.whileloop.jandhandarshak.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.whileloop.jandhandarshak.R
import java.util.*

open class GetNearbyPlacesData  {

    var googlePlacesData: String? = null
    var map: GoogleMap? = null
    var url: String? = null
    var context: Context? = null
    var progressBar: ProgressBar? = null
    var close: ImageView? = null




    private fun ShowNearbyPlaces(nearbyPlacesList: List<HashMap<String?, String?>?>?) {
        for (i in nearbyPlacesList!!.indices) {
            Log.d("onPostExecute", "Entered into showing locations")
            val markerOptions = MarkerOptions()
            val googlePlace =
                nearbyPlacesList[i]
            val lat = googlePlace!!["lat"]!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()
            val placeName = googlePlace["place_name"]
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title(placeName)
            markerOptions.snippet(googlePlace["vicinity"])
            println(BitmapDescriptorFactory.fromResource(R.drawable.atms))
            val height = 100
            val width = 100
            val bitmap =
                BitmapFactory.decodeResource(context!!.resources, R.drawable.marker)
            val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            map!!.addMarker(markerOptions)
        }
        try {
            println(nearbyPlacesList[0]!!["lng"])
            val latLng = LatLng(
                java.lang.Double.valueOf(nearbyPlacesList[0]!!["lat"]!!),
                java.lang.Double.valueOf(nearbyPlacesList[0]!!["lng"]!!)
            )
            map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            map!!.animateCamera(CameraUpdateFactory.zoomTo(17f))
            progressBar!!.visibility = View.GONE
            close!!.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}