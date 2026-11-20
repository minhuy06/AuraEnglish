package com.example.auraenglish.practice

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeRankBinding
import com.example.auraenglish.databinding.ItemPracticeRankRowBinding

class PracticeRankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeRankBinding
    private var period = RankPeriod.WEEK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeRankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val chips = listOf(
            binding.chipDay to RankPeriod.DAY,
            binding.chipWeek to RankPeriod.WEEK,
            binding.chipMonth to RankPeriod.MONTH,
            binding.chipYear to RankPeriod.YEAR
        )
        chips.forEach { (chip, value) ->
            chip.setOnClickListener {
                period = value
                selectChip(chip, chips.map { it.first })
                render(animateList = true)
            }
        }
        selectChip(binding.chipWeek, chips.map { it.first })

        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.RANK)
        render(animateList = false)
        playEntrance()
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

    private fun render(animateList: Boolean) {
        val entries = PracticeRankData.entries(period)
        val top = entries.take(3)
        bindPodium(
            top.getOrNull(0),
            binding.avatar1,
            binding.name1,
            binding.xp1,
            R.color.practice_medal_gold
        )
        bindPodium(
            top.getOrNull(1),
            binding.avatar2,
            binding.name2,
            binding.xp2,
            R.color.practice_medal_silver
        )
        bindPodium(
            top.getOrNull(2),
            binding.avatar3,
            binding.name3,
            binding.xp3,
            R.color.practice_medal_bronze
        )

        binding.rankList.removeAllViews()
        entries.drop(3).forEachIndexed { index, entry ->
            val item = ItemPracticeRankRowBinding.inflate(
                LayoutInflater.from(this), binding.rankList, false
            )
            item.tvRank.text = entry.rank.toString()
            item.tvAvatar.text = entry.initials
            tintAvatar(item.tvAvatar, entry.avatarColorRes)
            item.tvName.text = if (entry.isCurrentUser) {
                getString(R.string.practice_rank_you_suffix, entry.name)
            } else {
                entry.name
            }
            item.tvXp.text = getString(
                R.string.practice_rank_xp,
                PracticeRankData.formatXp(entry.xp)
            )
            item.root.setBackgroundResource(
                if (entry.isCurrentUser) R.drawable.bg_practice_rank_pinned
                else R.drawable.bg_practice_rank_row
            )
            binding.rankList.addView(item.root)
            if (animateList) {
                item.root.alpha = 0f
                item.root.translationY = 12f
                item.root.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay((index * 40L))
                    .setDuration(180L)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }

        val me = entries.first { it.isCurrentUser }
        val above = entries.firstOrNull { it.rank == me.rank - 1 }
        binding.tvStickyRank.text = me.rank.toString()
        binding.tvStickyAvatar.text = me.initials
        tintAvatar(binding.tvStickyAvatar, android.R.color.white)
        binding.tvStickyAvatar.setTextColor(
            ContextCompat.getColor(this, R.color.practice_rank_sticky)
        )
        binding.tvStickyName.text = me.name
        binding.tvStickyXp.text = getString(
            R.string.practice_rank_xp,
            PracticeRankData.formatXp(me.xp)
        )
        binding.tvStickyGap.text = if (above != null) {
            getString(
                R.string.practice_rank_gap,
                (above.xp - me.xp).coerceAtLeast(0),
                above.rank
            )
        } else {
            getString(R.string.practice_rank_title)
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
        val targets = listOf<View>(
            binding.podiumRow,
            binding.rankList,
            binding.stickyRank
        )
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
