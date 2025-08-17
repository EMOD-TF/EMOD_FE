package com.example.hackathon.Diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentSummarize2Binding
import com.example.hackathon.databinding.FragmentSummarize4Binding

class Summarize4Fragment : BaseFragment<FragmentSummarize4Binding>(FragmentSummarize4Binding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Expression Fragment로 이동
        binding.btnNext4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
        binding.btnSummarizeChat4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
    }

}