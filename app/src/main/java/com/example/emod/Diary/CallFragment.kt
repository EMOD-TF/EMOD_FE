package com.example.emod.Diary

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.emod.BaseFragment
import com.example.emod.databinding.FragmentCallBinding

class CallFragment : BaseFragment<FragmentCallBinding>(FragmentCallBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        requireActivity().enableEdgeToEdge()

        binding.btnCall.setOnClickListener {
            (activity as DiaryActivity).setFragment(ChatFragment())
        }

    }
    private val permissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    private fun checkPermissions() {
        if (permissions.any {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 100)
        }
    }
}