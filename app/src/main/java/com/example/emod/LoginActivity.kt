package com.example.emod

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.emod.ui.signUp.SignupActivity
import com.example.emod.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 로그인 버튼 클릭 시 메인으로 이동
        binding.button.setOnClickListener {
            moveToSignUp()
        }
    }

    private fun moveToSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}