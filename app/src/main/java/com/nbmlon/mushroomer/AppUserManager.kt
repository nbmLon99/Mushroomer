package com.nbmlon.mushroomer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object AppUserManager {
    private const val PREF_NAME = "AppUserPrefs"

    // 저장 코드
    fun saveAppUser(context: Context, appUser: AppUser) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()

        val gson = Gson()
        val json = gson.toJson(appUser)

        editor.putString("appUser", json)
        editor.apply()
    }

    // 불러오기 코드
    fun loadAppUser(context: Context): AppUser? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json: String? = prefs.getString("appUser", null)

        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, AppUser::class.java)
        } else {
            null
        }
    }

    // 삭제 코드
    fun clearAppUser(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()

        editor.remove("appUser")
        editor.apply()
    }

    // 확인 코드
    fun isAppUserSaved(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.contains("appUser")
    }
}