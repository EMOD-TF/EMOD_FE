package com.example.hackathon.data.remote.dto

data class SummaryResponse(
    val place: Detail,
    val event: Detail,
    val topic: Detail,
    val emotion: Detail
)


data class Detail(
    val keyword: String,
    val sentence: String
)