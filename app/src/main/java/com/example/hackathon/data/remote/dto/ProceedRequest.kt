package com.example.hackathon.data.remote.dto

data class ProceedRequest(
    val conversation: List<String>,
    val currentStep: Int
)