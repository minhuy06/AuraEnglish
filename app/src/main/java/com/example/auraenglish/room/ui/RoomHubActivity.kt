package com.example.auraenglish.room.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityRoomHubBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.room.data.RoomSessionStore
import com.example.auraenglish.room.data.RoomTopic

class RoomHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomHubBinding
    private var selectedTopic: RoomTopic = RoomTopic.TOEIC
    private var isStandardConfig: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            val savedTopic = savedInstanceState.getString(KEY_TOPIC)
            selectedTopic = RoomTopic.entries.firstOrNull { it.name == savedTopic } ?: RoomTopic.TOEIC
            isStandardConfig = savedInstanceState.getBoolean(KEY_IS_STANDARD_CONFIG, false)
        }

        setupInsets()
        updateTopicChips()
        updateConfigChips()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TOPIC, selectedTopic.name)
        outState.putBoolean(KEY_IS_STANDARD_CONFIG, isStandardConfig)
    }

    private fun setupInsets() = with(binding) {
        val heroTop = heroRoomHub.paddingTop
        val heroBaseHeight = heroRoomHub.layoutParams.height
        val rootBottom = rootRoomHub.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(rootRoomHub) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            heroRoomHub.setPadding(
                heroRoomHub.paddingLeft,
                heroTop + bars.top,
                heroRoomHub.paddingRight,
                heroRoomHub.paddingBottom
            )
            if (heroBaseHeight > 0) {
                val lp = heroRoomHub.layoutParams
                if (lp.height != heroBaseHeight + bars.top) {
                    lp.height = heroBaseHeight + bars.top
                    heroRoomHub.layoutParams = lp
                }
            }
            view.setPadding(bars.left, 0, bars.right, rootBottom + bars.bottom)
            insets
        }
    }

    private fun setupListeners() = with(binding) {
        btnBackRoomHub.setOnClickListener { finish() }

        chipTopicToeic.setOnClickListener {
            selectedTopic = RoomTopic.TOEIC
            updateTopicChips()
        }
        chipTopicGrammar.setOnClickListener {
            selectedTopic = RoomTopic.GRAMMAR
            updateTopicChips()
        }

        chipConfigQuick.setOnClickListener {
            isStandardConfig = false
            updateConfigChips()
        }
        chipConfigStandard.setOnClickListener {
            isStandardConfig = true
            updateConfigChips()
        }

        btnCreateRoom.setOnClickDebounced {
            val rawName = edtRoomName.text?.toString()?.trim().orEmpty()
            val roomName = rawName.ifEmpty { getString(R.string.room_hub_name_default) }
            val questionCount = if (isStandardConfig) STANDARD_QUESTION_COUNT else QUICK_QUESTION_COUNT
            val durationMinutes = if (isStandardConfig) STANDARD_DURATION_MINUTES else QUICK_DURATION_MINUTES

            RoomSessionStore.createRoom(
                roomName = roomName,
                topic = selectedTopic,
                questionCount = questionCount,
                durationMinutes = durationMinutes
            )
            openLobby()
        }

        edtJoinRoomCode.doAfterTextChanged {
            if (tvJoinCodeError.isVisible) {
                tvJoinCodeError.gone()
            }
        }

        btnJoinRoom.setOnClickDebounced {
            val code = edtJoinRoomCode.text?.toString()?.trim().orEmpty()
            when {
                code.isEmpty() -> {
                    showJoinCodeError(R.string.room_err_code_empty)
                }
                code.length != ROOM_CODE_LENGTH || !code.all { it.isLetterOrDigit() } -> {
                    showJoinCodeError(R.string.room_err_code_short)
                }
                else -> {
                    tvJoinCodeError.gone()
                    RoomSessionStore.joinRoomByCode(code)
                    openLobby()
                }
            }
        }
    }

    private fun updateTopicChips() = with(binding) {
        styleChip(chipTopicToeic, isSelected = selectedTopic == RoomTopic.TOEIC)
        styleChip(chipTopicGrammar, isSelected = selectedTopic == RoomTopic.GRAMMAR)
    }

    private fun updateConfigChips() = with(binding) {
        styleChip(chipConfigQuick, isSelected = !isStandardConfig)
        styleChip(chipConfigStandard, isSelected = isStandardConfig)
    }

    private fun styleChip(chip: TextView, isSelected: Boolean) {
        chip.setBackgroundResource(
            if (isSelected) R.drawable.bg_room_chip_active else R.drawable.bg_room_chip_inactive
        )
        chip.setTextColor(
            ContextCompat.getColor(
                this,
                if (isSelected) R.color.white else R.color.room_text_secondary
            )
        )
        chip.typeface = ResourcesCompat.getFont(
            this,
            if (isSelected) R.font.be_vietnam_pro_bold else R.font.be_vietnam_pro_medium
        )
    }

    private fun showJoinCodeError(messageRes: Int) = with(binding) {
        tvJoinCodeError.setText(messageRes)
        tvJoinCodeError.visible()
        edtJoinRoomCode.requestFocus()
    }

    private fun openLobby() {
        startActivity(Intent(this, RoomLobbyActivity::class.java))
    }

    companion object {
        private const val KEY_TOPIC = "key_topic"
        private const val KEY_IS_STANDARD_CONFIG = "key_is_standard_config"
        private const val ROOM_CODE_LENGTH = 6
        private const val QUICK_QUESTION_COUNT = 5
        private const val QUICK_DURATION_MINUTES = 5
        private const val STANDARD_QUESTION_COUNT = 10
        private const val STANDARD_DURATION_MINUTES = 10
    }
}
