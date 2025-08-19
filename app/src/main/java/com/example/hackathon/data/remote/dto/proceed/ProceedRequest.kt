package com.example.hackathon.data.remote.dto.proceed

data class ProceedRequest(
    val conversation: List<String>,
    val currentStep: Int
)