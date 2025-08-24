package com.example.emod.ui.signUp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.emod.BaseFragment
import com.example.emod.R
import com.example.emod.databinding.FragmentSignup3Binding

class Signup3Fragment
    : BaseFragment<FragmentSignup3Binding>(FragmentSignup3Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()
    private val questions by lazy { resources.getStringArray(R.array.signup_step_3_questions) }

    private val answerArrays = intArrayOf(
        R.array.signup_step_3_q1_answers,
        R.array.signup_step_3_q2_answers
    )

    private var qIndex = 0
    private val selections = mutableMapOf<Int, Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ VM → selections 프리로드
        preloadSelectionsFromVM()
        qIndex = when {
            vm.q1.isNullOrBlank() -> 0
            vm.q2.isNullOrBlank() -> 1
            else -> 0 // 전부 응답했어도 0번째로 보여주기 (원하면 1로 바꿔도 됨)
        }

        // 보기 탭/라디오 리스너 (기존 그대로)
        binding.rowAnswer1.setOnClickListener { select(0) }
        binding.rowAnswer2.setOnClickListener { select(1) }
        binding.rowAnswer3.setOnClickListener { select(2) }
        binding.radioAnswer1.setOnClickListener { select(0) }
        binding.radioAnswer2.setOnClickListener { select(1) }
        binding.radioAnswer3.setOnClickListener { select(2) }

        binding.step3BtnLeft.setOnClickListener {
            if (qIndex > 0) {
                saveCurrentSelection()
                qIndex--
                render()
            }
        }
        binding.step3BtnRight.setOnClickListener {
            if (!saveCurrentSelection()) return@setOnClickListener
            if (qIndex < questions.lastIndex) {
                qIndex++
                render()
            }
        }

        render()
    }

    private fun preloadSelectionsFromVM() {
        indexOfAnswer(answerArrays[0], vm.q1)?.let { selections[0] = it }
        indexOfAnswer(answerArrays[1], vm.q2)?.let { selections[1] = it }
    }

    private fun indexOfAnswer(@androidx.annotation.ArrayRes resId: Int, value: String?): Int? {
        if (value.isNullOrEmpty()) return null
        val arr = resources.getStringArray(resId)
        val idx = arr.indexOf(value)
        return if (idx >= 0) idx else null
    }

    private fun render() {
        // 질문/보기
        binding.step3Question.text = questions[qIndex]
        val answers = resources.getStringArray(answerArrays[qIndex])
        binding.tvAnswer1.text = answers.getOrNull(0) ?: ""
        binding.tvAnswer2.text = answers.getOrNull(1) ?: ""
        binding.tvAnswer3.text = answers.getOrNull(2) ?: ""

        // 선택 복원
        val sel = selections[qIndex] ?: -1
        setChecked(sel)

        // 화살표/완료 버튼 가시성
        val isFirst = qIndex == 0
        val isLast  = qIndex == questions.lastIndex

        // 첫 문항에서는 왼쪽 화살표를 '보이지 않게' (자리 유지 위해 INVISIBLE)
        binding.step3BtnLeft.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE

        // 마지막 문항에서는 오른쪽 화살표 숨김
        binding.step3BtnRight.visibility = if (isLast) View.GONE else View.VISIBLE
    }

    private fun select(answerIndex: Int) {
        selections[qIndex] = answerIndex
        setChecked(answerIndex)
        val answers = resources.getStringArray(answerArrays[qIndex])
        val picked = answers.getOrNull(answerIndex) ?: return
        if (qIndex == 0) vm.q1 = picked else if (qIndex == 1) vm.q2 = picked
    }

    private fun setChecked(which: Int) {
        binding.radioAnswer1.isChecked = (which == 0)
        binding.radioAnswer2.isChecked = (which == 1)
        binding.radioAnswer3.isChecked = (which == 2)
    }

    private fun saveCurrentSelection(): Boolean {
        val chosen = when {
            binding.radioAnswer1.isChecked -> 0
            binding.radioAnswer2.isChecked -> 1
            binding.radioAnswer3.isChecked -> 2
            else -> -1
        }
        if (chosen == -1) {
            Toast.makeText(requireContext(), "하나를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        selections[qIndex] = chosen
        val answers = resources.getStringArray(answerArrays[qIndex])
        val picked = answers.getOrNull(chosen) ?: ""
        if (qIndex == 0) vm.q1 = picked else if (qIndex == 1) vm.q2 = picked
        return true
    }
}
