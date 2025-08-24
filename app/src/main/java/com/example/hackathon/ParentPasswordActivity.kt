package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.ui.parent.ParentActivity
import com.example.hackathon.databinding.ActivityParentPasswordBinding

class ParentPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPasswordBinding.inflate(layoutInflater)

        binding.passwordSubmit.setOnClickListener {
            val intent = Intent(this, ParentActivity::class.java)
            startActivity(intent)
            finish()
        }

        enableEdgeToEdge()
        setContentView(binding.root)
    }

}