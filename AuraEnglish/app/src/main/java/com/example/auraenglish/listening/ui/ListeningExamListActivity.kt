package com.example.auraenglish.listening.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.databinding.ActivityListeningExamListBinding
import com.example.auraenglish.listening.adapter.ListeningExamAdapter
import com.example.auraenglish.listening.data.ListeningSampleData
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab

// Danh sách đề luyện nghe
class ListeningExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListeningExamListBinding
    private lateinit var adapter: ListeningExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListeningExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        adapter = ListeningExamAdapter { exam ->
            startActivity(
                Intent(this, ListeningQuestionActivity::class.java)
                    .putExtra(ListeningSampleData.EXTRA_EXAM_ID, exam.id)
            )
        }
        binding.examList.layoutManager = LinearLayoutManager(this)
        binding.examList.adapter = adapter
        binding.examList.setHasFixedSize(true)

        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.PRACTICE)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        ListeningSampleData.exams.forEach { ListeningSampleData.refreshProgress(it) }
        adapter.submitList(ListeningSampleData.exams.toList())
    }
}
