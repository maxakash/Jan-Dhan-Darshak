package com.whileloop.jandhandarshak.listadapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.whileloop.jandhandarshak.R
import com.whileloop.jandhandarshak.views.MainActivity
import com.whileloop.jandhandarshak.views.PlaceDetails
import kotlinx.android.synthetic.main.result_list_item.view.*
import java.util.*

class ResultListAdapter(
    val placesList: ArrayList<HashMap<String?, String?>?>,
    val context: Context
) : RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateList(places: ArrayList<HashMap<String?, String?>?>) {
        placesList.clear()
        placesList.addAll(places)
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
        println(googlePlace.toString())

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


    }
}