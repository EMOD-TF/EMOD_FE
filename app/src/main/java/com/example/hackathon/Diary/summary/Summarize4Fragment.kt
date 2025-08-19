package com.example.hackathon.Diary.summary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.Diary.DiaryActivity
import com.example.hackathon.Diary.ExpressionFragment
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.databinding.FragmentSummarize4Binding

class Summarize4Fragment : BaseFragment<FragmentSummarize4Binding>(FragmentSummarize4Binding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            binding.keywordEmotion.text = summary.emotion.keyword

            binding.keywordEmotion.text = summary.emotion.sentence
        }

        // 전달받은 데이터 꺼내기
//        val place = arguments?.getString("keyword_place") ?: ""
//        val event = arguments?.getString("keyword_event") ?: ""
//        val topic = arguments?.getString("keyword_topic") ?: ""
//        val emotion = arguments?.getString("keyword_emotion") ?: ""

        // AppCompatButton 텍스트 변경
//        binding.keywordPlace.text = place
//        binding.keywordEvent.text = event
//        binding.keywordTopic.text = topic
//        binding.keywordEmotion.text = emotion

        // Expression Fragment로 이동
        binding.btnNext4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
        binding.btnSummarizeChat4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
    }

}