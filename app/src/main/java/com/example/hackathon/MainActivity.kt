package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnFaceTracking.setOnClickListener {
            moveFaceTracking()
        }
        binding.btnVoiceRecognition.setOnClickListener {
            moveVoiceRecognition()
        }
    }

    private fun moveFaceTracking() {
        val intent = Intent(this, FaceTrackingActivity::class.java)
        startActivity(intent)
    }

    private fun moveVoiceRecognition() {
        val intent = Intent(this, VoiceActivity::class.java)
        startActivity(intent)
    }

}