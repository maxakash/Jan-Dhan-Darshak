package com.whileloop.jandhandarshak.data

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DataParser {

    fun parse(jsonData: String?): List<HashMap<String?, String?>?>? {
        var jsonArray: JSONArray? = null
        val jsonObject: JSONObject
        try {
            println(jsonData.toString())
            Log.d("Places", "parse")
           // jsonObject = JSONObject(jsonData!!)
           // jsonArray = jsonObject.getJSONArray("results")
        } catch (e: JSONException) {
            Log.d("Places", "parse error")
            e.printStackTrace()
        }
        return getPlaces(jsonArray)
    }

    private fun getPlaces(jsonArray: JSONArray?): List<HashMap<String?, String?>?>? {
        val placesCount = jsonArray!!.length()
        val placesList: MutableList<HashMap<String?, String?>?> =
            ArrayList()
        var placeMap: HashMap<String?, String?>? = null
        Log.d("Places", "getPlaces")
        for (i in 0 until placesCount) {
            try {
                placeMap = getPlace(jsonArray[i] as JSONObject)
                placesList.add(placeMap)
                Log.d("Places", "Adding places")
            } catch (e: JSONException) {
                Log.d("Places", "Error in Adding places")
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
        var latitude: String? = ""
        var longitude: String? = ""
        var reference: String? = ""
        //String open_now ="";
        Log.d("getPlace", "Entered")
        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }
            latitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")
            longitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")
            reference = googlePlaceJson.getString("reference")
            // id =googlePlaceJson.getString("place_id");
            googlePlaceMap["place_name"] = placeName
            googlePlaceMap["vicinity"] = vicinity
            googlePlaceMap["lat"] = latitude
            googlePlaceMap["lng"] = longitude
            googlePlaceMap["reference"] = reference
            // Log.d("getPlace", "Putting Places");
        } catch (e: JSONException) {
            Log.d("getPlace", "Error")
            e.printStackTrace()
        }
        return googlePlaceMap
    }
}