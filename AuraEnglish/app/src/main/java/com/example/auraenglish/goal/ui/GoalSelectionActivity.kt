package com.example.auraenglish.goal.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.databinding.ActivityGoalSelectionBinding
import com.example.auraenglish.goal.data.AssessmentSessionStore
import com.example.auraenglish.practice.utils.setOnClickDebounced

class GoalSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSelectionBinding
    private var selectedGoalId: String = "daily"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGoalSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootGoalSelection)

        setupOptionListeners()
        setupActionButtons()
        updateSelectionUi()
    }

    private fun setupOptionListeners() = with(binding) {
        cardGoalDaily.setOnClickListener {
            selectedGoalId = "daily"
            updateSelectionUi()
        }
        cardGoalToeic.setOnClickListener {
            selectedGoalId = "toeic"
            updateSelectionUi()
        }
        cardGoalIelts.setOnClickListener {
            selectedGoalId = "ielts"
            updateSelectionUi()
        }
        cardGoalSchool.setOnClickListener {
            selectedGoalId = "school"
            updateSelectionUi()
        }
    }

    private fun updateSelectionUi() = with(binding) {
        val options = listOf(
            Triple("daily", cardGoalDaily, ivCheckDaily),
            Triple("toeic", cardGoalToeic, ivCheckToeic),
            Triple("ielts", cardGoalIelts, ivCheckIelts),
            Triple("school", cardGoalSchool, ivCheckSchool)
        )

        options.forEach { (id, card, check) ->
            val isSelected = (id == selectedGoalId)
            card.setBackgroundResource(
                if (isSelected) R.drawable.bg_goal_option_selected
                else R.drawable.bg_goal_card
            )
            check.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }

    private fun setupActionButtons() = with(binding) {
        btnGoalContinue.setOnClickDebounced {
            AssessmentSessionStore.selectedGoalId = selectedGoalId
            startActivity(Intent(this@GoalSelectionActivity, AssessmentIntroActivity::class.java))
        }

        tvGoalSkip.setOnClickListener {
            startActivity(Intent(this@GoalSelectionActivity, AssessmentIntroActivity::class.java))
        }
    }
}
