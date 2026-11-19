package com.example.auraenglish.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeExamListBinding
import com.example.auraenglish.databinding.ItemPracticeExamBinding

class PracticeExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeExamListBinding
    private var currentFilter = ExamStatus.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupFilters()
        setupBottomNav()
        renderList()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSearch.setOnClickListener {
            Toast.makeText(this, "Tìm kiếm (dữ liệu mẫu)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        renderList()
    }

    private fun setupFilters() {
        val map = mapOf(
            binding.chipAll to ExamStatus.ALL,
            binding.chipTodo to ExamStatus.TODO,
            binding.chipDoing to ExamStatus.DOING,
            binding.chipDone to ExamStatus.DONE
        )
        // Mặc định Figma hay để "Chưa làm" active — dùng Tất cả cho dễ xem
        selectChip(binding.chipAll, map.keys.toList())
        map.forEach { (chip, status) ->
            chip.setOnClickListener {
                currentFilter = status
                selectChip(chip, map.keys.toList())
                renderList()
            }
        }
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_text_secondary))
            chip.setTypeface(null, if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }
    }

    private fun renderList() {
        binding.examList.removeAllViews()
        val data = PracticeSampleData.filteredExams(currentFilter)
        if (data.isEmpty()) {
            val empty = TextView(this).apply {
                text = "Không có đề thi phù hợp bộ lọc"
                setTextColor(getColor(R.color.practice_text_secondary))
                textSize = 14f
                setPadding(8, 48, 8, 8)
            }
            binding.examList.addView(empty)
            return
        }
        data.forEach { exam ->
            val item = ItemPracticeExamBinding.inflate(LayoutInflater.from(this), binding.examList, false)
            bindExam(item, exam)
            binding.examList.addView(item.root)
        }
    }

    private fun bindExam(item: ItemPracticeExamBinding, exam: PracticeExam) {
        item.tvIndex.text = exam.index
        item.tvTitle.text = exam.title
        item.tvMeta.text = exam.meta
        when (exam.status) {
            ExamStatus.TODO -> {
                item.tvStatus.text = getString(R.string.practice_status_todo)
                item.tvStatus.setBackgroundResource(R.drawable.bg_practice_status_todo)
                item.tvStatus.setTextColor(getColor(R.color.practice_text_secondary))
            }
            ExamStatus.DOING -> {
                item.tvStatus.text = getString(R.string.practice_status_doing, exam.answeredCount)
                item.tvStatus.setBackgroundResource(R.drawable.bg_practice_status_doing)
                item.tvStatus.setTextColor(getColor(R.color.practice_chip_progress_text))
            }
            ExamStatus.DONE -> {
                item.tvStatus.text = getString(R.string.practice_status_done, exam.score)
                item.tvStatus.setBackgroundResource(R.drawable.bg_practice_status_done)
                item.tvStatus.setTextColor(getColor(R.color.practice_chip_done_text))
            }
            ExamStatus.ALL -> Unit
        }
        item.root.isClickable = true
        item.root.isFocusable = true
        item.root.setOnClickListener {
            PracticeSampleData.currentExamId = exam.id
            startActivity(
                Intent(this, PracticeExamOverviewActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
            )
        }
    }

    private fun setupBottomNav() {
        val toast = { label: String ->
            Toast.makeText(this, "$label (UI mẫu)", Toast.LENGTH_SHORT).show()
        }
        binding.bottomNavInclude.navHome.setOnClickListener { toast(getString(R.string.practice_nav_home)) }
        binding.bottomNavInclude.navLibrary.setOnClickListener { toast(getString(R.string.practice_nav_library)) }
        binding.bottomNavInclude.navPractice.setOnClickListener { /* đang ở đây */ }
        binding.bottomNavInclude.navRank.setOnClickListener { toast(getString(R.string.practice_nav_rank)) }
        binding.bottomNavInclude.navSettings.setOnClickListener { toast(getString(R.string.practice_nav_settings)) }
    }
}
