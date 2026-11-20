package com.example.auraenglish.room.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.example.auraenglish.room.data.RoomSessionStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

object RoomInviteDialogHelper {

    fun showInviteDialog(
        activity: Activity,
        roomCode: String,
        remainingSlots: Int,
        existingNames: Set<String>,
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

        populateFriendSuggestions(activity, binding, existingNames)

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

    private fun populateFriendSuggestions(
        activity: Activity,
        binding: DialogRoomInviteBinding,
        existingNames: Set<String>
    ) {
        val normalizedExisting = existingNames.mapTo(HashSet()) { it.trim().lowercase(Locale.ROOT) }
        val availableSuggestions = RoomSessionStore.friendSuggestions.filter { suggestion ->
            suggestion.trim().lowercase(Locale.ROOT) !in normalizedExisting
        }

        val horizontalPadding = dpToPx(activity, 12)
        val verticalPadding = dpToPx(activity, 6)
        val chipMarginEnd = dpToPx(activity, 8)
        val chipTypeface = ResourcesCompat.getFont(activity, R.font.be_vietnam_pro_semibold)
        val chipTextColor = ContextCompat.getColor(activity, R.color.room_primary)

        binding.layoutFriendSuggestions.removeAllViews()
        availableSuggestions.forEach { suggestion ->
            val chip = TextView(activity).apply {
                text = suggestion
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(chipTextColor)
                typeface = chipTypeface
                setBackgroundResource(R.drawable.bg_room_chip_inactive)
                setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
                isClickable = true
                isFocusable = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = chipMarginEnd
                }
                setOnClickListener {
                    binding.edtInviteUsername.setText(suggestion)
                    binding.edtInviteUsername.setSelection(suggestion.length)
                    binding.tvInviteError.gone()
                }
            }
            binding.layoutFriendSuggestions.addView(chip)
        }
    }

    private fun dpToPx(activity: Activity, dp: Int): Int =
        (dp * activity.resources.displayMetrics.density).toInt()
}
