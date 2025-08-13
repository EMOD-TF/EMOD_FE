package com.example.hackathon.Diary

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.hackathon.R
import com.example.hackathon.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)

        setFragment(CallFragment())

        onBackPressedDispatcher.addCallback(this) {
            val fm = supportFragmentManager
            if (fm.backStackEntryCount > 0) {
                fm.popBackStack()
            } else {
                finish()
            }
        }

    }

    fun setFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.fl_diary, fragment, fragment::class.java.name)
        }
    }

//    override fun onBackPressed() {
//        val fragmentManager = supportFragmentManager
//        if (fragmentManager.backStackEntryCount > 0) {
//            fragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//    }

}