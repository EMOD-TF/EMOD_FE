package com.example.hackathon.Diary.summary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.Diary.DiaryActivity
import com.example.hackathon.Diary.ExpressionFragment
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.R
import com.example.hackathon.data.repository.ProfileRepository
import com.example.hackathon.databinding.FragmentSummarize4Binding
import com.example.hackathon.ui.signUp.SignupViewModel

class Summarize4Fragment : BaseFragment<FragmentSummarize4Binding>(FragmentSummarize4Binding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = context?.getSharedPreferences("my name", Context.MODE_PRIVATE)
        val userName = prefs?.getString("userName", null)
        binding.tvToday.text = "${userName}의 하루"

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            binding.keywordEmotion.text = summary.emotion.keyword

            Log.d("ChatFragment", "감정 : ${summary.emotion.keyword}")

            if (summary.emotion.keyword == "기쁨") {
                binding.icEmotion.setImageResource(R.drawable.ic_happy)
                binding.tvChat.text = "${userName}" + getString(R.string.happy_emotion)
            }
            else if (summary.emotion.keyword == "화남") {
                binding.icEmotion.setImageResource(R.drawable.ic_angry)
                binding.tvChat.text = "${userName}" + getString(R.string.angry_emotion)
            }
            else {
                binding.icEmotion.setImageResource(R.drawable.ic_sad)
                binding.tvChat.text = "${userName}" + getString(R.string.sad_emotion)
            }
        }

        // Expression Fragment로 이동
        binding.btnNext4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
        binding.btnSummarizeChat4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
    }



}