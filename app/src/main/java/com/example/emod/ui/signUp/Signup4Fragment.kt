package com.example.emod.ui.signUp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.emod.BaseFragment
import com.example.emod.databinding.FragmentSignup4Binding

class Signup4Fragment
    : BaseFragment<FragmentSignup4Binding>(FragmentSignup4Binding::inflate) {

    enum class Env { KINDERGARTEN, SCHOOL, BUILDING, HOME }

    private val vm: SignupViewModel by activityViewModels()
    // 현재 선택 하나만 보관(없어도 됨)
    private var selected: SignupViewModel.Env? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 초기 선택 복원
        clearAll()
        vm.learningPlace?.let {
            selected = it
            when (it) {
                SignupViewModel.Env.KINDERGARTEN -> binding.boxKindergarten.isSelected = true
                SignupViewModel.Env.SCHOOL       -> binding.boxSchool.isSelected = true
                SignupViewModel.Env.BUILDING     -> binding.boxBuilding.isSelected = true
                SignupViewModel.Env.HOME         -> binding.boxHome.isSelected = true
            }
        }

        // 클릭 리스너
        binding.boxKindergarten.setOnClickListener { onBoxClicked(SignupViewModel.Env.KINDERGARTEN, it) }
        binding.boxSchool.setOnClickListener       { onBoxClicked(SignupViewModel.Env.SCHOOL, it) }
        binding.boxBuilding.setOnClickListener     { onBoxClicked(SignupViewModel.Env.BUILDING, it) }
        binding.boxHome.setOnClickListener         { onBoxClicked(SignupViewModel.Env.HOME, it) }
    }


    private fun onBoxClicked(env: SignupViewModel.Env, view: View) {
        if (selected == env) return
        clearAll()
        view.isSelected = true
        selected = env
        vm.learningPlace = env            // ✅ 저장
    }

    private fun clearAll() {
        binding.boxKindergarten.isSelected = false
        binding.boxSchool.isSelected       = false
        binding.boxBuilding.isSelected     = false
        binding.boxHome.isSelected         = false
    }

    /** 다음 단계에서 읽어갈 때 사용 */
    fun getSelectedEnvironment(): SignupViewModel.Env? = selected
}
