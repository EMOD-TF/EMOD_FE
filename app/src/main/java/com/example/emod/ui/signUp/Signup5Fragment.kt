// ui/signUp/Signup5Fragment.kt (전체)
package com.example.emod.ui.signUp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.emod.BaseFragment
import com.example.emod.HomeActivity
import com.example.emod.data.repository.ProfileRepository
import com.example.emod.databinding.FragmentSignup5Binding
import kotlinx.coroutines.launch

class Signup5Fragment
    : BaseFragment<FragmentSignup5Binding>(FragmentSignup5Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()
    private lateinit var repo: ProfileRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = ProfileRepository(context.applicationContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = vm.name?.takeIf { it.isNotBlank() } ?: "우리 아이"
        binding.tvName.text = name

        val prefs = context?.getSharedPreferences("my name", Context.MODE_PRIVATE)
        prefs?.edit()?.putString("userName", "${name}")?.apply()

        binding.btnToHome.setOnClickListener { postProfileAndToMain() }
    }

    private fun postProfileAndToMain() {
        binding.btnToHome.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            val result = repo.postProfile(vm)
            if (!isAdded) return@launch
            binding.btnToHome.isEnabled = true

            result.onSuccess {
                // ✅ 레포에서 profileCompleted = true 저장 완료했으니 바로 메인
                startActivity(
                    Intent(requireContext(), HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                )
                // flags로 Task를 갈아끼우므로 finish()는 선택사항
                requireActivity().finish()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
