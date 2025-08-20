package com.example.hackathon.Diary.summary

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.Diary.DiaryActivity
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.data.repository.ProfileRepository
import com.example.hackathon.databinding.FragmentSummarize2Binding
import com.example.hackathon.ui.signUp.SignupViewModel

class Summarize2Fragment : BaseFragment<FragmentSummarize2Binding>(FragmentSummarize2Binding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = context?.getSharedPreferences("my name", Context.MODE_PRIVATE)
        val userName = prefs?.getString("userName", null)
        binding.tvToday.text = "${userName}의 하루"

        viewModel.summaryData.observe(viewLifecycleOwner){ summary ->
            binding.keywordPlace.text = summary.place.keyword
            binding.keywordEvent.text = summary.event.keyword
            binding.keywordTopic.text = summary.topic.keyword

            binding.tvChat.text = summary.event.sentence
        }

        // 다음 프래그먼트로 이동
        binding.btnSummarizeChat2.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize3Fragment())
        }

        binding.btnNext2.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize3Fragment())
        }
    }

}