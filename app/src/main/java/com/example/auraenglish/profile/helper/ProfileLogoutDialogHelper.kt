package com.example.auraenglish.profile.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.auraenglish.databinding.DialogConfirmLogoutBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ProfileLogoutDialogHelper {

    fun showLogoutConfirmDialog(
        activity: Activity,
        onConfirmLogout: () -> Unit
    ) {
        val binding = DialogConfirmLogoutBinding.inflate(LayoutInflater.from(activity))
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding) {
            btnCloseLogoutDialog.setOnClickListener { dialog.dismiss() }
            btnStayLoggedIn.setOnClickListener { dialog.dismiss() }
            btnConfirmLogout.setOnClickDebounced {
                dialog.dismiss()
                onConfirmLogout()
            }
        }

        dialog.show()
    }
}
