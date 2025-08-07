package com.example.hackathon.Diary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.hackathon.R
import com.example.hackathon.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        setFragment(CallFragment())
    }

    private fun setFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.fl_diary, fragment)
        }
    }
}