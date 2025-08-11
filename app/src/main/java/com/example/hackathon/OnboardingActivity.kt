package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // '시작하기' 버튼에 클릭 리스너 설정
        binding.button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // login 화면으로 이동
            startActivity(intent)
            // 현재 액티비티 종료
            finish()
        }
    }
}