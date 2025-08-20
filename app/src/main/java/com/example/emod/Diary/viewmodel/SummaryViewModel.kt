package com.example.emod.Diary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.emod.data.remote.dto.summary.SummaryResponse

class SummaryViewModel: ViewModel() {
    val summaryData = MutableLiveData<SummaryResponse>()
}