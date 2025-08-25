package com.example.emod.Diary.summary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.emod.BaseFragment
import com.example.emod.Diary.DiaryActivity
import com.example.emod.Diary.ExpressionFragment
import com.example.emod.Diary.viewmodel.SummaryViewModel
import com.example.emod.R
import com.example.emod.data.remote.dto.summary.SummaryResponse
import com.example.emod.databinding.FragmentSummarize4Binding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

            saveSummaryToLocal(requireContext(), summary)

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

            val prefs = requireContext().getSharedPreferences("summarize_content", Context.MODE_PRIVATE)
            val editor = prefs.edit()

            // 오늘 날짜 (key)
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            // 저장할 내용 (value)
            val summarizeText = summary.emotion.sentence  // 예: place/topic/event 합친 문장

            // JSON 또는 그냥 문자열 저장
            editor.putString(today, summarizeText)
            editor.apply()

            Log.d("Summarize4Fragment", "저장 완료: $today -> $summarizeText")
        }


        // Expression Fragment로 이동
        binding.btnNext4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
        binding.btnSummarizeChat4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
    }


    // 로컬에 프로필 요약 내용 저장 함수
    private fun saveSummaryToLocal(context: Context, summary: SummaryResponse) {
        val prefs = context.getSharedPreferences("summarize_content", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 1. 현재 날짜 구하기
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 2. summary 데이터 하나의 문자열로 합치기
        val value = """
        sentence: ${summary.emotion.sentence}
        topic: ${summary.topic.sentence}
        event: ${summary.event.sentence}
        place: ${summary.place.sentence}
    """.trimIndent()

        // 3. 날짜를 key로 저장 (같은 날짜면 덮어쓰기 됨)
        editor.putString(currentDate, value)
        editor.apply()
    }




}