package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.utils.*

import com.example.auraenglish.practice.helper.*

import com.example.auraenglish.practice.data.*

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeRankBinding
import com.example.auraenglish.practice.adapter.PracticeRankAdapter
import com.example.auraenglish.practice.viewmodel.PracticeRankViewModel
import com.example.auraenglish.practice.viewmodel.RankUiState

class PracticeRankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeRankBinding
    private val viewModel: PracticeRankViewModel by viewModels()
    private lateinit var rankAdapter: PracticeRankAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeRankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupViews()
        setupListeners()
        observeData()
        playEntrance()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupViews() {
        rankAdapter = PracticeRankAdapter()
        binding.rankList.apply {
            layoutManager = LinearLayoutManager(this@PracticeRankActivity)
            adapter = rankAdapter
            setHasFixedSize(true)
        }
        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.RANK)
    }

    private fun setupListeners() {
        val chips = listOf(
            binding.chipDay to RankPeriod.DAY,
            binding.chipWeek to RankPeriod.WEEK,
            binding.chipMonth to RankPeriod.MONTH,
            binding.chipYear to RankPeriod.YEAR
        )
        chips.forEach { (chip, value) ->
            chip.setOnClickListener {
                viewModel.setPeriod(value)
                selectChip(chip, chips.map { it.first })
            }
        }
    }

    private fun observeData() {
        viewModel.uiState.observe(this) { state ->
            bindUi(state)
            selectChipForPeriod(state.period)
        }
    }

    private fun bindUi(state: RankUiState) = with(binding) {
        val top = state.podium
        bindPodium(top.getOrNull(0), avatar1, name1, xp1, R.color.practice_medal_gold)
        bindPodium(top.getOrNull(1), avatar2, name2, xp2, R.color.practice_medal_silver)
        bindPodium(top.getOrNull(2), avatar3, name3, xp3, R.color.practice_medal_bronze)

        rankAdapter.submitList(state.listRows)

        val me = state.me ?: return@with
        val above = state.aboveMe
        tvStickyRank.text = me.rank.toString()
        tvStickyAvatar.text = me.initials
        tintAvatar(tvStickyAvatar, android.R.color.white)
        tvStickyAvatar.setTextColor(
            ContextCompat.getColor(this@PracticeRankActivity, R.color.practice_rank_sticky)
        )
        tvStickyName.text = me.name
        tvStickyXp.text = getString(
            R.string.practice_rank_xp,
            PracticeRankData.formatXp(me.xp)
        )
        tvStickyGap.text = if (above != null) {
            getString(
                R.string.practice_rank_gap,
                (above.xp - me.xp).coerceAtLeast(0),
                above.rank
            )
        } else {
            getString(R.string.practice_rank_title)
        }
    }

    private fun selectChipForPeriod(period: RankPeriod) {
        val selected = when (period) {
            RankPeriod.DAY -> binding.chipDay
            RankPeriod.WEEK -> binding.chipWeek
            RankPeriod.MONTH -> binding.chipMonth
            RankPeriod.YEAR -> binding.chipYear
        }
        selectChip(
            selected,
            listOf(binding.chipDay, binding.chipWeek, binding.chipMonth, binding.chipYear)
        )
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (active) R.color.white else R.color.practice_primary
                )
            )
            chip.setTypeface(
                null,
                if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )
            chip.elevation = if (active) 4f else 0f
        }
    }

    private fun bindPodium(
        entry: RankEntry?,
        avatar: TextView,
        name: TextView,
        xp: TextView,
        medalColorRes: Int
    ) {
        if (entry == null) return
        avatar.text = entry.initials
        tintPodiumAvatar(avatar, entry.avatarColorRes, medalColorRes)
        name.text = entry.name
        xp.text = getString(R.string.practice_rank_xp, PracticeRankData.formatXp(entry.xp))
    }

    private fun tintPodiumAvatar(view: TextView, fillColorRes: Int, strokeColorRes: Int) {
        val stroke = (3f * resources.displayMetrics.density).toInt().coerceAtLeast(3)
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(this@PracticeRankActivity, fillColorRes))
            setStroke(stroke, ContextCompat.getColor(this@PracticeRankActivity, strokeColorRes))
        }
    }

    private fun tintAvatar(view: TextView, colorRes: Int) {
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(this@PracticeRankActivity, colorRes))
        }
    }

    private fun playEntrance() {
        val targets = listOf<View>(binding.podiumRow, binding.rankList, binding.stickyRank)
        val animators = targets.mapIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 18f
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
                    ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 18f, 0f)
                )
                duration = 220L
                startDelay = index * 70L
                interpolator = DecelerateInterpolator()
            }
        }
        AnimatorSet().apply {
            playTogether(animators)
            start()
        }
    }
}
