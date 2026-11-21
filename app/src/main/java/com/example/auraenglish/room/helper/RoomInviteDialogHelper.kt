package com.example.auraenglish.room.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogRoomInviteBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.room.data.InviteError
import com.example.auraenglish.room.data.InviteResult
import com.example.auraenglish.room.data.RoomMemberUiModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object RoomInviteDialogHelper {

    fun showInviteDialog(
        activity: Activity,
        roomCode: String,
        remainingSlots: Int,
        onInviteSubmit: (String) -> InviteResult,
        onInvitedSuccess: (RoomMemberUiModel) -> Unit
    ) {
        val binding = DialogRoomInviteBinding.inflate(LayoutInflater.from(activity))
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvInviteSubtitle.text = activity.getString(
            R.string.room_invite_subtitle_fmt,
            roomCode,
            remainingSlots
        )

        binding.edtInviteUsername.doAfterTextChanged {
            if (binding.tvInviteError.isVisible) {
                binding.tvInviteError.gone()
            }
        }

        binding.btnCloseInviteDialog.setOnClickDebounced { dialog.dismiss() }
        binding.tvCancelInvite.setOnClickDebounced { dialog.dismiss() }

        binding.btnConfirmInvite.setOnClickDebounced {
            val rawText = binding.edtInviteUsername.text?.toString().orEmpty()
            when (val result = onInviteSubmit(rawText)) {
                is InviteResult.Failure -> {
                    val errorRes = when (result.error) {
                        InviteError.EMPTY -> R.string.room_err_invite_empty
                        InviteError.DUPLICATE -> R.string.room_err_invite_duplicate
                        InviteError.ROOM_FULL -> R.string.room_err_invite_full
                    }
                    binding.tvInviteError.text = activity.getString(errorRes)
                    binding.tvInviteError.visible()
                    binding.edtInviteUsername.requestFocus()
                }
                is InviteResult.Success -> {
                    dialog.dismiss()
                    onInvitedSuccess(result.member)
                }
            }
        }

        dialog.show()
    }
}
