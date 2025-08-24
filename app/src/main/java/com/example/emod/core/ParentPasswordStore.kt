package com.example.hackathon.core

import android.content.Context
import java.security.MessageDigest

object ParentPasswordStore {
    private const val PREF = "parent_password_pref"
    private const val KEY_HASH = "pass_hash"
    private const val SALT = "emod.local.salt" // 간단한 솔트

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun hasPasscode(ctx: Context): Boolean =
        prefs(ctx).getString(KEY_HASH, null) != null

    fun setPasscode(ctx: Context, pin: String) {
        prefs(ctx).edit().putString(KEY_HASH, sha256(pin + SALT)).apply()
    }

    fun verify(ctx: Context, pin: String): Boolean {
        val saved = prefs(ctx).getString(KEY_HASH, null) ?: return false
        return saved == sha256(pin + SALT)
    }

    fun clear(ctx: Context) = prefs(ctx).edit().remove(KEY_HASH).apply()

    private fun sha256(s: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(s.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
