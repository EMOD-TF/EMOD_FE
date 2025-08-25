package com.example.hackathon.ui.parent

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.emod.BaseFragment
import com.example.emod.databinding.FragmentParent2Binding
import com.example.emod.R
import com.example.widgets.WordCloudView

class Parent2Fragment
    : BaseFragment<FragmentParent2Binding>(FragmentParent2Binding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slot1 = binding.itemSlot1;
        val slot2 = binding.itemSlot2;
        val slot3 = binding.itemSlot3;

        slot1.tvEmotionLabel.text = "기쁨"
        slot1.tvEmotionCount.text = "5"
        slot1.ivEmotionIcon.setImageResource(R.drawable.ic_lion_happy)

        slot2.tvEmotionLabel.text = "슬픔"
        slot2.tvEmotionCount.text = "3"
        slot2.ivEmotionIcon.setImageResource(R.drawable.ic_lion_sad)

        slot3.tvEmotionLabel.text = "화남"
        slot3.tvEmotionCount.text = "2"
        slot3.ivEmotionIcon.setImageResource(R.drawable.ic_lion_angry)


        // 1) 스피너 세팅
        val months = (1..12).map { "${it}월" }
        val spinner = binding.spinnerMonth

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_month_spinner,  // 표시용 레이아웃
            months
        ).apply {
            setDropDownViewResource(R.layout.item_month_dropdown) // 드롭다운 아이템 레이아웃
        }
        spinner.adapter = adapter

        // 현재 월로 초기 선택
        val now = java.time.LocalDate.now()
        spinner.setSelection(now.monthValue - 1, false)

        // 선택 이벤트
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val month = position + 1 // 1~12
                // TODO: month에 맞춰 데이터 갱신 (감정 순위/카운트 API, 로컬 계산 등)
                // 예) viewModel.setMonth(month)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val words = listOf(
            WordCloudView.Word("버스", 10f),
            WordCloudView.Word("엄마", 7f),
            WordCloudView.Word("아빠", 5f),
            WordCloudView.Word("형아", 4f),
            WordCloudView.Word("선생님", 3f),
            WordCloudView.Word("고양이", 2f),
            WordCloudView.Word("자동차", 6f),
            WordCloudView.Word("집", 3.5f)
        )
        binding.wordCloud.post {
            binding.wordCloud.minSp = 20f
            binding.wordCloud.maxSp = 80f
            binding.wordCloud.submit(words)   // ← 이제 width/height가 확보된 뒤 실행
        }
    }
}
