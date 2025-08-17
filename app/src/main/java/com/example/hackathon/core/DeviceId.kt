package com.example.hackathon.core


import android.content.Context
import java.util.UUID

object DeviceId {
    private const val PREFS = "app_prefs"
    private const val KEY = "device_uuid"

    @Volatile private var cached: String? = null

    fun get(context: Context): String {
        cached?.let { return it }
        val appCtx = context.applicationContext
        val prefs = appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY, null)
        if (existing != null) {
            cached = existing
            return existing
        }
        synchronized(this) {
            cached?.let { return it }
            val id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY, id).apply()
            cached = id
            return id
        }
    }
}