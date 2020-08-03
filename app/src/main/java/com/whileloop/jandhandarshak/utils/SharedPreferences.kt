package com.whileloop.jandhandarshak.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE


class SharedPreferences {

    fun setPrefs(key: String, value: Int, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences("com.whileloop.jandhandarshak", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getPrefs(key: String, context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences("com.whileloop.jandhandarshak", MODE_PRIVATE)
        return sharedPreferences.getInt(key,0)
    }

    fun setArrayPrefs(
        arrayName: String,
        array: ArrayList<String?>,
        mContext: Context
    ) {
        val prefs = mContext.getSharedPreferences("com.whileloop.jandhandarshak", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(arrayName + "_size", array.size)
        for (i in 0 until array.size) editor.putString(arrayName + "_" + i, array[i])
        editor.apply()
    }

    fun getArrayPrefs(
        arrayName: String,
        mContext: Context
    ): ArrayList<String?> {
        val prefs = mContext.getSharedPreferences("com.whileloop.jandhandarshak", MODE_PRIVATE)
        val size = prefs.getInt(arrayName + "_size", 0)
        val array: ArrayList<String?> = ArrayList(size)
        for (i in 0 until size) array.add(prefs.getString(arrayName + "_" + i, null))
        return array
    }
}