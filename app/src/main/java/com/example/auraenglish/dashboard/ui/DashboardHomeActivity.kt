package com.example.auraenglish.dashboard.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.databinding.ActivityDashboardHomeBinding
import com.example.auraenglish.dashboard.data.CourseFilter
import com.example.auraenglish.practice.data.ExamStatus
import com.example.auraenglish.practice.data.PracticeSampleData
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab
import com.example.auraenglish.practice.ui.PracticeExamOverviewActivity
import com.example.auraenglish.practice.utils.setOnClickDebounced

// Trang chủ học viên — hero, tuần, skill cards
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
        bindHeroSuggestion()
        animateSkillBars()

        binding.btnCart.setOnClickDebounced {
            startActivity(Intent(this, DashboardStoreActivity::class.java))
        }
        binding.heroInclude.btnHeroCta.setOnClickDebounced { openSuggested() }
        binding.heroInclude.heroCard.setOnClickDebounced { openSuggested() }
        binding.continueCard.setOnClickDebounced { openCategory() }
        binding.skillVocab.setOnClickDebounced { openCategory(CourseFilter.VOCAB) }
        binding.skillGrammar.setOnClickDebounced { openCategory(CourseFilter.GRAMMAR) }
        binding.skillExam.setOnClickDebounced { openCategory(CourseFilter.EXAM) }
        binding.skillListen.setOnClickDebounced {
            startActivity(
                Intent(this, com.example.auraenglish.listening.ui.ListeningExamListActivity::class.java)
            )
        }
    }

    private fun bindHeroSuggestion() {
        val doing = PracticeSampleData.exams.firstOrNull { it.status == ExamStatus.DOING }
            ?: PracticeSampleData.exams.firstOrNull { it.status == ExamStatus.TODO }
        if (doing != null) {
            val left = ((200 - doing.answeredCount).coerceAtLeast(0) * 0.6).toInt().coerceAtLeast(1)
            binding.heroInclude.tvHeroSuggest.text =
                "Gợi ý: tiếp tục ${doing.title} · còn ~$left phút"
        }
    }

    private fun openSuggested() {
        val exam = PracticeSampleData.exams.firstOrNull { it.status == ExamStatus.DOING }
            ?: PracticeSampleData.exams.firstOrNull { it.status == ExamStatus.TODO }
        if (exam != null) {
            PracticeSampleData.currentExamId = exam.id
            startActivity(
                Intent(this, PracticeExamOverviewActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
            )
        } else {
            openCategory(CourseFilter.EXAM)
        }
    }

    private fun animateSkillBars() {
        animateBar(binding.skillVocabProgress, 0.42f)
        animateBar(binding.skillGrammarProgress, 0.78f)
        animateBar(binding.skillExamProgress, 0.35f)
        animateBar(binding.skillListenProgress, 0.18f)
    }

    private fun animateBar(bar: View, ratio: Float) {
        bar.post {
            val parentW = (bar.parent as View).width
            val target = (parentW * ratio).toInt().coerceAtLeast(8)
            ValueAnimator.ofInt(0, target).apply {
                duration = 520
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    bar.layoutParams = bar.layoutParams.apply { width = it.animatedValue as Int }
                    bar.requestLayout()
                }
                start()
            }
        }
    }

    private fun openCategory(filter: CourseFilter = CourseFilter.ALL) {
        startActivity(
            Intent(this, DashboardCategoryActivity::class.java)
                .putExtra(DashboardCategoryActivity.EXTRA_FILTER, filter.name)
        )
    }
}
