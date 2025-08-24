package com.example.hackathon.ui.parent

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.emod.BaseFragment
import com.example.emod.R
import com.example.emod.databinding.FragmentParent1Binding


class Parent1Fragment
    : BaseFragment<FragmentParent1Binding>(FragmentParent1Binding::inflate) {

    private data class DayInfo(
        val containerId: Int,          // 요일 셀 레이아웃 id (e.g., R.id.sundayLayout)
        val dateTextId: Int,           // 숫자 날짜 TextView id (e.g., R.id.sundayDateText)
        val dayTextId: Int,            // 요일 TextView id (e.g., R.id.sundayDayText)
        val dateString: String,        // "2025년 8월 24일"
        val hasDiary: Boolean,         // 컨텐츠 존재 여부
        val titleRes: Int? = null,     // 일기 제목 string 리소스 (hasDiary=true일 때만)
        val contentRes: Int? = null,   // 일기 본문 string 리소스
        val guideRes: Int? = null,      // 가이드 string 리소스
        val emotionRes: Int? = null    // 감정 상태 string 리소스
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 일기 영역 뷰
        val diaryTitle  = binding.root.findViewById<TextView>(R.id.diaryTitleTextView)
        val diaryContent= binding.root.findViewById<TextView>(R.id.diaryContentTextView)
        val diaryDate   = binding.root.findViewById<TextView>(R.id.diaryDateTextView)
        val diaryGuide  = binding.root.findViewById<TextView>(R.id.diaryGuideTextView)
        val emotionImage = binding.root.findViewById<ImageView>(R.id.recentDiaryEmotionImageView)

        // 주간 날짜 정보 (※ 24일만 컨텐츠 있음, 25일 이후는 "내용 없음")
        val days = listOf(
            DayInfo(
                containerId = R.id.sundayLayout,
                dateTextId  = R.id.sundayDateText,
                dayTextId   = R.id.sundayDayText,
                dateString  = "2025년 8월 24일",
                hasDiary    = true,
                titleRes    = R.string.diary_title_placeholder_2,
                contentRes  = R.string.diary_placeholder_2,
                guideRes    = R.string.diary_guide_placeholder_2,
                emotionRes = R.drawable.ic_lion_angry
            ),
            DayInfo(
                containerId = R.id.mondayLayout,
                dateTextId  = R.id.mondayDateText,
                dayTextId   = R.id.mondayDayText,
                dateString  = "2025년 8월 25일",
                hasDiary    = true,
                titleRes    = R.string.diary_title_placeholder_1,
                contentRes  = R.string.diary_placeholder_1,
                guideRes    = R.string.diary_guide_placeholder_1,
                emotionRes = R.drawable.ic_lion_sad
            ),
            DayInfo(R.id.tuesdayLayout,   R.id.tuesdayDateText,   R.id.tuesdayDayText,   "2025년 8월 26일", false),
            DayInfo(R.id.wednesdayLayout, R.id.wednesdayDateText, R.id.wednesdayDayText, "2025년 8월 27일", false),
            DayInfo(R.id.thursdayLayout,  R.id.thursdayDateText,  R.id.thursdayDayText,  "2025년 8월 28일", false),
            DayInfo(R.id.fridayLayout,    R.id.fridayDateText,    R.id.fridayDayText,    "2025년 8월 29일", false),
            DayInfo(R.id.saturdayLayout,  R.id.saturdayDateText,  R.id.saturdayDayText,  "2025년 8월 30일", false),
        )

        val emptyMessage = "아직 작성된 내용이 없어요."
        val yellow = ContextCompat.getColor(requireContext(), R.color.yellow) // colors.xml에 <color name="yellow">#FFB108</color>
        val defaultColor = ContextCompat.getColor(requireContext(), android.R.color.black)

        fun resetAllDayColors() {
            // 모든 날짜/요일 텍스트를 기본색으로 초기화
            days.forEach { d ->
                binding.root.findViewById<TextView>(d.dateTextId)?.setTextColor(defaultColor)
                binding.root.findViewById<TextView>(d.dayTextId )?.setTextColor(defaultColor)
            }
        }

        fun selectDayColors(day: DayInfo) {
            // 선택된 날짜만 노란색
            binding.root.findViewById<TextView>(day.dateTextId)?.setTextColor(yellow)
            binding.root.findViewById<TextView>(day.dayTextId )?.setTextColor(yellow)
        }

        fun applyDiary(day: DayInfo) {
            diaryDate.text = day.dateString
            if (day.hasDiary) {
                diaryTitle.setText(day.titleRes!!)
                diaryContent.setText(day.contentRes!!)
                diaryGuide.setText(day.guideRes!!)
                emotionImage.setImageResource(day.emotionRes!!)
                emotionImage.alpha = 1.0f
            } else {
                diaryTitle.text = emptyMessage
                diaryContent.text = emptyMessage
                diaryGuide.text = emptyMessage
                emotionImage.setImageResource(R.drawable.ic_lion_happy)
                emotionImage.alpha = 0.5f
            }
        }

        // 클릭 리스너 일괄 등록
        days.forEach { day ->
            val container = binding.root.findViewById<LinearLayout>(day.containerId)
            container?.setOnClickListener {
                resetAllDayColors()
                selectDayColors(day)
                applyDiary(day)
            }
        }

        // 초기 상태: 필요 시 24일 선택으로 시작하고 싶다면 아래 주석 해제
        // resetAllDayColors()
        // selectDayColors(days.first())
        // applyDiary(days.first())
    }
}
