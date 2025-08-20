package com.example.emod.domain.model

data class UserSession(
    val authId: Long,
    val deviceCode: String,
    val jwt: String,
    val profileCompleted: Boolean
)
