package com.example.emod.Diary

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isTtsInitialized = false
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                isTtsInitialized = true

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        // TTS 뒤 다음 Fragment로 이동
                        lifecycleScope.launch {
                            delay(1000)
                            (activity as DiaryActivity).setFragment(DiaryEndFragment())
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        Log.e("ChatFragment", "Error in TTS")
                    }
                })

            }
        }

        viewModel.summaryData.observe(viewLifecycleOwner) { summary ->
            val emotion = summary.emotion.keyword
            var currentEmotion = ""

            if (emotion == "기쁨") {
                currentEmotion = "기쁠"
            }
            else if (emotion == "화남") {
                currentEmotion = "화날"
            }
            else {
                currentEmotion = "슬플"
            }

            var finishText = "정말 잘했어!\n앞으로 " + "${currentEmotion} 때는 " + "이런 표정을 지으며 표현해봐!"

            // TTS
            speakAndStartListening(finishText)
            binding.tvFinishDairy.text = finishText
        }

    }

    private fun speakAndStartListening(text: String) {
        if (isTtsInitialized) {
            val params = Bundle()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "FINISH_TTS")
        }
    }
}