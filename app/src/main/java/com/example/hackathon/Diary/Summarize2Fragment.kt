package com.example.hackathon.Diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentSummarize2Binding

class Summarize2Fragment : BaseFragment<FragmentSummarize2Binding>(FragmentSummarize2Binding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 프래그먼트로 이동
        binding.btnSummarizeChat2.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize3Fragment())
        }

        binding.btnNext2.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize3Fragment())
        }
    }

}