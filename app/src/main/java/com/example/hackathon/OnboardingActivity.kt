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

        // 화면을 클릭하거나 5초 후에 로그인 화면으로 이동
        binding.root.postDelayed({
            moveToLogin()
        }, 5000)
    }

    fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}