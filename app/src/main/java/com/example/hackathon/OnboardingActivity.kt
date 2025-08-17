package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.core.DeviceId
import com.example.hackathon.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    private companion object {
        const val PREFS = "app_prefs"
        const val KEY_IS_SIGNED_UP = "is_signed_up"
        const val EXTRA_DEVICE_ID = "extra_device_id"
    }
    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val deviceId = DeviceId.get(this)

        // ✅ 상수 사용
        val isSignedUp = prefs.getBoolean(KEY_IS_SIGNED_UP, false)
        val dest = if (isSignedUp) MainActivity::class.java else LoginActivity::class.java

        // 3초 딜레이
        binding.root.postDelayed({
            val intent = Intent(this, dest).apply {
                putExtra(EXTRA_DEVICE_ID, deviceId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            // ✅ 온보딩 액티비티 정리 (최근앱에 안 남게)
            finish()
        }, 3000)
    }
}
