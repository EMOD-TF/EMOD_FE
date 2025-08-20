package com.example.hackathon.Diary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.hackathon.BaseFragment
import com.example.hackathon.R
import com.example.hackathon.databinding.FragmentFinishEmoBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class FinishEmoFragment : BaseFragment<FragmentFinishEmoBinding>(FragmentFinishEmoBinding::inflate) {

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
                    }
                    override fun onError(utteranceId: String?) {}
                })

            }
        }

        // 3초 뒤 다음 프래그먼트로 이동
        lifecycleScope.launch {
            speakAndStartListening("")
            (activity as DiaryActivity).setFragment(DiaryEndFragment())
        }

//        // 다음 프래그먼트로 이동
//        binding.btnSummarizeChat1.setOnClickListener {
//            (activity as DiaryActivity).setFragment(Summarize2Fragment())
//        }
    }

    private fun speakAndStartListening(text: String) {
        if (isTtsInitialized) {
            val params = Bundle()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "QUESTION_ID")
        }
    }
}