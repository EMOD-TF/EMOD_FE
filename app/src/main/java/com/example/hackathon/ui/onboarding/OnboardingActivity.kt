package com.example.hackathon.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hackathon.LoginActivity
import com.example.hackathon.MainActivity
import com.example.hackathon.core.DeviceId
import com.example.hackathon.databinding.ActivityOnboardingBinding
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val vm: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val deviceId = DeviceId.get(this) // 이미 있으니 그대로 사용

        // 3초 딜레이 후 자동으로 회원가입 진행
        binding.root.postDelayed({
            vm.signup(deviceId)
        }, 3000)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { st ->
                    when (st) {
                        is OnboardingViewModel.UiState.Loading -> {
                            // 로딩 UI
                        }
                        is OnboardingViewModel.UiState.Success -> {
                            val next = if (st.session.profileCompleted) {
                                MainActivity::class.java
                            } else {
                                LoginActivity::class.java
                            }
                            startActivity(Intent(this@OnboardingActivity, next).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        }
                        is OnboardingViewModel.UiState.Error -> {
                            Toast.makeText(this@OnboardingActivity, "로그인 실패: ${st.throwable.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
