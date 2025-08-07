package com.example.hackathon

import android.Manifest
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackathon.databinding.ActivityFaceTrackingBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.util.concurrent.Executors

class FaceTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceTrackingBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null

    // set overlayView
    private lateinit var overlayView: FaceContourOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFaceTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overlayView = binding.overlayView

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        startCamera()
    }

    private fun startCamera() {
        val cameraProvideFuture = ProcessCameraProvider.getInstance(this)
        cameraProvideFuture.addListener({
            val cameraProvider = cameraProvideFuture.get()
            val preview = Preview.Builder().build().also {
                // 미리보기 SurfaceView/PreviewBiew에 연결
//                it.setSurfaceProvider(previewView.surfaceProvider)
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(this))
    }

//    inner class FaceAnalyzer : ImageAnalysis.Analyzer {
//        private val options = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
//            .build()
//        private val detector = FaceDetection.getClient(options)
//
//        @OptIn(ExperimentalGetImage::class)
//        override fun analyze(imageProxy: ImageProxy) {
//            val mediaImage = imageProxy.image ?: run {
//                imageProxy.close()
//                return
//            }
//            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//            detector.process(image)
//                .addOnSuccessListener { faces ->
//                    val matrix = getCorrectionMatrix(imageProxy, binding.previewView)
//                    runOnUiThread {
//                        overlayView.setFaces(faces, matrix)
//
//                    }
//                    imageProxy.close()
//                }
//                .addOnFailureListener {
//                    imageProxy.close()
//                }
//        }
//    }
    inner class FaceAnalyzer : ImageAnalysis.Analyzer {

        private val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()

        private val detector = FaceDetection.getClient(options)

        private var lastLoggedTime = 0L  // 마지막 로그 시간

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return
            }

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastLoggedTime >= 1000) { // 1초마다 로그
                        lastLoggedTime = currentTime

                        for ((index, face) in faces.withIndex()) {
                            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                            val upperLipContour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points

                            Log.d("FaceContour", "Face #$index:")

                            leftEyeContour?.let {
                                Log.d("FaceContour", "  LEFT EYE Points:")
                                it.forEach { point ->
                                    Log.d("FaceContour", "    (${point.x}, ${point.y})")
                                }
                            }

                            upperLipContour?.let {
                                Log.d("FaceContour", "  UPPER LIP Points:")
                                it.forEach { point ->
                                    Log.d("FaceContour", "    (${point.x}, ${point.y})")
                                }
                            }
                        }
                    }

                    // UI 업데이트용 overlay
                    val matrix = getCorrectionMatrix(imageProxy, binding.previewView)
                    runOnUiThread {
                        overlayView.setFaces(faces, matrix)
                    }

                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
        }
    }

    fun getCorrectionMatrix(imageProxy: ImageProxy, previewView: PreviewView): Matrix {
        val matrix = Matrix()

        val imageWidth = imageProxy.width.toFloat()
        val imageHeight = imageProxy.height.toFloat()
        val viewWidth = previewView.width.toFloat()
        val viewHeight = previewView.height.toFloat()
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        // 중심 기준 회전 보정
        matrix.postTranslate(-imageWidth / 2f, -imageHeight / 2f)
        matrix.postRotate((rotationDegrees + 90).toFloat())

        // 회전 후 다시 중앙으로 이동
        if (rotationDegrees == 90 || rotationDegrees == 270) {
            matrix.postTranslate(imageHeight / 2f, imageWidth / 2f)
        } else {
            matrix.postTranslate(imageWidth / 2f, imageHeight / 2f)
        }

        // 스케일 보정
        val scaleX = (viewWidth + 50) / if (rotationDegrees == 90 || rotationDegrees == 270) imageHeight else imageWidth
        val scaleY = (viewHeight + 50) / if (rotationDegrees == 90 || rotationDegrees == 270) imageWidth else imageHeight
        matrix.postScale(scaleX, scaleY)

        // 전면 카메라 미러링
        matrix.postScale(-1f, 1f, viewWidth / 2f, viewHeight / 2f)

        return matrix
    }




    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
        cameraProvider = null
    }
}