package com.example.emod.data.remote.dto.proceed

data class ProceedResponse(
    val isAnswerValid: Boolean,
    val nextStep: Int,
    val questionToAsk: String,
    val reason: String
)