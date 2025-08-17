package com.example.hackathon.ui.signUp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.databinding.FragmentSignup2Binding
import java.util.Calendar
import kotlin.getValue

class Signup2Fragment
    : BaseFragment<FragmentSignup2Binding>(FragmentSignup2Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()

    data class ChildInfo(
        val name: String,
        val year: Int,
        val month: Int, // 1..12
        val gender: Gender
    )
    enum class Gender { MAN, WOMAN }

    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1 // 1..12
    private var selectedGender: Gender = Gender.MAN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.doAfterTextChanged {
            vm.name = it?.toString()?.trim().orEmpty()
        }

        // ✅ 초기값 복원
        binding.etName.setText(vm.name.orEmpty())

        vm.birthYear?.let { selectedYear = it }
        vm.birthMonth?.let { selectedMonth = it }
        updateBirthLabel()

        when (vm.gender) {
            SignupViewModel.Gender.MALE -> {
                binding.rbMan.isChecked = true
                selectedGender = Gender.MAN
            }
            SignupViewModel.Gender.FEMALE -> {
                binding.rbWoman.isChecked = true
                selectedGender = Gender.WOMAN
            }
            null -> {
                // ✅ 성별을 안 건드려도 통과되도록 기본값(남) 주입 + UI 체크
                vm.gender = SignupViewModel.Gender.MALE
                binding.rbMan.isChecked = true
                selectedGender = Gender.MAN
            }
        }

        // ✅ 생년/월도 안 건드려도 통과되도록 기본값 주입
        if (vm.birthYear == null) vm.birthYear = selectedYear
        if (vm.birthMonth == null) vm.birthMonth = selectedMonth

        // 리스너
        binding.birthContainer.setOnClickListener { showYearMonthPicker() }
        binding.tvBirthValue.setOnClickListener { showYearMonthPicker() }

        binding.rgGender.setOnCheckedChangeListener { _, _ ->
            selectedGender = if (binding.rbMan.isChecked) Gender.MAN else Gender.WOMAN
            vm.gender = if (binding.rbMan.isChecked)
                SignupViewModel.Gender.MALE else SignupViewModel.Gender.FEMALE
        }
    }



    private fun updateBirthLabel() {
        binding.tvBirthValue.text = "${selectedYear}년 ${selectedMonth}월"
    }

    /** 연/월 선택 다이얼로그 */
    private fun showYearMonthPicker() {
        val ctx = requireContext()

        // ✅ inflate 사용하지 말고 직접 생성
        val container = android.widget.FrameLayout(ctx).apply {
            setPadding(dp(16), dp(8), dp(16), dp(8))
        }

        val yearPicker = NumberPicker(ctx).apply {
            val now = Calendar.getInstance().get(Calendar.YEAR)
            minValue = 2000   // 필요 시 조정
            maxValue = now
            value = selectedYear.coerceIn(minValue, maxValue)
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }

        val monthPicker = NumberPicker(ctx).apply {
            minValue = 1
            maxValue = 12
            value = selectedMonth.coerceIn(minValue, maxValue)
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }

        // 가로 배치
        val row = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            addView(yearPicker, android.widget.LinearLayout.LayoutParams(0, dp(120), 1f))
            addView(monthPicker, android.widget.LinearLayout.LayoutParams(0, dp(120), 1f))
        }
        container.addView(row)

        AlertDialog.Builder(ctx)
            .setTitle("생년월 선택")
            .setView(container)
            .setPositiveButton("확인") { d, _ ->
                selectedYear = yearPicker.value
                selectedMonth = monthPicker.value
                updateBirthLabel()
                vm.birthYear = selectedYear          // ✅ 저장
                vm.birthMonth = selectedMonth
                d.dismiss()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    /** 외부에서 호출: 값 검증 후 DTO 반환 */
    fun collectFormOrNull(): ChildInfo? {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return null
        }

        // ✅ 최종 결정값을 VM에 다시 싱크 (혹시 모를 누락 방지)
        vm.birthYear = selectedYear
        vm.birthMonth = selectedMonth
        vm.gender = if (selectedGender == Gender.MAN)
            SignupViewModel.Gender.MALE else SignupViewModel.Gender.FEMALE

        return ChildInfo(
            name = name,
            year = selectedYear,
            month = selectedMonth,
            gender = selectedGender
        )
    }

    private fun dp(v: Int): Int =
        (resources.displayMetrics.density * v).toInt()
}
