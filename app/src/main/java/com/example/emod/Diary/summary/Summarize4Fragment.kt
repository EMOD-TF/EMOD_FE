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
import com.example.emod.data.LocalSummary
import com.example.emod.data.remote.dto.summary.SummaryResponse
import com.example.emod.databinding.FragmentSummarize4Binding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Summarize4Fragment : BaseFragment<FragmentSummarize4Binding>(FragmentSummarize4Binding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val gson by lazy { Gson() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("my name", Context.MODE_PRIVATE)
        val userName = prefs.getString("userName", null)
        binding.tvToday.text = "${userName}의 하루"

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            // UI 바인딩
            binding.keywordEmotion.text = summary.emotion.keyword

            // ✅ JSON으로 저장 + emotion 분리 저장
            saveSummaryToLocal(requireContext(), summary)

            // 감정별 아이콘/멘트
            when (summary.emotion.keyword) {
                "기쁨" -> {
                    binding.icEmotion.setImageResource(R.drawable.ic_happy)
                    binding.tvChat.text = "$userName" + getString(R.string.happy_emotion)
                }
                "화남" -> {
                    binding.icEmotion.setImageResource(R.drawable.ic_angry)
                    binding.tvChat.text = "$userName" + getString(R.string.angry_emotion)
                }
                else -> {
                    binding.icEmotion.setImageResource(R.drawable.ic_sad)
                    binding.tvChat.text = "$userName" + getString(R.string.sad_emotion)
                }
            }
        }

        binding.btnNext4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
        binding.btnSummarizeChat4.setOnClickListener {
            (activity as DiaryActivity).setFragment(ExpressionFragment())
        }
    }

    // ✅ JSON 저장: 날짜 키로 LocalSummary를 저장 + emotion은 별도 키로도 저장
    private fun saveSummaryToLocal(context: Context, summary: SummaryResponse) {
        val prefs = context.getSharedPreferences("summarize_content", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val local = LocalSummary(
            emotionKeyword = summary.emotion.keyword,
            emotionSentence = summary.emotion.sentence,
            topic = summary.topic.sentence,
            event = summary.event.sentence,
            place = summary.place.sentence
        )

        val json = gson.toJson(local)

        // 날짜별 요약 JSON 저장
        editor.putString(currentDate, json)

        // ✅ emotion은 별도 접근할 수 있도록 분리 저장(요구사항)
        editor.putString("${currentDate}_emotion", summary.emotion.keyword)

        editor.apply()

        Log.d("Summarize4Fragment", "저장 완료: $currentDate -> $json")
    }

}