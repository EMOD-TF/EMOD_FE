package com.example.hackathon.SignUp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentSignup3Binding

class Signup3Fragment
    : BaseFragment<FragmentSignup3Binding>(FragmentSignup3Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()
    private val questions by lazy {
        resources.getStringArray(R.array.signup_step_3_questions)
    }

    // 각 질문에 대응하는 답변 배열 리소스 ID
    private val answerArrays = intArrayOf(
        R.array.signup_step_3_q1_answers,
        R.array.signup_step_3_q2_answers
    )

    // 현재 질문 인덱스
    private var qIndex = 0

    // 질문별 선택 인덱스 저장 (0..2)
    private val selections = mutableMapOf<Int, Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 보기 줄 전체 클릭으로도 선택되게
        binding.rowAnswer1.setOnClickListener { select(0) }
        binding.rowAnswer2.setOnClickListener { select(1) }
        binding.rowAnswer3.setOnClickListener { select(2) }
        binding.radioAnswer1.setOnClickListener { select(0) }
        binding.radioAnswer2.setOnClickListener { select(1) }
        binding.radioAnswer3.setOnClickListener { select(2) }

        // 좌/우 버튼
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
            } else {
                // 마지막 질문이면 완료 처리 (원하는 동작으로 교체)
                Toast.makeText(requireContext(), "체크리스트 완료!", Toast.LENGTH_SHORT).show()
                // TODO: selections 를 다음 화면/뷰모델로 전달
            }
        }

        render() // 첫 화면 그리기
    }

    /** 화면 갱신: 질문/보기 텍스트, 선택상태, 좌우 버튼 상태 */
    private fun render() {
        // 질문
        binding.step3Question.text = questions[qIndex]

        // 보기 텍스트
        val answers = resources.getStringArray(answerArrays[qIndex])
        binding.tvAnswer1.text = answers.getOrNull(0) ?: ""
        binding.tvAnswer2.text = answers.getOrNull(1) ?: ""
        binding.tvAnswer3.text = answers.getOrNull(2) ?: ""

        // 선택 복원
        val sel = selections[qIndex] ?: -1
        setChecked(sel)

        // 좌/우 버튼 상태
        binding.step3BtnLeft.isEnabled = qIndex > 0
        binding.step3BtnLeft.alpha = if (qIndex > 0) 1f else 0.3f

        // 마지막 질문이면 오른쪽 버튼에 완료 의미를 주고 싶다면 alpha/콘텐츠설명만
        binding.step3BtnRight.contentDescription =
            if (qIndex == questions.lastIndex) "완료" else "다음"
    }

    /** 특정 보기 선택 */
    private fun select(answerIndex: Int) {
        selections[qIndex] = answerIndex
        setChecked(answerIndex)
        // 즉시 ViewModel에도 반영(UX상 안정적)
        val answers = resources.getStringArray(answerArrays[qIndex])
        val picked = answers.getOrNull(answerIndex) ?: return
        if (qIndex == 0) vm.q1 = picked else if (qIndex == 1) vm.q2 = picked
    }

    /** 라디오 체크 상태 일괄 반영 */
    private fun setChecked(which: Int) {
        binding.radioAnswer1.isChecked = (which == 0)
        binding.radioAnswer2.isChecked = (which == 1)
        binding.radioAnswer3.isChecked = (which == 2)
    }

    /** 현재 질문의 선택 저장(미선택 시 안내) */
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
