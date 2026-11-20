package com.example.auraenglish.room.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.databinding.ActivityRoomRankingBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.room.adapter.RoomRankingAdapter
import com.example.auraenglish.room.data.RoomRankEntryUiModel
import com.example.auraenglish.room.data.RoomSessionStore

class RoomRankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomRankingBinding
    private val rankingAdapter = RoomRankingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        bindRankingData()
        setupListeners()
    }

    private fun setupViews() = with(binding) {
        val basePadding = resources.getDimensionPixelSize(R.dimen.room_screen_padding)
        ViewCompat.setOnApplyWindowInsetsListener(rootRoomRanking) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                basePadding + bars.left,
                basePadding + bars.top,
                basePadding + bars.right,
                basePadding + bars.bottom
            )
            insets
        }

        rvRoomRankings.layoutManager = LinearLayoutManager(this@RoomRankingActivity)
        rvRoomRankings.setHasFixedSize(true)
        rvRoomRankings.adapter = rankingAdapter
    }

    private fun bindRankingData() = with(binding) {
        val room = RoomSessionStore.currentRoom
        val rankings = RoomSessionStore.lastRankingResults

        tvRoomRankSubtitle.text = getString(
            R.string.room_ranking_subtitle_fmt,
            room.code,
            room.topic.label
        )

        bindCurrentUserSummary(rankings)
        bindPodium(rankings)
        rankingAdapter.submitList(rankings)
    }

    private fun bindCurrentUserSummary(rankings: List<RoomRankEntryUiModel>) = with(binding) {
        val currentUserEntry = rankings.firstOrNull { it.member.isCurrentUser }
        if (currentUserEntry != null) {
            val xpBonus = currentUserEntry.score / 10 + 50
            tvYourRankSummary.visible()
            tvYourRankSummary.text = getString(
                R.string.room_ranking_your_summary_fmt,
                currentUserEntry.rank,
                currentUserEntry.correctCount,
                currentUserEntry.totalQuestions,
                currentUserEntry.formattedTime,
                xpBonus
            )
        } else {
            tvYourRankSummary.gone()
        }
    }

    private fun bindPodium(rankings: List<RoomRankEntryUiModel>) = with(binding) {
        layoutPodium.visibleIf(rankings.isNotEmpty())

        val first = rankings.getOrNull(0)
        podiumFirst.visibleIf(first != null)
        first?.let {
            tvPodium1Name.text = it.member.name
            tvPodium1Score.text = getString(R.string.room_ranking_score_fmt, it.score)
        }

        val second = rankings.getOrNull(1)
        podiumSecond.visibleIf(second != null)
        second?.let {
            tvPodium2Name.text = it.member.name
            tvPodium2Score.text = getString(R.string.room_ranking_score_fmt, it.score)
        }

        val third = rankings.getOrNull(2)
        podiumThird.visibleIf(third != null)
        third?.let {
            tvPodium3Name.text = it.member.name
            tvPodium3Score.text = getString(R.string.room_ranking_score_fmt, it.score)
        }
    }

    private fun setupListeners() = with(binding) {
        btnRoomRematch.setOnClickDebounced {
            RoomSessionStore.resetExamAnswers()
            val intent = Intent(this@RoomRankingActivity, RoomLobbyActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
        }

        btnRoomBackHome.setOnClickDebounced {
            val intent = Intent(this@RoomRankingActivity, DashboardHomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
        }
    }
}
