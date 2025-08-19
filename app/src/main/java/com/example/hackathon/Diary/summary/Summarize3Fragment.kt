package com.example.hackathon.Diary.summary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.Diary.DiaryActivity
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.databinding.FragmentSummarize3Binding

class Summarize3Fragment : BaseFragment<FragmentSummarize3Binding>(FragmentSummarize3Binding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            binding.keywordPlace.text = summary.place.keyword
            binding.keywordEvent.text = summary.event.keyword
            binding.keywordTopic.text = summary.topic.keyword

            binding.tvChat.text = summary.topic.sentence
        }

//        // 전달받은 데이터 꺼내기
//        val place = arguments?.getString("keyword_place") ?: ""
//        val event = arguments?.getString("keyword_event") ?: ""
//        val topic = arguments?.getString("keyword_topic") ?: ""
//        val emotion = arguments?.getString("keyword_emotion") ?: ""
//
//        // AppCompatButton 텍스트 변경
//        binding.keywordPlace.text = place
//        binding.keywordEvent.text = event
//        binding.keywordTopic.text = topic

        // 다음 프래그먼트로 이동
        binding.btnSummarizeChat3.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize4Fragment())
//            arguments = Bundle().apply {
//                putString("place", place)
//                putString("event", event)
//                putString("topic", topic)
//                putString("emotion",emotion)
//            }
        }

        binding.btnNext3.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize4Fragment())
        }
    }

}