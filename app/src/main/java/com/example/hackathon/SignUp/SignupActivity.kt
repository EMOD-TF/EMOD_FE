package com.example.hackathon.SignUp

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.hackathon.R
import com.example.hackathon.databinding.ActivitySignupBinding

class SignupActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var currentStep = 1

    private lateinit var stepIndicators: List<TextView>
    private val stepTexts by lazy {
        resources.getStringArray(R.array.signup_step)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)

        // 1. TextView 리스트 초기화
        stepIndicators = listOf(binding.step1, binding.step2, binding.step3, binding.step4)

        // 2. 처음 화면이 생성될 때 UI 업데이트
        updateUIForCurrentStep()

        // 3. 버튼 클릭 시 단계 변경 및 UI 업데이트
        binding.btnRight.setOnClickListener {
            if (currentStep < 4) {
                currentStep++
                updateUIForCurrentStep()
            }
        }

        binding.btnLeft.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateUIForCurrentStep()
            }
        }
    }

    /**
     * 프래그먼트를 교체하는 함수
     * @param fragment 교체할 프래그먼트
     */
    fun setFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.ft_signup, fragment, fragment::class.java.name)
        }
    }

    /**
     * 현재 단계(currentStep)에 맞게 모든 UI를 업데이트하는 통합 함수
     */
    private fun updateUIForCurrentStep() {
        // 배열 범위를 벗어나지 않도록 안전 체크
        if (currentStep in 1..stepTexts.size) {
            // 메인 텍스트 변경
            binding.stepText.text = stepTexts[currentStep - 1]

            // 상단 스텝 인디케이터 UI 변경
            updateStepIndicator(currentStep)

            // fragment 교체
            when (currentStep) {
                1 -> setFragment(Signup1Fragment())
                2 -> setFragment(Signup2Fragment())
                3 -> setFragment(Signup3Fragment())
                4 -> setFragment(Signup4Fragment())
            }
        }
    }

    /**
     * 현재 단계에 맞춰 상단 스텝 UI를 업데이트하는 함수
     * @param currentStep 현재 단계 (1, 2, 3, 4)
     */
    private fun updateStepIndicator(currentStep: Int) {
        stepIndicators.forEachIndexed { index, textView ->
            // forEachIndexed는 인덱스(0,1,2,3)와 항목(TextView)을 모두 제공합니다.
            // 현재 스텝은 1부터 시작하고, 인덱스는 0부터 시작하므로 1을 더해 비교합니다.
            if ((index + 1) == currentStep) {
                // 활성화 상태 UI 적용
                textView.setBackgroundResource(R.drawable.bg_step_active)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33.33f)
            } else {
                // 비활성화 상태 UI 적용
                textView.setBackgroundResource(R.drawable.bg_step_inactive)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.48f)
            }
        }
    }
}