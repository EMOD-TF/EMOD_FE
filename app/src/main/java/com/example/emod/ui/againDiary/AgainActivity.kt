package com.example.emod.ui.againDiary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListPopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emod.HomeActivity
import com.example.emod.R
import com.example.emod.databinding.ActivityAgainBinding
import com.example.emod.ui.againDiary.adapter.CustomSpinnerAdapter
import com.example.emod.ui.againDiary.adapter.DiaryDayAdapter
import com.example.emod.ui.againDiary.data.DiaryDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAgainBinding

    private lateinit var rvAdapter: DiaryDayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgainBinding.inflate(layoutInflater)

        binding.btnExitAgain.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        // 1. 캘린더 인스턴스 생성 및 특정 날짜 셋팅 (월-1 주의)= 7월 (0-based)
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1// 0-based, 예: 0=1월, 6=7월
        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH) - 1 // 0-based
        val weekTitles = getWeekTitles(currentYear, currentMonth)
//        weekTextView.text = weekTitles[currentWeek]  // 안전
        // 날짜 리스트 생성
        val diaryDays = mutableListOf<DiaryDay>()
        val prefs = this.getSharedPreferences("summarize_content", MODE_PRIVATE)
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfDay = SimpleDateFormat("E", Locale.KOREAN)

        for (i in 0 until 7) {
            val dateStr = sdfDate.format(calendar.time)
            val dayOfWeek = sdfDay.format(calendar.time)
            val summary = prefs.getString(dateStr, null)
            val emotion = getEmotionFromSummary(summary)
            diaryDays.add(DiaryDay(dateStr, dayOfWeek, summary, emotion))
            calendar.add(Calendar.DAY_OF_MONTH, 1)

        }
        val writtenDiaryCount = diaryDays.count { it.summary != null }
        binding.tvDayCount.text = "$writtenDiaryCount 일"



        rvAdapter = DiaryDayAdapter(diaryDays) { diaryDay ->
            Log.d("ChatFragment", "${diaryDay.emotion}")
            // 감정에 따른 이미지 리소스 결정
            val emotionRes = when {
                diaryDay.emotion?.contains("기뻤") == true -> R.drawable.ic_lion_happy
                diaryDay.emotion?.contains("슬픔") == true -> R.drawable.ic_lion_sad
                diaryDay.emotion?.contains("화남") == true -> R.drawable.ic_lion_angry
                else -> R.drawable.ic_lion_happy
            }

            // 감정에 따른 제목 결정
            val title = when {
                diaryDay.emotion?.contains("기뻤") == true -> "기쁜 마음을 느낀 날"
                diaryDay.emotion?.contains("슬픔") == true -> "슬픈 일이 있었던 날"
                diaryDay.emotion?.contains("화남") == true -> "조금은 화가 났던 날"
                else -> "최근 감정일기"
            }

            binding.llSelectDay.visibility = View.VISIBLE
            binding.icEmotion.setImageResource(emotionRes)
            binding.tvDiaryTitle.text = title       // 감정별 제목 반영
            binding.tvDiaryDate.text = diaryDay.date
            binding.tvDiaryContent.text = diaryDay.summary ?: "내용 없음"

        }



        binding.rvCalendar.adapter = rvAdapter
        binding.rvCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 1. 달(month) 변경 시, 해당 월 weekTitles 새로 생성
        val listPopupWindowButton = binding.dataSelector
        val listPopupWindow = ListPopupWindow(this, null, androidx.appcompat.R.attr.listPopupWindowStyle)

        // anchorView에 버튼 지정
        listPopupWindow.anchorView = listPopupWindowButton



        val autoCompleteWeek = binding.dataSelector

        // 어댑터 생성 (Material 스타일에 맞는 기본 레이아웃 사용)
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.list_popup_window_item,
            weekTitles
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text = view.findViewById<TextView>(R.id.spinner_item_text)
                text.text = getItem(position)
                // 선택된 아이템 스타일 커스텀 가능 (필요시)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val text = view.findViewById<TextView>(R.id.spinner_item_text)
                text.text = getItem(position)
                // 드롭다운 아이템 스타일 자유롭게 변경 가능
                return view
            }
        }

        autoCompleteWeek.setAdapter(adapter)

        // 기본 텍스트 지정 (첫 항목)
        autoCompleteWeek.setText(weekTitles.firstOrNull() ?: "", false)

        // 아이템 선택 리스너
        autoCompleteWeek.setOnItemClickListener { parent, view, position, id ->
            val selectedWeek = position + 1
            updateDiaryListForWeek(currentYear, currentMonth, selectedWeek)


        }


        // 현재 주차를 기본 선택값으로 세팅
        if (currentWeek in weekTitles.indices) {
            autoCompleteWeek.setText(weekTitles[currentWeek], false)
        }


        enableEdgeToEdge()
        setContentView(binding.root)
    }



    // 월의 주차(week of month) 리스트 구하기
    fun getWeekTitles(year: Int, month: Int): List<String> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // 0-based
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val weeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
        return (1..weeks).map { "${year}년 ${month}월 ${getKoreanOrder(it)}주" }
    }

    fun getKoreanOrder(order: Int): String {
        val orders = listOf("첫째", "둘째", "셋째", "넷째", "다섯째", "여섯째")
        return orders[order - 1]
    }

    // 특정 년/월/n번째 주차의 일~토 날짜와 정확한 요일 구하기
    fun getDatesAndDaysOfWeek(year: Int, month: Int, weekOfMonth: Int): List<Pair<String, String>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        // 주 시작일을 일요일에서 월요일로 변경
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        cal.add(Calendar.WEEK_OF_MONTH, weekOfMonth - 1)

        val result = mutableListOf<Pair<String, String>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("E", Locale.KOREAN)
        for (i in 0 until 7) {
            val dateString = dateFormat.format(cal.time)
            val dayOfWeek = dayFormat.format(cal.time)
            result.add(dateString to dayOfWeek)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }


    fun updateDiaryListForWeek(year: Int, month: Int, week: Int) {
        val prefs = this.getSharedPreferences("summarize_content", Context.MODE_PRIVATE)
        val items = getDatesAndDaysOfWeek(year, month, week).map { (dateStr, dayStr) ->
            val summary = prefs.getString(dateStr, null)
            val emotion = getEmotionFromSummary(summary)
            DiaryDay(date = dateStr, dayOfWeek = dayStr, summary = summary, emotion = emotion)
        }
        rvAdapter.updateItems(items)
    }

    fun getEmotionFromSummary(summary: String?): String? {
        if (summary == null) return null

        // summary 예시:
        // "emotion: 기쁨\nsentence: 어떤 내용\n..."
        val lines = summary.lines()
        for (line in lines) {
            if (line.startsWith("emotion:")) {
                return line.substringAfter("emotion:").trim()
            }
        }
        return null
    }



}