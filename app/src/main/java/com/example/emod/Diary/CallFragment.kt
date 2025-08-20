package com.example.emod.Diary

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.emod.BaseFragment
import com.example.emod.databinding.FragmentCallBinding

class CallFragment : BaseFragment<FragmentCallBinding>(FragmentCallBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()

        binding.btnCall.setOnClickListener {
            (activity as DiaryActivity).setFragment(ChatFragment())
        }

    }
}