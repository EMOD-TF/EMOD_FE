//package com.example.hackathon.SignUp
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.fragment.app.activityViewModels
//import com.example.hackathon.BaseFragment
//import com.example.hackathon.databinding.FragmentSignup5Binding
//import org.json.JSONObject
//
//class Signup5Fragment
//    : BaseFragment<FragmentSignup5Binding>(FragmentSignup5Binding::inflate) {
//
//    private val vm: SignupViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // 1) 보기 좋게 들여쓰기(2칸)
//        val raw = vm.toResultJson()
//        val pretty = try {
//            JSONObject(raw).toString(2)
//        } catch (e: Exception) {
//            raw // 혹시 파싱 실패하면 원문 노출
//        }
//        binding.tvResult.text = pretty
//
//        // 2) 길게 눌러 복사
//        binding.tvResult.setOnLongClickListener {
//            copyToClipboard(pretty)
//            Toast.makeText(requireContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun copyToClipboard(text: String) {
//        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        cm.setPrimaryClip(ClipData.newPlainText("signup_result", text))
//    }
//}
package com.example.hackathon.SignUp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.hackathon.BaseFragment
import com.example.hackathon.MainActivity
import com.example.hackathon.databinding.FragmentSignup5Binding

class Signup5Fragment
    : BaseFragment<FragmentSignup5Binding>(FragmentSignup5Binding::inflate) {

    private val vm: SignupViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 2단계에서 입력한 이름을 표시 (없으면 기본값)
        val name = vm.name?.takeIf { it.isNotBlank() } ?: "우리 아이"
        binding.tvName.text = name

        binding.btnToHome.setOnClickListener {
            // 프로필 전송 후 메인 화면으로 이동
            postProfileAndToMain()
        }
    }

    private fun postProfileAndToMain() {
        // 프로필 전송 구현
        // 메인화면 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // 현재 액티비티 종료
    }
}
