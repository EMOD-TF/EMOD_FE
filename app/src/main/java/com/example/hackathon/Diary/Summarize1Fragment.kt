package com.example.hackathon.Diary

import android.os.Bundle
import android.view.View
import com.example.hackathon.BaseFragment
import com.example.hackathon.databinding.FragmentSummarizeBinding

class Summarize1Fragment : BaseFragment<FragmentSummarizeBinding>(FragmentSummarizeBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 프래그먼트로 이동
        binding.btnSummarizeChat1.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize2Fragment())
        }

        binding.btnNext1.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize2Fragment())
        }
    }

}