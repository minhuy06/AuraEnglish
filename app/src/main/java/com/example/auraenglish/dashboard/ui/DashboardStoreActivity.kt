package com.example.auraenglish.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.adapter.DashboardStoreAdapter
import com.example.auraenglish.dashboard.data.DashboardSampleData
import com.example.auraenglish.dashboard.data.StoreFilter
import com.example.auraenglish.databinding.ActivityDashboardStoreBinding
import com.example.auraenglish.practice.ui.PracticeExamListActivity
import com.example.auraenglish.practice.utils.setOnClickDebounced

// Cửa hàng bộ đề (Figma Cửa hàng)
class DashboardStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardStoreBinding
    private lateinit var adapter: DashboardStoreAdapter
    private var filter = StoreFilter.ALL
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        adapter = DashboardStoreAdapter { item ->
            if (item.owned) {
                startActivity(Intent(this, PracticeExamListActivity::class.java))
            } else {
                Toast.makeText(this, R.string.dash_buy, Toast.LENGTH_SHORT).show()
            }
        }
        binding.storeList.layoutManager = LinearLayoutManager(this)
        binding.storeList.adapter = adapter
        binding.storeList.setHasFixedSize(true)

        binding.btnBack.setOnClickDebounced { finish() }

        val chips = listOf(
            binding.chipAll to StoreFilter.ALL,
            binding.chipOwned to StoreFilter.OWNED,
            binding.chipNotOwned to StoreFilter.NOT_OWNED
        )
        chips.forEach { (chip, value) ->
            chip.setOnClickListener {
                filter = value
                selectChip(chip, chips.map { it.first })
                refresh()
            }
        }

        binding.etSearch.doAfterTextChanged {
            query = it?.toString().orEmpty().trim()
            refresh()
        }

        selectChip(binding.chipAll, chips.map { it.first })
        refresh()
    }

    private fun refresh() {
        adapter.submitList(DashboardSampleData.filteredStore(filter, query))
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_dash_chip_active else R.drawable.bg_dash_chip_idle
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (active) R.color.white else R.color.dash_primary
                )
            )
            chip.setTypeface(
                null,
                if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )
        }
    }
}
