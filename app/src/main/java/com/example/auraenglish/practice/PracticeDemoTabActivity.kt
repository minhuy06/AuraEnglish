package com.example.auraenglish.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeDemoTabBinding
import com.example.auraenglish.databinding.ItemPracticeSavedWordBinding

class PracticeDemoTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeDemoTabBinding
    private lateinit var tab: PracticeTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeDemoTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tab = runCatching {
            PracticeTab.valueOf(intent.getStringExtra(PracticeNavHelper.EXTRA_TAB) ?: PracticeTab.HOME.name)
        }.getOrDefault(PracticeTab.HOME)

        val (title, desc, icon) = when (tab) {
            PracticeTab.HOME -> Triple(
                getString(R.string.practice_nav_home),
                getString(R.string.practice_demo_home_desc),
                R.drawable.ic_nav_home
            )
            PracticeTab.LIBRARY -> Triple(
                getString(R.string.practice_nav_library),
                getString(R.string.practice_demo_library_desc),
                R.drawable.ic_nav_library
            )
            PracticeTab.RANK -> Triple(
                getString(R.string.practice_nav_rank),
                getString(R.string.practice_demo_rank_desc),
                R.drawable.ic_nav_rank
            )
            PracticeTab.SETTINGS -> Triple(
                getString(R.string.practice_nav_settings),
                getString(R.string.practice_demo_settings_desc),
                R.drawable.ic_nav_settings
            )
            PracticeTab.PRACTICE -> Triple(
                getString(R.string.practice_nav_practice),
                "",
                R.drawable.ic_nav_practice
            )
        }

        binding.tvTitle.text = title
        binding.tvHeadline.text = title
        binding.tvDescription.text = desc
        binding.ivIcon.setImageResource(icon)
        binding.ivIcon.setColorFilter(getColor(R.color.practice_primary))

        binding.librarySection.isVisible = tab == PracticeTab.LIBRARY
        if (tab == PracticeTab.LIBRARY) {
            renderLibraryWords()
        }

        binding.btnBack.setOnClickListener { goPractice() }
        binding.btnPrimary.setOnClickListener { goPractice() }
        PracticeNavHelper.bind(this, binding.bottomNavInclude, tab)
    }

    private fun renderLibraryWords() {
        val words = PracticeSampleData.savedWords
        binding.tvLibraryTitle.text = getString(R.string.practice_library_words_title, words.size)
        binding.tvLibraryEmpty.isVisible = words.isEmpty()
        binding.wordList.removeAllViews()
        words.forEach { saved ->
            val item = ItemPracticeSavedWordBinding.inflate(
                LayoutInflater.from(this), binding.wordList, false
            )
            item.tvWord.text = saved.word
            item.tvMeta.text = getString(
                R.string.practice_library_word_meta,
                saved.topic,
                saved.fromQuestion
            )
            binding.wordList.addView(item.root)
        }
    }

    private fun goPractice() {
        startActivity(
            Intent(this, PracticeExamListActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}
