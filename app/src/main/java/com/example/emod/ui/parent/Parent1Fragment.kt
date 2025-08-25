package com.example.hackathon.ui.parent

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.emod.BaseFragment
import com.example.emod.R
import com.example.emod.databinding.FragmentParent1Binding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class Parent1Fragment
    : BaseFragment<FragmentParent1Binding>(FragmentParent1Binding::inflate) {

    /** 한 주(일~토) 정보 */
    private data class WeekInfo(
        val label: String,          // e.g., "2025년 8월 3주차 (8/11~8/17)"
        val startSunday: LocalDate  // 해당 주의 '일요일'
    )

    /** 일자별(요일 셀) 표시/일기 데이터 */
    private data class DayInfo(
        val containerId: Int,
        val dateTextId: Int,
        val dayTextId: Int,
        val date: LocalDate,
        val hasDiary: Boolean,
        val titleRes: Int? = null,
        val contentRes: Int? = null,
        val guideRes: Int? = null,
        val emotionRes: Int? = null
    )

    private val dayCellIds = listOf(
        Triple(R.id.sundayLayout,    R.id.sundayDateText,    R.id.sundayDayText),
        Triple(R.id.mondayLayout,    R.id.mondayDateText,    R.id.mondayDayText),
        Triple(R.id.tuesdayLayout,   R.id.tuesdayDateText,   R.id.tuesdayDayText),
        Triple(R.id.wednesdayLayout, R.id.wednesdayDateText, R.id.wednesdayDayText),
        Triple(R.id.thursdayLayout,  R.id.thursdayDateText,  R.id.thursdayDayText),
        Triple(R.id.fridayLayout,    R.id.fridayDateText,    R.id.fridayDayText),
        Triple(R.id.saturdayLayout,  R.id.saturdayDateText,  R.id.saturdayDayText),
    )

    private val headerDateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREA)

    // 시작 기본 주차(현재 날짜 기준 주의 일요일로 설정)
    private var currentWeek: WeekInfo = run {
        val today = LocalDate.now()
        val startSun = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        WeekInfo(makeWeekLabelFrom(startSun), startSun)
    }
    private var currentDays: List<DayInfo> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryTitle   = binding.root.findViewById<TextView>(R.id.diaryTitleTextView)
        val diaryContent = binding.root.findViewById<TextView>(R.id.diaryContentTextView)
        val diaryDate    = binding.root.findViewById<TextView>(R.id.diaryDateTextView)
        val diaryGuide   = binding.root.findViewById<TextView>(R.id.diaryGuideTextView)
        val emotionImage = binding.root.findViewById<ImageView>(R.id.recentDiaryEmotionImageView)

        val emptyMessage = "아직 작성된 내용이 없어요."
        val yellow = ContextCompat.getColor(requireContext(), R.color.yellow)
        val defaultColor = ContextCompat.getColor(requireContext(), android.R.color.black)

        fun resetAllDayColors() {
            currentDays.forEach { d ->
                binding.root.findViewById<TextView>(d.dateTextId)?.setTextColor(defaultColor)
                binding.root.findViewById<TextView>(d.dayTextId )?.setTextColor(defaultColor)
            }
        }

        fun selectDayColors(day: DayInfo) {
            binding.root.findViewById<TextView>(day.dateTextId)?.setTextColor(yellow)
            binding.root.findViewById<TextView>(day.dayTextId )?.setTextColor(yellow)
        }

        fun applyDiary(day: DayInfo) {
            diaryDate.text = day.date.format(headerDateFormatter)
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

        fun renderCalendarStrip(days: List<DayInfo>) {
            days.forEach { d ->
                binding.root.findViewById<TextView>(d.dateTextId)?.text = d.date.dayOfMonth.toString()
            }
        }

        fun buildDaysForWeek(startSunday: LocalDate): List<DayInfo> {
            val dates = (0..6).map { startSunday.plusDays(it.toLong()) }

            // 👉 여기서 실제 데이터 매핑 규칙을 적용하세요.
            // 데모: 2025-08-24(일), 2025-08-25(월)만 컨텐츠 있음
            return dayCellIds.zip(dates).map { (ids, date) ->
                val (containerId, dateTextId, dayTextId) = ids
                when (date) {
                    LocalDate.of(2025, 8, 24) -> DayInfo(
                        containerId, dateTextId, dayTextId, date, true,
                        R.string.diary_title_placeholder_2,
                        R.string.diary_placeholder_2,
                        R.string.diary_guide_placeholder_2,
                        R.drawable.ic_lion_angry
                    )
                    LocalDate.of(2025, 8, 25) -> DayInfo(
                        containerId, dateTextId, dayTextId, date, true,
                        R.string.diary_title_placeholder_1,
                        R.string.diary_placeholder_1,
                        R.string.diary_guide_placeholder_1,
                        R.drawable.ic_lion_sad
                    )
                    else -> DayInfo(containerId, dateTextId, dayTextId, date, false)
                }
            }
        }

        fun bindWeek(week: WeekInfo, selectFirstWithDiary: Boolean = true) {
            binding.root.findViewById<TextView>(R.id.weekTitleTextView)?.text = week.label
            currentWeek = week
            currentDays = buildDaysForWeek(week.startSunday)
            renderCalendarStrip(currentDays)

            currentDays.forEach { day ->
                binding.root.findViewById<LinearLayout>(day.containerId)?.setOnClickListener {
                    resetAllDayColors()
                    selectDayColors(day)
                    applyDiary(day)
                }
            }

            resetAllDayColors()
            val initial = if (selectFirstWithDiary) {
                currentDays.firstOrNull { it.hasDiary } ?: currentDays.first()
            } else currentDays.first()
            selectDayColors(initial)
            applyDiary(initial)
        }

        // ▼▼ 주차 피커: 년/월 자유 선택 → 해당 월의 모든 "일요일 시작" 주 리스트 ▼▼
        fun showWeekPicker() {
            val dialog = BottomSheetDialog(requireContext())
            val container = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
            }

            // 상단: 연/월 NumberPicker
            val pickersRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            val yearPicker = NumberPicker(requireContext()).apply {
                // 필요한 범위로 자유롭게 조절
                minValue = 2000
                maxValue = 2100
                value = currentWeek.startSunday.year
            }
            val monthPicker = NumberPicker(requireContext()).apply {
                minValue = 1
                maxValue = 12
                value = currentWeek.startSunday.monthValue
            }

            val title = TextView(requireContext()).apply {
                text = "주차 선택"
                textSize = 18f
                setPadding(0, 0, 0, 16)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }

            val weeksList = ListView(requireContext())

            fun buildWeeksOf(year: Int, month: Int): List<WeekInfo> {
                val ym = YearMonth.of(year, month)
                val firstDay = ym.atDay(1)
                val lastDay = ym.atEndOfMonth()

                // 이 달에 걸치는 모든 '일요일'을 찾는다 (해당 달에 속하는 일요일들)
                var firstSunday = firstDay.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                val weeks = mutableListOf<WeekInfo>()
                var idx = 1
                while (!firstSunday.isAfter(lastDay)) {
                    val label = makeWeekLabel(year, month, idx, firstSunday)
                    weeks += WeekInfo(label, firstSunday)
                    firstSunday = firstSunday.plusWeeks(1)
                    idx++
                }

                // 빈 달(예: 일요일이 없는 달)은 거의 없지만, 안전장치: 해당 달을 포함하는 임의의 주 제공
                if (weeks.isEmpty()) {
                    val sunday = firstDay.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    weeks += WeekInfo(makeWeekLabelFrom(sunday), sunday)
                }
                return weeks
            }

            fun refreshWeekList() {
                val ys = yearPicker.value
                val ms = monthPicker.value
                val weeks = buildWeeksOf(ys, ms)
                val labels = weeks.map { it.label }
                weeksList.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
                weeksList.setOnItemClickListener { _, _, position, _ ->
                    bindWeek(weeks[position])
                    dialog.dismiss()
                }
            }

            yearPicker.setOnValueChangedListener { _, _, _ -> refreshWeekList() }
            monthPicker.setOnValueChangedListener { _, _, _ -> refreshWeekList() }

            pickersRow.addView(yearPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            pickersRow.addView(monthPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

            container.addView(title)
            container.addView(pickersRow)
            container.addView(View(requireContext()).apply {
                setBackgroundColor(0x22000000); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1).apply { topMargin = 16; bottomMargin = 16 }
            })
            container.addView(weeksList, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

            dialog.setContentView(container)
            dialog.show()

            // 초기 목록 구성
            refreshWeekList()
        }
        // ▲▲ 주차 피커 끝 ▲▲

        // 주차 헤더 클릭 시 바텀시트 띄우기
        val weekSelector = binding.root.findViewById<View>(R.id.weekSelectorLayout)
        weekSelector?.setOnClickListener { showWeekPicker() }

        // 초기 표시
        bindWeek(currentWeek)
    }

    /** 주차 라벨 생성 (예: "2025년 8월 3주차") */
    private fun makeWeekLabel(year: Int, month: Int, weekIdxInMonth: Int, weekStartSunday: LocalDate): String {
        val end = weekStartSunday.plusDays(6)
        return String.format(
            Locale.KOREA,
            "%d년 %d월 %d주차",
            year, month, weekIdxInMonth,
            weekStartSunday.monthValue, weekStartSunday.dayOfMonth,
            end.monthValue, end.dayOfMonth
        )
    }

    /** 주차 라벨 생성 (startSunday만 아는 경우: 해당 월 기준 주차 인덱스를 계산) */
    private fun makeWeekLabelFrom(startSunday: LocalDate): String {
        val ym = YearMonth.from(startSunday)
        // 같은 달의 첫 일요일
        val firstSundayOfMonth = ym.atDay(1).with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val weeksBetween = java.time.temporal.ChronoUnit.WEEKS.between(firstSundayOfMonth, startSunday).toInt()
        val weekIdx = weeksBetween + 1
        return makeWeekLabel(startSunday.year, startSunday.monthValue, weekIdx, startSunday)
    }
}
