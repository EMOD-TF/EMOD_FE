package com.example.emod.Diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.emod.BaseFragment
import com.example.emod.HomeActivity
import com.example.emod.databinding.FragmentDiaryEndBinding

class DiaryEndFragment : BaseFragment<FragmentDiaryEndBinding>(FragmentDiaryEndBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 프래그먼트로 이동
        binding.btnTurnoff.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }
    }

}