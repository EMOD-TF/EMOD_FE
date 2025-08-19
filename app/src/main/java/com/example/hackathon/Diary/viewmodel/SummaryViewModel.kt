package com.example.hackathon.Diary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hackathon.data.remote.dto.summary.SummaryResponse

class SummaryViewModel: ViewModel() {
    val summaryData = MutableLiveData<SummaryResponse>()
}