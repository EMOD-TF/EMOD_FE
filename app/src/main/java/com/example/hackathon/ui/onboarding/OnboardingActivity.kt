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
import com.example.hackathon.HomeActivity
import com.example.hackathon.LoginActivity
import com.example.hackathon.MainActivity
import com.example.hackathon.core.DeviceId
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import com.example.hackathon.data.local.datastore.AuthDataStore
import com.example.hackathon.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val vm: OnboardingViewModel by viewModels()

    private var navigated = false // 중복 네비 방지 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val deviceId = DeviceId.get(this)
        val store = AuthDataStore(this)

        // 1) 3초 후 회원가입 트리거 (그 사이에 이미 토큰이 있으면 분기에서 막아줌)
        binding.root.postDelayed({ vm.signup(deviceId) }, 3000)

        // 2) DataStore Flow 로 분기
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // jwt가 존재하는 순간부터 분기 가능
                store.jwtFlow
                    .combine(store.profileCompletedFlow) { jwt, completed -> jwt to completed }
                    .distinctUntilChanged()
                    .filter { (jwt, _) -> !jwt.isNullOrBlank() } // 토큰 생기면만 진행
                    .collect { (_, completed) ->
                        if (!navigated) {
                            navigated = true
                            val next = if (!completed) HomeActivity::class.java else LoginActivity::class.java
                            startActivity(Intent(this@OnboardingActivity, next).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        }
                    }
            }
        }

        // 3) 로딩/에러는 기존 VM으로 처리 (분기는 DataStore가 담당)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { st ->
                    when (st) {
                        is OnboardingViewModel.UiState.Loading -> {
                            // 로딩 UI (스피너 등)
                        }
                        is OnboardingViewModel.UiState.Error -> {
                            Toast.makeText(
                                this@OnboardingActivity,
                                "로그인 실패: ${st.throwable.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> Unit // Success는 분기 안 함 (DataStore가 처리)
                    }
                }
            }
        }
    }
}
