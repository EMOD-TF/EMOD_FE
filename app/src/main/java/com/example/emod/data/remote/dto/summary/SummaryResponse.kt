package com.example.emod.data.remote.dto.summary

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