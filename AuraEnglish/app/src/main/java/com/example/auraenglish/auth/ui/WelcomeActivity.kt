package com.example.auraenglish.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.databinding.ActivityAuthWelcomeBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthWelcome)

        binding.btnWelcomeLogin.setOnClickDebounced {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnWelcomeRegister.setOnClickDebounced {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
