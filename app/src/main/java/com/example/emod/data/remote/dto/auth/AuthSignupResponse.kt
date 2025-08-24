package com.example.emod.data.remote.dto.auth

data class AuthSignupResponse(
    val authId: Long,
    val deviceCode: String,
    val profileCompleted: Boolean,
    val jwt: String
)
