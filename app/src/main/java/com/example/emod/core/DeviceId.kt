package com.example.emod.core

import android.content.Context
import android.util.Log
import java.util.UUID

object DeviceId {
    private const val PREFS = "app_prefs"
    private const val KEY = "device_uuid"
    private const val TAG = "DeviceId"

    @Volatile private var cached: String? = null

    fun get(context: Context): String {
        cached?.let {
            Log.d(TAG, "✅ 캐시에서 불러옴: $it")
            return it
        }
        val appCtx = context.applicationContext
        val prefs = appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY, null)
        if (existing != null) {
            cached = existing
            Log.d(TAG, "📦 SharedPreferences에서 불러옴: $existing")
            return existing
        }
        synchronized(this) {
            cached?.let {
                Log.d(TAG, "✅ 동기화 블록 내 캐시에서 불러옴: $it")
                return it
            }
            val id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY, id).apply()
            cached = id
            Log.d(TAG, "✨ 새 UUID 생성 및 저장: $id")
            return id
        }
    }
}
