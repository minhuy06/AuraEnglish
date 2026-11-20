package com.example.auraenglish.admin.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.admin.data.AdminActivityPoint
import com.example.auraenglish.admin.data.AdminCoursePerformance
import com.example.auraenglish.admin.data.AdminStatisticsPeriod
import com.example.auraenglish.admin.data.AdminStatisticsUiModel
import com.example.auraenglish.admin.viewmodel.AdminStatisticsViewModel
import com.example.auraenglish.databinding.ActivityAdminStatisticsBinding
import com.example.auraenglish.databinding.ItemAdminActivityBarBinding
import com.example.auraenglish.databinding.ItemAdminCourseProgressBinding
import com.example.auraenglish.databinding.ItemAdminMetricBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

class AdminStatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminStatisticsBinding
    private val viewModel: AdminStatisticsViewModel by viewModels()
    private val locale = Locale("vi", "VN")
    private val integerFormatter: NumberFormat by lazy { NumberFormat.getIntegerInstance(locale) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupMetricCards()
        setupListeners()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootAdminStatistics) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupMetricCards() = with(binding) {
        configureMetric(
            metricStudents,
            R.string.admin_stats_metric_students,
            R.color.admin_primary
        )
        configureMetric(
            metricActive,
            R.string.admin_stats_metric_active,
            R.color.admin_success
        )
        configureMetric(
            metricCompletion,
            R.string.admin_stats_metric_completion,
            R.color.admin_purple
        )
        configureMetric(
            metricRevenue,
            R.string.admin_stats_metric_revenue,
            R.color.admin_warning
        )
    }

    private fun configureMetric(
        card: ItemAdminMetricBinding,
        @StringRes labelRes: Int,
        @ColorRes accentColorRes: Int
    ) {
        card.tvMetricLabel.setText(labelRes)
        card.viewMetricAccent.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, accentColorRes)
        )
    }

    private fun setupListeners() = with(binding) {
        chipStatsWeek.setOnClickDebounced {
            viewModel.setPeriod(AdminStatisticsPeriod.WEEK)
        }
        chipStatsMonth.setOnClickDebounced {
            viewModel.setPeriod(AdminStatisticsPeriod.MONTH)
        }
        chipStatsYear.setOnClickDebounced {
            viewModel.setPeriod(AdminStatisticsPeriod.YEAR)
        }

        includeAdminBottomNav.navCourses.setOnClickDebounced {
            openAdminScreen(AdminCourseManageActivity::class.java)
        }
        includeAdminBottomNav.navExams.setOnClickDebounced {
            openAdminScreen(AdminExamShopActivity::class.java)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            bindState(state)
        }
    }

    private fun bindState(state: AdminStatisticsUiModel) = with(binding) {
        metricStudents.tvMetricValue.text = integerFormatter.format(state.totalStudents)
        bindTrend(metricStudents, state.totalStudentsChange)

        metricActive.tvMetricValue.text = integerFormatter.format(state.activeStudents)
        bindTrend(metricActive, state.activeStudentsChange)

        metricCompletion.tvMetricValue.text = getString(
            R.string.admin_stats_percent_fmt,
            state.completionPercent
        )
        bindTrend(metricCompletion, state.completionChange)

        val revenueMillions = String.format(locale, "%.1f", state.revenueVnd / 1_000_000.0)
        metricRevenue.tvMetricValue.text = getString(
            R.string.admin_stats_revenue_value_fmt,
            revenueMillions
        )
        bindTrend(metricRevenue, state.revenueChange)

        bindPeriod(state.period)
        bindActivity(state.activity)
        bindTopCourses(state.topCourses)
    }

    private fun bindTrend(card: ItemAdminMetricBinding, change: Double) {
        val amount = String.format(locale, "%.1f%%", abs(change))
        val (textRes, colorRes) = when {
            change > 0.0 -> R.string.admin_stats_positive_change_fmt to R.color.admin_success
            change < 0.0 -> R.string.admin_stats_negative_change_fmt to R.color.admin_danger
            else -> R.string.admin_stats_stable_change to R.color.admin_text_secondary
        }
        card.tvMetricTrend.text = if (change == 0.0) {
            getString(textRes)
        } else {
            getString(textRes, amount)
        }
        card.tvMetricTrend.setTextColor(ContextCompat.getColor(this, colorRes))
    }

    private fun bindPeriod(period: AdminStatisticsPeriod) {
        val chips = listOf(
            binding.chipStatsWeek to AdminStatisticsPeriod.WEEK,
            binding.chipStatsMonth to AdminStatisticsPeriod.MONTH,
            binding.chipStatsYear to AdminStatisticsPeriod.YEAR
        )
        chips.forEach { (chip, value) ->
            stylePeriodChip(chip, value == period)
        }
    }

    private fun stylePeriodChip(chip: TextView, active: Boolean) {
        chip.setBackgroundResource(
            if (active) R.drawable.bg_admin_chip_active else R.drawable.bg_admin_chip_inactive
        )
        chip.setTextColor(
            ContextCompat.getColor(
                this,
                if (active) R.color.white else R.color.admin_text_secondary
            )
        )
        chip.setTypeface(null, if (active) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun bindActivity(points: List<AdminActivityPoint>) {
        val rows = listOf(
            binding.activityBar1,
            binding.activityBar2,
            binding.activityBar3,
            binding.activityBar4,
            binding.activityBar5,
            binding.activityBar6,
            binding.activityBar7
        )
        val maxValue = points.maxOfOrNull { it.learners }?.coerceAtLeast(1) ?: 1

        rows.forEachIndexed { index, row ->
            val point = points.getOrNull(index)
            row.root.visibility = if (point == null) android.view.View.GONE else android.view.View.VISIBLE
            if (point != null) bindActivityRow(row, point, maxValue)
        }
    }

    private fun bindActivityRow(
        row: ItemAdminActivityBarBinding,
        point: AdminActivityPoint,
        maxValue: Int
    ) = with(row) {
        tvActivityLabel.text = point.label
        tvActivityValue.text = integerFormatter.format(point.learners)
        pbActivity.progress = (point.learners * 100.0 / maxValue).roundToInt()
    }

    private fun bindTopCourses(courses: List<AdminCoursePerformance>) {
        val rows = listOf(
            binding.courseProgress1,
            binding.courseProgress2,
            binding.courseProgress3
        )
        rows.forEachIndexed { index, row ->
            val course = courses.getOrNull(index)
            row.root.visibility = if (course == null) android.view.View.GONE else android.view.View.VISIBLE
            if (course != null) bindCourseRow(row, course)
        }
    }

    private fun bindCourseRow(
        row: ItemAdminCourseProgressBinding,
        course: AdminCoursePerformance
    ) = with(row) {
        tvCourseProgressTitle.text = course.title
        tvCourseProgressMeta.text = getString(
            R.string.admin_stats_learner_count_fmt,
            course.learnerCount
        )
        tvCourseProgressPercent.text = getString(
            R.string.admin_stats_percent_fmt,
            course.completionPercent
        )
        pbCourseProgress.progress = course.completionPercent
    }

    private fun openAdminScreen(target: Class<out AppCompatActivity>) {
        startActivity(
            Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}
