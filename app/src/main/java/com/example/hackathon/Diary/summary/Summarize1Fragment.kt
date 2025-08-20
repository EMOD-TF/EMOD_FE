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
import com.example.hackathon.databinding.FragmentSummarizeBinding
import com.example.hackathon.ui.signUp.SignupViewModel

class Summarize1Fragment : BaseFragment<FragmentSummarizeBinding>(FragmentSummarizeBinding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()
//    private val profileViewModel: SignupViewModel by viewModels {
//        SignupViewModelFactory(ProfileRepository(requireContext()))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            binding.keywordPlace.text = summary.place.keyword
            binding.keywordEvent.text = summary.event.keyword
            binding.keywordTopic.text = summary.topic.keyword

            binding.tvChat.text = summary.place.sentence
        }

        // name 지정
//        profileViewModel.nameFlow.observe(viewLifecycleOwner) { savedName ->
//            binding.tvToday.text = "${savedName}의 하루" ?: "길동이"
//        }
        val prefs = context?.getSharedPreferences("my name", Context.MODE_PRIVATE)
        val userName = prefs?.getString("userName", null)
        binding.tvToday.text = "${userName}의 하루"

        // 다음 프래그먼트로 이동
        binding.btnSummarizeChat1.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize2Fragment())
        }

        binding.btnNext1.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize2Fragment())
        }
    }

}