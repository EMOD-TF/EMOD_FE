package com.example.hackathon.SignUp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.databinding.FragmentSignup5Binding
import org.json.JSONObject

class Signup5Fragment
    : BaseFragment<FragmentSignup5Binding>(FragmentSignup5Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 보기 좋게 들여쓰기(2칸)
        val raw = vm.toResultJson()
        val pretty = try {
            JSONObject(raw).toString(2)
        } catch (e: Exception) {
            raw // 혹시 파싱 실패하면 원문 노출
        }
        binding.tvResult.text = pretty

        // 2) 길게 눌러 복사
        binding.tvResult.setOnLongClickListener {
            copyToClipboard(pretty)
            Toast.makeText(requireContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun copyToClipboard(text: String) {
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("signup_result", text))
    }
}
