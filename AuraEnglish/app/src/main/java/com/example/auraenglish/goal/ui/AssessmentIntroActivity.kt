package com.example.auraenglish.goal.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.databinding.ActivityAssessmentIntroBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AssessmentIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssessmentIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssessmentIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAssessmentIntro)

        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        btnIntroBack.setOnClickListener { finish() }

        btnStartQuiz.setOnClickDebounced {
            startActivity(Intent(this@AssessmentIntroActivity, AssessmentQuizActivity::class.java))
        }

        tvIntroSkip.setOnClickListener {
            val intent = Intent(this@AssessmentIntroActivity, DashboardHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
