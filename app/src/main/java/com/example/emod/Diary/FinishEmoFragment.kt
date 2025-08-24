package com.example.emod.Diary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.emod.BaseFragment
import com.example.emod.Diary.viewmodel.SummaryViewModel
import com.example.emod.databinding.FragmentFinishEmoBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class FinishEmoFragment : BaseFragment<FragmentFinishEmoBinding>(FragmentFinishEmoBinding::inflate) {

    private val viewModel: SummaryViewModel by activityViewModels()
    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false
    private var pendingSpeechText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isTtsInitialized = false
        pendingSpeechText = null

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("FinishEmoFragment", "Korean language not supported or missing data")
                    // 필요 시 사용자 안내
                } else {
                    isTtsInitialized = true
                    Log.d("FinishEmoFragment", "TTS initialized successfully")

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {
                            // TTS 끝난 뒤 UI 스레드에서 다음 동작
                            Handler(Looper.getMainLooper()).post {
                                lifecycleScope.launch {
                                    delay(1000) // 추가 지연 시간
                                    (activity as? DiaryActivity)?.setFragment(DiaryEndFragment())
                                }
                            }
                        }
                        override fun onError(utteranceId: String?) {
                            Log.e("FinishEmoFragment", "TTS error on utterance: $utteranceId")
                        }
                    })

                    // 초기화 전 대기 중인 텍스트가 있다면 바로 재생
                    pendingSpeechText?.let {
                        speakAndStartListening(it)
                        pendingSpeechText = null
                    }
                }
            } else {
                Log.e("FinishEmoFragment", "TTS initialization failed with status: $status")
            }
        }

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            val emotion = summary.emotion.keyword
            val currentEmotion = when (emotion) {
                "기쁨" -> "기쁠"
                "화남" -> "화날"
                else -> "슬플"
            }

            val finishText = "정말 잘했어!\n앞으로 ${currentEmotion} 때는 이런 표정을 지으며 표현해봐!"

            binding.tvFinishDairy.text = finishText
            speakAndStartListening(finishText)
        }
    }

    private fun speakAndStartListening(text: String) {
        if (isTtsInitialized) {
            val params = Bundle()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "FINISH_TTS")
        } else {
            pendingSpeechText = text
            Log.d("FinishEmoFragment", "TTS not initialized yet, speech queued")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts.stop()
        tts.shutdown()
    }
}
