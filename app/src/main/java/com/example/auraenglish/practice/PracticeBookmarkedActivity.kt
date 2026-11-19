package com.example.auraenglish.practice

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeBookmarkedBinding
import com.example.auraenglish.databinding.ItemPracticeBookmarkedBinding

class PracticeBookmarkedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBookmarkedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeBookmarkedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.chipAll.text = getString(R.string.practice_filter_flagged_all, 3)
        binding.chipTodo.text = getString(R.string.practice_filter_flagged_todo, 1)
        binding.chipDone.text = getString(R.string.practice_filter_flagged_done, 2)

        bindItem(
            binding.item1, "11", getString(R.string.practice_tag_grammar_word),
            "The new policy has been ______ by all department managers.",
            getString(R.string.practice_selected_answer, "C"),
            R.drawable.bg_practice_status_doing, R.color.practice_chip_progress_text
        )
        bindItem(
            binding.item2, "12", getString(R.string.practice_tag_grammar_verb),
            "The marketing team will ______ the new product to our clients...",
            getString(R.string.practice_selected_answer, "A"),
            R.drawable.bg_practice_status_doing, R.color.practice_chip_progress_text
        )
        bindItem(
            binding.item3, "14", getString(R.string.practice_tag_vocab_prep),
            "Please submit your expense report ______ Friday afternoon.",
            getString(R.string.practice_status_todo),
            R.drawable.bg_practice_status_todo, R.color.practice_pending_text
        )

        val chips = listOf(binding.chipAll, binding.chipTodo, binding.chipDone)
        chips.forEach { chip -> chip.setOnClickListener { selectChip(chip, chips) } }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun bindItem(
        item: ItemPracticeBookmarkedBinding,
        number: String,
        tag: String,
        question: String,
        status: String,
        statusBg: Int,
        statusColor: Int
    ) {
        item.tvNumber.text = number
        item.tvTag.text = tag
        item.tvQuestion.text = question
        item.tvStatus.text = status
        item.tvStatus.setBackgroundResource(statusBg)
        item.tvStatus.setTextColor(getColor(statusColor))
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_text_secondary))
        }
    }
}
