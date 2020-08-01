package com.whileloop.jandhandarshak.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var currentLocation: LatLng
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            bottomNavigationView.menu.setGroupCheckable(0,true,true)
            when (item.itemId) {
                R.id.atms -> {
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    markertype.text = "ATM"
                    map.clear()
                    val url: String =
                        viewModel.getUrl(currentLocation.latitude, currentLocation.longitude, "atm")
                    val dataTransfer = arrayOfNulls<Any>(5)
                    dataTransfer[0] = map
                    dataTransfer[1] = url
                    dataTransfer[2] = applicationContext
                    dataTransfer[3] = midprogress
                    dataTransfer[4] = searchResultClose
                   // val getNearbyPlacesData = GetNearbyPlacesData()
                }
                R.id.branch -> {
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    viewModel.getData(currentLocation, "atm")

                }


                R.id.postOffice -> {

                }
                R.id.csc -> {

                }
                R.id.bankMitra -> {

                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        swipeRefresh.isEnabled = false
        swipeRefresh.setProgressViewOffset(true,300,300)
        setDrawer()
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigationView.menu.setGroupCheckable(0,false,true)


    }


    private fun setDrawer() {
        val tf = Typeface.createFromAsset(this.assets, "monst.ttf")

        AccountHeaderView(this).apply {
            attachToSliderView(slider) // attach to the slider
            addProfiles(
                ProfileDrawerItem().apply {
                    nameText = "Jan Dhan Darshak"
                    iconRes = R.drawable.logo
                    typeface = tf
                    selectionListEnabledForSingleProfile = false
                }
            )

        }
        val item1 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.favouriteLocations
            typeface = tf
            iconRes = R.drawable.heart
        }
        val item2 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.missingBank
            typeface = tf
            iconRes = R.drawable.missing
        }
        val item3 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.feedback
            typeface = tf
            iconRes = R.drawable.feedback
        }

        val item4 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.help
            typeface = tf
            iconRes = R.drawable.help
        }
        val item5 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.aboutUs
            typeface = tf
            iconRes = R.drawable.aboutinfo
        }
        val item6 = PrimaryDrawerItem().apply {
            identifier = 1
            nameRes = R.string.disclaimer
            typeface = tf
            iconRes = R.drawable.about
        }

        slider.apply {
            itemAdapter.add(
                item1, item2, item3, item4, item5, item6

            )
            onDrawerItemClickListener = { v, drawerItem, position ->
                // listener action
                false

            }
        }
    }


    private fun locationListener() {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            @SuppressLint("MissingPermission")
            override fun onLocationChanged(location: Location) {

                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false
                }

                println("location changed with ${location.latitude}")

                currentLocation = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(14f)
                    .build()
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                map.isMyLocationEnabled = true

            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.mapType = 1
        map.uiSettings.isMyLocationButtonEnabled = false
        map.setMapStyle(
            MapStyleOptions(
                resources
                    .getString(R.string.style_json)
            )
        )
        locationListener()
        getLocation()

    }


    @SuppressLint("MissingPermission")
    fun onClick(view: View) {

        when (view.id) {
            R.id.menu -> {
                val drawer = slider.drawerLayout
                drawer?.open()
            }

            R.id.searchResultBack -> {
                searchResultbar.visibility = View.INVISIBLE
                searchBar.visibility = View.VISIBLE
                map.clear()
                map.isMyLocationEnabled = true
            }

            R.id.searchResultClose -> {

            }

        }

    }

    private fun createLocationRequest() {

        if (!setMyLastLocation()) {
            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 0
                priority = PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(this)
            val task =
                client.checkLocationSettings(builder.build())
            task.addOnSuccessListener(
                this
            ) { locationSettingsResponse ->
                Log.i("location settings", locationSettingsResponse.toString())
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    this.locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        100f,
                        locationListener
                    )
                }
            }
            task.addOnFailureListener(this) { e ->

                println("error getting location ${e.message}")

                if (e is ResolvableApiException) {
                    try {
                        e.startResolutionForResult(
                            this@MainActivity,
                            2
                        )
                    } catch (sendEx: SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            }
        }
    }



    private fun setMyLastLocation(): Boolean {

        var locationKnown = false

        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, object : OnSuccessListener<Location?> {
                override fun onSuccess(location: Location?) {
                    if (location != null) {
                        locationKnown = true
                        val latLng = LatLng(location.latitude, location.longitude)
                        println("last location exists")
                        if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            map.isMyLocationEnabled = true
                            return
                        }
                        currentLocation = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    }
                }
            })

        return locationKnown
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1927
            )
        } else {
            swipeRefresh.isRefreshing = true
            createLocationRequest()
        }
    }


    override fun onMarkerClick(p0: Marker?) = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
              //  Toast.makeText(this, "Gps opened", Toast.LENGTH_SHORT).show()
                createLocationRequest()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "refused to open gps",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("result cancelled", data.toString())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1927) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("permission granted")
                createLocationRequest()
            }
        }
    }


}