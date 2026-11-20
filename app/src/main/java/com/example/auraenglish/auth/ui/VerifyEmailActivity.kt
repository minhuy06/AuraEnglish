package com.example.auraenglish.auth.ui

import android.content.Intent
import com.example.auraenglish.R

class VerifyEmailActivity : BaseOtpActivity() {
    override val logo = "M"
    override val titleRes = R.string.auth_verify_title
    override val subtitleRes = R.string.auth_verify_subtitle
    override val primaryActionRes = R.string.auth_verify_button
    override val secondaryActionRes = R.string.auth_resend_link
    override val secondaryResendsCode = true

    override fun onCodeAccepted(email: String) {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}
