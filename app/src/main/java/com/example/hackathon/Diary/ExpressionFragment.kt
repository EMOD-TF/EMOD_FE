package com.example.hackathon.Diary

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentExpressionBinding
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.Executors



class ExpressionFragment : BaseFragment<FragmentExpressionBinding>(FragmentExpressionBinding::inflate) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var faceDetector: FaceDetector

    // 외부에서 전달받을 목표 감정 (예: "기쁨", "속상", "화남")
    private var targetEmotion: String = "속상" // 기본값, 실제로는 arguments나 ViewModel에서 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 예: arguments로 전달받기
        targetEmotion = arguments?.getString("targetEmotion") ?: "속상"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // 초기 텍스트 세팅
        binding.tvExpression.text = "현재 인식할 감정: $targetEmotion"

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        faceDetector = FaceDetection.getClient(options)

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.imgKidExpression.surfaceProvider)
            }
            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, FaceAnalyzer())
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    inner class FaceAnalyzer : ImageAnalysis.Analyzer {
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                faceDetector.process(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            val face = faces[0]
                            val detectedEmotion = recognizeEmotion(face)

                            activity?.runOnUiThread {
                                if (detectedEmotion == targetEmotion) {
                                    binding.tvExpression.text = "$targetEmotion 표정 인식 성공!"
                                } else {
                                    binding.tvExpression.text = "현재 인식 표정: $detectedEmotion"
                                }
                            }
                        } else {
                            activity?.runOnUiThread {
                                binding.tvExpression.text = "얼굴을 찾을 수 없습니다."
                            }
                        }
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    private fun recognizeEmotion(face: Face): String {
        val smilingProb = face.smilingProbability ?: 0f
        val upperLip = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
        val leftCorner = upperLip?.firstOrNull()
        val rightCorner = upperLip?.lastOrNull()
        val mouthCenter = upperLip?.getOrNull(upperLip.size / 2)
        val avgCornerY = if (leftCorner != null && rightCorner != null) (leftCorner.y + rightCorner.y) / 2 else 0f
        val centerY = mouthCenter?.y ?: 0f

        return when (targetEmotion) {
            "기쁨" -> if (smilingProb > 0.7f && avgCornerY < centerY) "기쁨" else "다름"
            "속상" -> if (
                smilingProb < 0.3f &&                   // 미소 거의 없음
                (avgCornerY - centerY) > 17f &&           // 입꼬리 확실히 처짐 (8px 이상)
                isSadEyebrowAngle(face)                       // 눈썹 기준 추가
            ) "속상" else "다름"
            "화남" -> if (isAngry(face, smilingProb)) "화남" else "다름"
            else -> "알수없음"
        }
    }

    // 눈썹 각도 판별: 바깥이 안쪽보다 올라가 있으면 슬픔 경향
    private fun isSadEyebrowAngle(face: Face): Boolean {
        val leftEyebrow = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
        val rightEyebrow = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
        if (leftEyebrow == null || rightEyebrow == null) return false

        // 왼쪽 눈썹: 바깥(0) vs 안쪽(마지막) y값 비교
        val leftOuter = leftEyebrow.first().y
        val leftInner = leftEyebrow.last().y
        val rightOuter = rightEyebrow.last().y
        val rightInner = rightEyebrow.first().y

        // 슬픔일 때 바깥이 더 높음 (= y좌표 더 위)
        val leftSlopeSad = leftOuter > leftInner
        val rightSlopeSad = rightOuter > rightInner
        return leftSlopeSad || rightSlopeSad
    }



    // 화남 기준 변경: 눈썹-눈 거리 + 웃음 확률(낮음) + 입꼬리
    private fun isAngry(face: Face, smilingProb: Float): Boolean {
        val leftEyebrow = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
        val rightEyebrow = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
        val leftEye = face.getContour(FaceContour.LEFT_EYE)?.points
        val rightEye = face.getContour(FaceContour.RIGHT_EYE)?.points
        val upperLip = face.getContour(FaceContour.UPPER_LIP_TOP)?.points

        if (leftEyebrow == null || rightEyebrow == null || leftEye == null || rightEye == null || upperLip == null) return false

        // 눈썹-눈 거리 평균 (확실히 내려와야 화남)
        val leftDiff = leftEyebrow.map { it.y }.average() - leftEye.map { it.y }.average()
        val rightDiff = rightEyebrow.map { it.y }.average() - rightEye.map { it.y }.average()

        // 입꼬리 거의 평평 (화난 표정은 입꼬리 올림 없음)
        val leftCorner = upperLip.firstOrNull()
        val rightCorner = upperLip.lastOrNull()
        val mouthCenter = upperLip.getOrNull(upperLip.size / 2)
        val avgCornerY = if (leftCorner != null && rightCorner != null) (leftCorner.y + rightCorner.y) / 2 else 0f
        val centerY = mouthCenter?.y ?: 0f
        val mouthSlope = Math.abs(avgCornerY - centerY)

        // 화남 기준: 눈썹-눈 거리 확실히 감소, 웃지 않고, 입꼬리 평평 (조합)
        return (leftDiff < 10 && rightDiff < 10) &&
                (smilingProb < 0.2f) &&
                (mouthSlope < 5f)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        faceDetector.close()
        cameraExecutor.shutdown()
    }
}

