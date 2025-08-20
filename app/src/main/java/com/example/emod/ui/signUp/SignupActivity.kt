package com.example.emod.ui.signUp

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.commit
import com.example.emod.R
import com.example.emod.databinding.ActivitySignupBinding

class SignupActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var currentStep = 1

    private lateinit var stepIndicators: List<TextView>
    private val stepTexts by lazy {
        resources.getStringArray(R.array.signup_step)
    }

    private val vm: SignupViewModel by viewModels()


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
            if (!vm.isStepValid(currentStep)) {
                Toast.makeText(this, "필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentStep < 5) {
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
        if (currentStep == 5) {
            binding.stepText.visibility = View.GONE
            binding.stepIndicator.visibility = View.GONE
            setFragmentFullWidth(full = true)
        } else {
            binding.stepText.visibility = View.VISIBLE
            binding.stepIndicator.visibility = View.VISIBLE
            setFragmentFullWidth(full = false)
        }
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
        } else if (currentStep == 5) {
            // 프래그먼트 교체
            setFragment(Signup5Fragment())
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

    private fun setFragmentFullWidth(full: Boolean) {
        val root = binding.root as ConstraintLayout
        val set = ConstraintSet().apply { clone(root) }

        // 우선 기존 좌우 제약 해제
        set.clear(R.id.ft_signup, ConstraintSet.START)
        set.clear(R.id.ft_signup, ConstraintSet.END)

        if (full) {
            // 좌우를 부모에 직접 연결
            set.connect(
                R.id.ft_signup, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            set.connect(
                R.id.ft_signup, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            // 좌우 버튼 사이로 제한
            set.connect(
                R.id.ft_signup, ConstraintSet.START,
                R.id.btn_left, ConstraintSet.END
            )
            set.connect(
                R.id.ft_signup, ConstraintSet.END,
                R.id.btn_right, ConstraintSet.START
            )
        }
        set.applyTo(root)
    }
}