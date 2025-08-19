package com.example.hackathon.Diary

import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hackathon.BaseFragment
import com.example.hackathon.Diary.summary.Summarize1Fragment
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.data.remote.client.ApiClient
import com.example.hackathon.data.remote.dto.proceed.ProceedRequest
import com.example.hackathon.data.remote.dto.summary.SummaryRequest
import com.example.hackathon.data.repository.AuthRepository
import com.example.hackathon.databinding.FragmentChatBinding
import kotlinx.coroutines.launch
import java.util.Locale

class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private val store by lazy { AuthRepository(requireContext()) }
    private val conversation = mutableListOf<String>()
    private var lastQuestion: String? = null
    private var recognizedText: String? = null
    private var isTtsInitialized = false

    private lateinit var tts: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private var pendingSpeechText: String? = null

    // Activity 범위 viewModel
    private val viewModel: SummaryViewModel by activityViewModels()

    // ✅ ApiService 생성 (AuthInterceptor 반영)
    private val api by lazy {
        ApiClient.createApi {
            store.getJwt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.popBackStack()

        val previewView = binding.imgKidFace

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

        isTtsInitialized = false
        pendingSpeechText = null
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                isTtsInitialized = true

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        Handler(Looper.getMainLooper()).post { startSpeechRecognition() }
                    }
                    override fun onError(utteranceId: String?) {}
                })

                // 혹시 초기화 전에 대기시킨 text가 있으면 바로 speak
                pendingSpeechText?.let {
                    speakAndStartListening(it)
                    pendingSpeechText = null
                }
            }
        }

        // STT 초기화
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        // 첫 질문 API 호출
        callProceed(emptyList(), 1)

        // "답변 끝내기" 버튼
        binding.btnFinish.setOnClickListener {
            if (lastQuestion.isNullOrBlank()) return@setOnClickListener
            if (recognizedText.isNullOrBlank()) {
                Toast.makeText(requireContext(), "음성을 먼저 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            conversation.add("assistant: $lastQuestion")
            conversation.add("user: $recognizedText")
            Log.d("ChatFragment", "보내는 대화: $conversation")
            callProceed(conversation, conversationStep())
        }

        // 뒤로가기
        binding.icTurnoff.setOnClickListener {
            (activity as DiaryActivity).setFragment(Summarize1Fragment())
        }
    }

    // ✅ proceed API 호출
    private fun callProceed(conversation: List<String>, currentStep: Int) {
        lifecycleScope.launch {
            try {
                val response = api.proceed(
                    ProceedRequest(conversation, currentStep)
                )
                Log.d("ChatFragment", "API Response: $response")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ChatFragment", "API Body: $body")
                    body?.let {
                        lastQuestion = it.questionToAsk

                        if (!it.isAnswerValid) {
                            // 질문을 TextView & TTS로 출력
                            binding.tvChatIng.text = it.questionToAsk
                            speakAndStartListening(it.questionToAsk)
                        } else {
                            // ✅ 마지막 단계 체크
                            if (it.isAnswerValid && it.nextStep == 5 && currentStep == 4) {
                                Log.d("ChatFragment", "마지막 단계 완료 → Summary 호출")
                                callSummary()
                            } else if (it.nextStep <= 4) {
                                callProceed(conversation, it.nextStep)
                            } else {
                                callSummary()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatFragment", "API Exception", e)
            }
        }
    }

    // ✅ Summary API 호출
    private fun callSummary() {
        lifecycleScope.launch {
            try {
                val response = api.summary(SummaryRequest(conversation))
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        Log.d("ChatFragment", "Summary: $it")

//                        // 👉 데이터 번들에 담기 (keyword + sentence)
//                        val bundle = Bundle().apply {
//                            putString("keyword_place", it.place.keyword)
//                            putString("sentence_place", it.place.sentence)
//
//                            putString("keyword_event", it.event.keyword)
//                            putString("sentence_event", it.event.sentence)
//
//                            putString("keyword_topic", it.topic.keyword)
//                            putString("sentence_topic", it.topic.sentence)
//
//                            putString("keyword_emotion", it.emotion.keyword)
//                            putString("sentence_emotion", it.emotion.sentence)
//                        }
//
//                        // 👉 Summarize1Fragment 로 이동
//                        val fragment = Summarize1Fragment().apply {
//                            arguments = bundle
//                        }

                        // ViewModel에 데이터 저장
                        viewModel.summaryData.value = it

                        (activity as DiaryActivity).setFragment(Summarize1Fragment())
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatFragment", "Summary API Exception", e)
            }
        }
    }

    // ✅ TTS → 끝난 뒤 STT 실행
    private fun speakAndStartListening(text: String) {
        if (isTtsInitialized) {
            val params = Bundle()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "QUESTION_ID")
        } else {
            // 아직 TTS 준비 안 됐으면 text를 대기큐에 넣어둔다!
            pendingSpeechText = text
            Log.e("ChatFragment", "TTS not initialized yet, sentence queued")
        }
    }

    // ✅ STT (TTS 이후 자동 실행됨)
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                recognizedText = matches?.joinToString(" ") ?: ""
//                binding.tvUserInput.text = recognizedText
            }
            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(p0: Int) {}
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
            override fun onBeginningOfSpeech() {}
        })

        speechRecognizer.startListening(intent)
    }

    // 현재 step 계산
    private fun conversationStep(): Int {
        return (conversation.size / 2) + 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts.stop()
        tts.shutdown()
        speechRecognizer.destroy()
        Log.d("ChatFragment", "Destroyed")
    }
}