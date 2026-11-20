package com.example.auraenglish.auth.ui

import android.content.Intent
import com.example.auraenglish.R
import com.example.auraenglish.auth.AuthContract

class ResetCodeActivity : BaseOtpActivity() {
    override val logo = "M"
    override val titleRes = R.string.auth_reset_code_title
    override val subtitleRes = R.string.auth_reset_code_subtitle
    override val primaryActionRes = R.string.auth_confirm_code_button
    override val secondaryActionRes = R.string.auth_change_email

    override fun onCodeAccepted(email: String) {
        startActivity(
            Intent(this, NewPasswordActivity::class.java)
                .putExtra(AuthContract.EXTRA_EMAIL, email)
        )
    }

    override fun onSecondaryAction() {
        finish()
    }
}
