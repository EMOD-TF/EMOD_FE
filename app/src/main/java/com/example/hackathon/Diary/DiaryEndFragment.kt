package com.example.hackathon.Diary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackathon.BaseFragment
import com.example.hackathon.HomeActivity
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentDiaryEndBinding
import com.example.hackathon.databinding.FragmentFinishEmoBinding

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