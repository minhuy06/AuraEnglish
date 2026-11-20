package com.example.auraenglish.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.databinding.ActivityAuthResetSuccessBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class ResetSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthResetSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthResetSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthResetSuccess)
        binding.btnLoginNow.setOnClickDebounced {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}
