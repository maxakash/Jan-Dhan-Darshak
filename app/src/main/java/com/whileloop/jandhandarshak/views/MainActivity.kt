package com.whileloop.jandhandarshak.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import com.google.android.gms.maps.model.*
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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var currentLocation: LatLng
    private val loading = Observer<Boolean> { isLoading ->

        if (!isLoading) {
            searchProgress.visibility = View.GONE
            searchResultClose.visibility = View.VISIBLE
        } else {
            searchProgress.visibility = View.VISIBLE
            searchResultClose.visibility = View.GONE
        }

    }
    private var markerLocation: LatLng? = null
    private var selectedMarker: Marker? = null


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            bottomNavigationView.menu.setGroupCheckable(0, true, true)
            selectedMarker = null
            when (item.itemId) {

                R.id.atms -> {
                    item.isChecked = true
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    markerType.text = "ATM"
                    searchResultText.text = "ATMs"
                    viewModel.getData(currentLocation, "atm", this, map)
                }
                R.id.branch -> {
                    item.isChecked = true
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    markerType.text = "Branch"
                    searchResultText.text = "Branch"
                    viewModel.getData(currentLocation, "bank", this, map)
                }


                R.id.postOffice -> {
                    item.isChecked = true
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    markerType.text = "Post Office"
                    searchResultText.text = "Post Office"
                    viewModel.getData(currentLocation, "post_office", this, map)

                }
                R.id.csc -> {
                    item.isChecked = true

                }
                R.id.bankMitra -> {
                    item.isChecked = true

                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.loading.observe(this, loading)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        swipeRefresh.isEnabled = false
        swipeRefresh.setProgressViewOffset(true, 300, 300)

        setDrawer()

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigationView.menu.setGroupCheckable(0, false, true)


        editTextQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                println("")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                voiceSearch.visibility = View.GONE
                searchText.visibility = View.VISIBLE
            }

        })


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

        map.setOnMarkerClickListener { m: Marker ->

            bottomNavigationView.visibility = View.GONE
            markerLocation = m.position
            markerDetails.visibility = View.VISIBLE
            markerName.text = m.title
            markerAdd.text = m.snippet
            if (m.tag == "true") {
                markerOpen.text = "Open Now"
                markerOpen.setTextColor(Color.GREEN)
            } else {
                markerOpen.text = "Closed Now"
                markerOpen.setTextColor(Color.RED)
            }
            println(m.tag)
            val height = 100
            val width = 100
            val selectedIcon =
                BitmapFactory.decodeResource(this.resources, R.drawable.selectedmarker)
            val unselectedIcon =
                BitmapFactory.decodeResource(this.resources, R.drawable.marker)
            val smallMarker = Bitmap.createScaledBitmap(selectedIcon, width, height, false)
            val smallMarker1 = Bitmap.createScaledBitmap(unselectedIcon, width, height, false)
            m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))

            selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker1))
            selectedMarker = m
            selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            true
        }

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
                bottomNavigationView.visibility = View.VISIBLE
                markerDetails.visibility = View.GONE
                fab.visibility = View.VISIBLE
                bottomNavigationView.menu.setGroupCheckable(0, false, true)

            }

            R.id.searchResultClose -> {
                searchResultbar.visibility = View.INVISIBLE
                searchBar.visibility = View.VISIBLE
                map.clear()
                map.isMyLocationEnabled = true
                bottomNavigationView.visibility = View.VISIBLE
                markerDetails.visibility = View.GONE
                fab.visibility = View.VISIBLE
                bottomNavigationView.menu.setGroupCheckable(0, false, true)
            }

            R.id.fab -> {
                map.isMyLocationEnabled = true
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }

            R.id.voiceSearch -> {
                markerType.text = ""
                startVoiceRecognitionActivity()
            }

            R.id.searchText -> {
                val searchQuery = editTextQuery.text.toString()
                if (searchQuery.isNotBlank()) {

                    editTextQuery.clearFocus()
                    editTextQuery.text.clear()
                    voiceSearch.visibility = View.VISIBLE
                    searchText.visibility = View.GONE
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    map.clear()
                    searchBar.visibility = View.GONE
                    searchResultbar.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    selectedMarker = null
                    viewModel.getVoiceData(currentLocation, searchQuery, this, map)
                    println(searchQuery)
                }
            }

            R.id.markerDirections -> {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=" + markerLocation?.latitude + "," + markerLocation?.longitude)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                }
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
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
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

    private fun startVoiceRecognitionActivity() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speech recognition demo"
        )
        startActivityForResult(intent, 1234)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                //  Toast.makeText(this, "Gps opened", Toast.LENGTH_SHORT).show()
                createLocationRequest()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "Please enable GPS",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("result cancelled", data.toString())
            }
        }
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            val matches =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            if (matches.isNotEmpty()) {
                searchResultText.text = matches[0]
                searchBar.visibility = View.GONE
                searchResultbar.visibility = View.VISIBLE
                fab.visibility = View.GONE
                map.clear()
                val searchText = matches[0]

                searchBar.visibility = View.GONE
                searchResultbar.visibility = View.VISIBLE
                fab.visibility = View.GONE
                selectedMarker = null
                viewModel.getVoiceData(currentLocation, searchText, this, map)

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