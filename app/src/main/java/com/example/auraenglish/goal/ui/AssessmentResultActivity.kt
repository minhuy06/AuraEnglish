package com.example.auraenglish.goal.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.databinding.ActivityAssessmentResultBinding
import com.example.auraenglish.goal.data.AssessmentSessionStore
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AssessmentResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssessmentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssessmentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAssessmentResult)

        bindData()
        setupListeners()
    }

    private fun bindData() = with(binding) {
        val summary = AssessmentSessionStore.getResultSummary()

        tvVocabScore.text = "${summary.vocabScore}%"
        pbVocab.progress = summary.vocabScore

        tvGrammarScore.text = "${summary.grammarScore}%"
        pbGrammar.progress = summary.grammarScore

        tvListeningScore.text = "${summary.listeningScore}%"
        pbListening.progress = summary.listeningScore

        tvReadingScore.text = "${summary.readingScore}%"
        pbReading.progress = summary.readingScore
    }

    private fun setupListeners() = with(binding) {
        btnResultDetail.setOnClickDebounced {
            startActivity(Intent(this@AssessmentResultActivity, AssessmentDetailActivity::class.java))
        }

        btnResultStartLearning.setOnClickDebounced {
            val intent = Intent(this@AssessmentResultActivity, DashboardHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
