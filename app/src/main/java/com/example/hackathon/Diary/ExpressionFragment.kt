package com.example.hackathon.Diary

import android.graphics.Color
import android.graphics.Outline
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentExpressionBinding
import com.example.hackathon.databinding.FragmentSummarize4Binding

class ExpressionFragment : BaseFragment<FragmentExpressionBinding>(FragmentExpressionBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previewView = binding.imgKidExpression
        val smile = binding.icSmile

        // previewView 띄우기
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(requireContext()))

        // previewView 겉에 잘라내기
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            55f,
            resources.displayMetrics
        )

        previewView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radiusPx)
            }
        }
        previewView.clipToOutline = true


        // 감정 폰트 일부 색 바꾸기
        val fullText = "기분이 속상할 땐,\n어떤 표정을 지을까?"
        val targetText = "속상할 땐"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(targetText)
        val end = start + targetText.length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#0080FF")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvMent.text = spannable
    }

}