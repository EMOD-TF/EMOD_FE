package com.example.hackathon.Diary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.Button
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.hackathon.Diary.viewmodel.SummaryViewModel
import com.example.hackathon.HomeActivity
import com.example.hackathon.R
import com.example.hackathon.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryBinding

    // Activity Scope ViewModel
    val summaryViewModel: SummaryViewModel by viewModels()

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

        onBackPressedDispatcher.addCallback(this) {
            showExitDiaryDialog()
        }

    }

    fun setFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.fl_diary, fragment, fragment::class.java.name)
        }
    }

    private fun showExitDiaryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit_diary, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            // 홈 액티비티로 이동
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 스택 제거
            startActivity(intent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            // 그냥 닫기
            dialog.dismiss()
        }

        dialog.show()
    }

}