package com.example.auraenglish.dashboard.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.databinding.ActivityDashboardHomeBinding
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab
import com.example.auraenglish.practice.ui.PracticeExamListActivity
import com.example.auraenglish.practice.utils.setOnClickDebounced

// Trang chủ học viên (Figma Trang chủ)
class DashboardHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.HOME)

        binding.btnCart.setOnClickDebounced {
            startActivity(Intent(this, DashboardStoreActivity::class.java))
        }
        binding.continueCard.setOnClickDebounced {
            startActivity(Intent(this, DashboardCategoryActivity::class.java))
        }
        binding.skillVocab.setOnClickDebounced { openCategory() }
        binding.skillGrammar.setOnClickDebounced { openCategory() }
        binding.skillExam.setOnClickDebounced {
            startActivity(Intent(this, PracticeExamListActivity::class.java))
        }
        binding.skillListen.setOnClickDebounced { openCategory() }
    }

    private fun openCategory() {
        startActivity(Intent(this, DashboardCategoryActivity::class.java))
    }
}
