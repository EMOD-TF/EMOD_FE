package com.example.emod.data.remote.dto.profile

data class ProfileRequest(
    val name: String,
    val birthYear: Int,
    val birthMonth: Int,
    val gender: String,         // "MALE" | "FEMALE"
    val q1: String,
    val q2: String,
    val learningPlace: String   // "KINDERGARTEN" | "SCHOOL" | ...
)
