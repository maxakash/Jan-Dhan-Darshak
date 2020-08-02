package com.whileloop.jandhandarshak.views

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.utils.infoToast
import com.whileloop.jandhandarshak.viewmodels.PlaceDetailViewModel
import kotlinx.android.synthetic.main.activity_place_details.*

class PlaceDetails : AppCompatActivity() {

    private lateinit var viewModel: PlaceDetailViewModel
    private lateinit var placeDetails: Place
    private lateinit var placeId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_details)

        placeId = intent.getStringExtra("placeId")
        println(placeId)

        Places.initialize(applicationContext, "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI")
        val placesClient = Places.createClient(this)

        viewModel = ViewModelProvider(this).get(PlaceDetailViewModel::class.java)

        viewModel.getPlaceDetails(placeId, placesClient, placePhoto)

        viewModel.placeDetail.observe(this, Observer { placeDetail ->

            placeDetails = placeDetail

            if (placeDetail.address != null)
                placeAddress.text = placeDetail.address

            if (placeDetail.name != null)
                placeName.text = placeDetail.name

            if (placeDetail.name != null)
                placeName.text = placeDetail.name

            if (placeDetail.types != null) {
                placeType.visibility = View.VISIBLE
                placeType.text = placeDetail.types!![0].toString()
            }

            if (placeDetail.openingHours != null) {
                val openingHours = placeDetail.openingHours!!
                val timings =
                    "${openingHours.weekdayText[0]}\n${openingHours.weekdayText[1]}\n" +
                            "${openingHours.weekdayText[2]}\n" +
                            "${openingHours.weekdayText[3]}\n" +
                            "${openingHours.weekdayText[4]}\n" +
                            "${openingHours.weekdayText[5]}\n" +
                            openingHours.weekdayText[6]
                placeTimings.text = timings
            } else {
                placeTimings.text = "Timings not known"
            }


        })
    }


    @SuppressLint("MissingPermission")
    fun onClick(view: View) {

        when (view.id) {

            R.id.placeDirections -> {

                val gmmIntentUri =
                    Uri.parse("google.navigation:q=" + placeDetails.latLng?.latitude + "," + placeDetails.latLng?.longitude)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                }
            }

            R.id.placeCall -> {

                if (placeDetails.phoneNumber != null) {
                    println("clicked here")
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + placeDetails.phoneNumber))
                    startActivity(intent)
                } else {
                    infoToast("Phone number not available")
                }
            }

            R.id.placeSave -> {
                   placeSave.setImageResource(R.drawable.place_mark_fav)
            }

            R.id.placeShare -> {
                val uri =
                    "https://www.google.com/maps/?q=" + placeDetails.latLng?.latitude + "," +  placeDetails.latLng?.longitude
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, uri)
                startActivity(Intent.createChooser(sharingIntent, "Share in..."))
            }
        }
    }


}