package com.example.auraenglish.admin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityAdminExamShopBinding
import com.example.auraenglish.databinding.ItemExamSetFreeBinding
import com.example.auraenglish.databinding.ItemExamSetPaidBinding

class AdminExamShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminExamShopBinding
    private lateinit var dialogs: AdminExamDialogs

    private val sampleItems = listOf(
        ExamSetUiModel(
            index = "01",
            title = "Bộ đề TOEIC - Nâng cao",
            meta = "60 đề • Listening + Reading • Đã bán: 112",
            status = "11.140.000đ",
            isPaid = true,
            priceLabel = "199.000đ",
            examCount = 60,
            description = "Gói đề nâng cao Listening + Reading"
        ),
        ExamSetUiModel(
            index = "02",
            title = "Luyện Part 5 nâng cao",
            meta = "20 đề • 600 câu hỏi • Đã bán: 74",
            status = "7.310.000đ",
            isPaid = true,
            priceLabel = "99.000đ",
            examCount = 20,
            description = "Luyện ngữ pháp Part 5"
        ),
        ExamSetUiModel(
            index = "03",
            title = "Đề thi thử TOEIC 01",
            meta = "200 câu • 120 phút (Part 1 - Part 7)",
            status = getStringPlaceholder("845 lượt", "685/990"),
            isPaid = false,
            examCount = 200,
            description = "Đề thi thử miễn phí đầy đủ 7 phần"
        ),
        ExamSetUiModel(
            index = "04",
            title = "Đề thi thử TOEIC 02",
            meta = "200 câu • 120 phút (Part 1 - Part 7)",
            status = getStringPlaceholder("620 lượt", "720/990"),
            isPaid = false,
            examCount = 200,
            description = "Đề thi thử miễn phí số 2"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminExamShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogs = AdminExamDialogs(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        bindSampleData()
        setupFilters()
        setupActions()
    }

    private fun getStringPlaceholder(attempts: String, avg: String): String {
        return "Lượt làm bài: $attempts • Điểm TB: $avg"
    }

    private fun bindSampleData() {
        bindPaid(binding.itemPaid1, sampleItems[0])
        bindPaid(binding.itemPaid2, sampleItems[1])
        bindFree(binding.itemFree1, sampleItems[2])
        bindFree(binding.itemFree2, sampleItems[3])
    }

    private fun bindPaid(itemBinding: ItemExamSetPaidBinding, item: ExamSetUiModel) {
        itemBinding.tvIndex.text = item.index
        itemBinding.tvTitle.text = item.title
        itemBinding.tvMeta.text = item.meta
        itemBinding.tvPrice.text = item.priceLabel
        itemBinding.tvStatus.text = getString(R.string.admin_status_selling, item.status)
        itemBinding.btnManage.text = getString(R.string.admin_action_manage_exams, item.examCount)

        itemBinding.btnManage.setOnClickListener {
            Toast.makeText(this, itemBinding.btnManage.text, Toast.LENGTH_SHORT).show()
        }
        itemBinding.btnEditPrice.setOnClickListener {
            dialogs.showEditPrice(item) {
                Toast.makeText(this, R.string.action_save, Toast.LENGTH_SHORT).show()
            }
        }
        itemBinding.btnStopSelling.setOnClickListener {
            dialogs.showStopSelling(item) {
                Toast.makeText(this, R.string.action_confirm_stop, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindFree(itemBinding: ItemExamSetFreeBinding, item: ExamSetUiModel) {
        itemBinding.tvIndex.text = item.index
        itemBinding.tvTitle.text = item.title
        itemBinding.tvMeta.text = item.meta
        itemBinding.tvStatus.text = item.status

        itemBinding.btnQuestions.setOnClickListener {
            Toast.makeText(this, R.string.admin_action_questions_explain, Toast.LENGTH_SHORT).show()
        }
        itemBinding.btnEdit.setOnClickListener {
            dialogs.showEditExam(item) {
                Toast.makeText(this, R.string.action_save, Toast.LENGTH_SHORT).show()
            }
        }
        itemBinding.btnDelete.setOnClickListener {
            dialogs.showDelete(item) {
                Toast.makeText(this, R.string.action_confirm_delete, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFilters() {
        val chips = listOf(binding.chipAll, binding.chipShop, binding.chipFree)
        chips.forEach { chip ->
            chip.setOnClickListener { selectChip(chip, chips) }
        }
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_admin_chip_active else R.drawable.bg_admin_chip_inactive
            )
            chip.setTextColor(
                getColor(if (active) R.color.white else R.color.admin_text_muted)
            )
            chip.setTypeface(null, if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }

        when (selected.id) {
            R.id.chipShop -> {
                binding.itemPaid1.root.visibility = android.view.View.VISIBLE
                binding.itemPaid2.root.visibility = android.view.View.VISIBLE
                binding.itemFree1.root.visibility = android.view.View.GONE
                binding.itemFree2.root.visibility = android.view.View.GONE
            }
            R.id.chipFree -> {
                binding.itemPaid1.root.visibility = android.view.View.GONE
                binding.itemPaid2.root.visibility = android.view.View.GONE
                binding.itemFree1.root.visibility = android.view.View.VISIBLE
                binding.itemFree2.root.visibility = android.view.View.VISIBLE
            }
            else -> {
                binding.itemPaid1.root.visibility = android.view.View.VISIBLE
                binding.itemPaid2.root.visibility = android.view.View.VISIBLE
                binding.itemFree1.root.visibility = android.view.View.VISIBLE
                binding.itemFree2.root.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun setupActions() {
        binding.btnCreateExamSet.setOnClickListener {
            dialogs.showCreateExamSet { name ->
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
