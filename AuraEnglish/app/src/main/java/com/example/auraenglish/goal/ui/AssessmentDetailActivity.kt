package com.example.auraenglish.goal.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.databinding.ActivityAssessmentDetailBinding
import com.example.auraenglish.goal.data.AssessmentSessionStore
import com.example.auraenglish.practice.ui.PracticeAnswerReviewActivity
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AssessmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssessmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssessmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAssessmentDetail)

        bindData()
        setupListeners()
    }

    private fun bindData() = with(binding) {
        val summary = AssessmentSessionStore.getResultSummary()

        tvDetailScore.text = getString(R.string.goal_detail_score_format, summary.score)
        tvDetailCorrectCount.text = summary.correctCount.toString()
        tvDetailWrongCount.text = summary.wrongCount.toString()
        tvDetailSkipCount.text = summary.skippedCount.toString()
    }

    private fun setupListeners() = with(binding) {
        btnDetailBack.setOnClickListener { finish() }

        btnDetailReview.setOnClickDebounced {
            // Can open answer review activity
            startActivity(Intent(this@AssessmentDetailActivity, PracticeAnswerReviewActivity::class.java))
        }

        btnDetailStartLearning.setOnClickDebounced {
            val intent = Intent(this@AssessmentDetailActivity, DashboardHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        tvDetailRetake.setOnClickListener {
            AssessmentSessionStore.resetSession()
            val intent = Intent(this@AssessmentDetailActivity, AssessmentQuizActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}
