package com.example.auraenglish.room.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityRoomLobbyBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.room.adapter.RoomMemberAdapter
import com.example.auraenglish.room.data.RoomSessionStore
import com.example.auraenglish.room.data.RoomTopic
import com.example.auraenglish.room.helper.RoomInviteDialogHelper
import com.example.auraenglish.room.viewmodel.RoomViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RoomLobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomLobbyBinding
    private val viewModel: RoomViewModel by viewModels()

    private val memberAdapter by lazy {
        RoomMemberAdapter(
            onEmptySlotClick = { openInviteDialog() },
            onKickClick = { member ->
                viewModel.removeMember(member.id)
                showToast(getString(R.string.room_toast_removed_fmt, member.name))
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupRecyclerView()
        bindRoomInfo()
        observeViewModel()
        setupListeners()
        registerBackHandler()
    }

    override fun onResume() {
        super.onResume()
        bindRoomInfo()
        viewModel.refreshLobby()
    }

    private fun setupInsets() = with(binding) {
        val baseLeft = rootRoomLobby.paddingLeft
        val baseTop = rootRoomLobby.paddingTop
        val baseRight = rootRoomLobby.paddingRight
        val baseBottom = rootRoomLobby.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(rootRoomLobby) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                baseLeft + bars.left,
                baseTop + bars.top,
                baseRight + bars.right,
                baseBottom + bars.bottom
            )
            insets
        }
    }

    private fun setupRecyclerView() = with(binding.rvRoomMembers) {
        layoutManager = LinearLayoutManager(this@RoomLobbyActivity)
        setHasFixedSize(true)
        adapter = memberAdapter
    }

    private fun bindRoomInfo() = with(binding) {
        val room = RoomSessionStore.currentRoom
        tvLobbyRoomName.text = room.roomName
        tvLobbyTopicBadge.setText(
            when (room.topic) {
                RoomTopic.TOEIC -> R.string.room_topic_toeic
                RoomTopic.GRAMMAR -> R.string.room_topic_grammar
            }
        )
        tvLobbyConfigMeta.setText(
            if (room.questionCount <= 5) R.string.room_hub_config_quick
            else R.string.room_hub_config_standard
        )
        tvLobbyRoomCode.text = room.code
    }

    private fun observeViewModel() = with(binding) {
        viewModel.lobbySlots.observe(this@RoomLobbyActivity) { slots ->
            memberAdapter.submitList(slots)
        }

        viewModel.memberCount.observe(this@RoomLobbyActivity) { count ->
            tvMembersHeader.text = getString(R.string.room_lobby_members_header_fmt, count)
        }

        viewModel.isRoomFull.observe(this@RoomLobbyActivity) { isFull ->
            btnInviteMember.isEnabled = !isFull
            btnInviteMember.alpha = if (isFull) 0.5f else 1f
            tvRoomFullHint.setText(
                if (isFull) R.string.room_lobby_room_full
                else R.string.room_lobby_need_more_members
            )
        }
    }

    private fun setupListeners() = with(binding) {
        btnBackLobby.setOnClickListener { confirmLeaveRoom() }
        btnLeaveRoom.setOnClickDebounced { confirmLeaveRoom() }

        btnCopyRoomCode.setOnClickDebounced {
            val code = RoomSessionStore.currentRoom.code
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.room_lobby_code_label), code)
            clipboard.setPrimaryClip(clip)
            showToast(getString(R.string.room_lobby_copied_toast, code))
        }

        btnInviteMember.setOnClickDebounced { openInviteDialog() }

        btnStartRoomExam.setOnClickDebounced {
            startActivity(Intent(this@RoomLobbyActivity, RoomExamActivity::class.java))
        }
    }

    private fun registerBackHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmLeaveRoom()
                }
            }
        )
    }

    private fun openInviteDialog() {
        val remainingSlots = RoomSessionStore.MAX_MEMBERS - RoomSessionStore.members.size
        if (remainingSlots <= 0) {
            showToast(R.string.room_lobby_room_full)
            return
        }
        val existingNames = RoomSessionStore.members.mapTo(HashSet()) { it.name }
        RoomInviteDialogHelper.showInviteDialog(
            activity = this,
            roomCode = RoomSessionStore.currentRoom.code,
            remainingSlots = remainingSlots,
            existingNames = existingNames,
            onInviteSubmit = { rawName -> viewModel.inviteMember(rawName) },
            onInvitedSuccess = { member ->
                showToast(getString(R.string.room_toast_invited_fmt, member.name))
            }
        )
    }

    private fun confirmLeaveRoom() {
        val roomCode = RoomSessionStore.currentRoom.code
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.room_lobby_leave_title)
            .setMessage(getString(R.string.room_lobby_leave_msg, roomCode))
            .setPositiveButton(R.string.room_lobby_btn_leave) { _, _ -> finish() }
            .setNegativeButton(R.string.room_invite_btn_cancel, null)
            .show()
    }
}
