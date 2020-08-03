package com.whileloop.jandhandarshak.listadapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.whileloop.jandhandarshak.API.APIService
import com.whileloop.jandhandarshak.API.ServiceBuilder1
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.views.MainActivity
import com.whileloop.jandhandarshak.views.PlaceDetails
import kotlinx.android.synthetic.main.result_list_item.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ResultListAdapter(
    private val placesList: ArrayList<HashMap<String?, String?>?>,
    private val context: Context
) : RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {


    var currentLocation: LatLng? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateList(places: ArrayList<HashMap<String?, String?>?>, location: LatLng) {
        placesList.clear()
        placesList.addAll(places)
        currentLocation = location
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.result_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = placesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val googlePlace = placesList[position]
        val lat = googlePlace?.get("lat")!!.toDouble()
        val lng = googlePlace["lng"]!!.toDouble()
        val placeName = googlePlace["place_name"]
        val location = LatLng(lat, lng)
        val placeId = googlePlace["place_id"]
        val address = googlePlace["vicinity"]
       // println(googlePlace.toString())

        if (placeName != null)
            holder.itemView.result_name.text = placeName

        if (address != null)
            holder.itemView.result_address.text = address

        holder.itemView.resultItem.setOnClickListener {

            if (context is MainActivity) {
                context.zoomToMarker(location)
            }
        }
        holder.itemView.result_detail.setOnClickListener {
            val detailIntent = Intent(context, PlaceDetails::class.java)
            detailIntent.putExtra("placeId", placeId)
            context.startActivity(detailIntent)
        }

        holder.itemView.result_audio.setOnClickListener {
            currentLocation?.let { it1 ->
                if (placeName != null) {
                    getDistance(it1, location, placeName)
                }
            }
        }


    }

    private fun getDistance(
        origin: LatLng,
        destination: LatLng,
        placeName: String
    ) {
        val request = ServiceBuilder1.buildService(APIService::class.java)
        val call = request.getDistance(
            origin = "${origin.latitude},${origin.longitude}",
            destinations = "${destination.latitude},${destination.longitude}",
            api = "AIzaSyCUYN_YgHg6FaPWzLgF4Pcr-pBfIwzGcaI"
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
                            " $placeName  is  $distance away and will take around $duration to reach there"

                        if (context is MainActivity)
                            context.speakOut(message)


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
}