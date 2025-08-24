package com.example.hackathon.ui.parent

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.emod.R
import com.example.emod.databinding.ActivityParentBinding

class ParentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // 기본 탭: 일기(false)로 시작
        binding.segSwitch.isChecked = false

        if (savedInstanceState == null) {
            swapTab(binding.segSwitch.isChecked) // 최초 1회만 붙이기
        } else {
            // 복원 시 라벨 색만 현재 체크 상태로 동기화
            updateLabelColors(binding.segSwitch.isChecked)
        }

        binding.segSwitch.setOnCheckedChangeListener { _, isChecked ->
            swapTab(isChecked)
        }

        binding.btnExitParent.setOnClickListener {
            finish()
        }
    }

    private fun swapTab(checked: Boolean) {
        updateLabelColors(checked)
        val frag: Fragment = if (!checked) Parent1Fragment() else Parent2Fragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.parentFragmentContainerView, frag)
            .commit()
    }

    private fun updateLabelColors(checked: Boolean) {
        if (checked) {
            // 리포트 선택(엄지가 오른쪽)
            binding.labelLeft.setTextColor(0xFF7F7F7F.toInt())
            binding.labelRight.setTextColor(0xFFFFFFFF.toInt())
        } else {
            // 일기 선택(엄지가 왼쪽)
            binding.labelLeft.setTextColor(0xFFFFFFFF.toInt())
            binding.labelRight.setTextColor(0xFF7F7F7F.toInt())
        }
    }
}
