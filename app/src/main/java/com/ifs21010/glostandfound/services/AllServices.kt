package com.ifs21010.glostandfound.services

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class AllServices {
    fun getAuthToken(activity: AppCompatActivity): String {
        val sharedPref = activity.getSharedPreferences(
            "my_prefs_file",
            Context.MODE_PRIVATE
        )

        val myValue = sharedPref?.getString("auth_token", "")

        return "Bearer $myValue"
    }
}