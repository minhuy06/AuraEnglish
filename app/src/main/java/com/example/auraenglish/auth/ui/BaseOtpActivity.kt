package com.example.auraenglish.auth.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.auraenglish.R
import com.example.auraenglish.auth.AuthContract
import com.example.auraenglish.auth.utils.OtpInputController
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.auth.utils.bind
import com.example.auraenglish.auth.validator.AuthFormValidator
import com.example.auraenglish.databinding.ActivityAuthOtpBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast

abstract class BaseOtpActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityAuthOtpBinding
    protected lateinit var email: String
    private lateinit var otpController: OtpInputController
    private var countDownTimer: CountDownTimer? = null

    protected abstract val logo: String
    @get:StringRes protected abstract val titleRes: Int
    @get:StringRes protected abstract val subtitleRes: Int
    @get:StringRes protected abstract val primaryActionRes: Int
    @get:StringRes protected abstract val secondaryActionRes: Int
    protected open val secondaryResendsCode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = intent.getStringExtra(AuthContract.EXTRA_EMAIL).orEmpty()
            .ifBlank { getString(R.string.auth_email_hint) }

        applyAuthInsets(binding.rootAuthOtp)
        binding.authHeader.bind(this, logo, titleRes, subtitleRes)
        binding.btnOtpPrimary.setText(primaryActionRes)
        binding.tvOtpSecondary.setText(secondaryActionRes)
        binding.tvOtpSentTo.text = getString(R.string.auth_code_sent_fmt, email)

        otpController = OtpInputController(otpInputs()) {
            binding.tvOtpError.visibility = View.GONE
        }
        binding.btnOtpPrimary.setOnClickDebounced { submitCode() }
        binding.tvOtpSecondary.setOnClickDebounced {
            if (secondaryResendsCode) resendCode() else onSecondaryAction()
        }
        binding.tvOtpCountdown.setOnClickDebounced {
            if (binding.tvOtpCountdown.isEnabled) resendCode()
        }
        startCountdown()
    }

    private fun otpInputs() = with(binding) {
        listOf(edtOtp1, edtOtp2, edtOtp3, edtOtp4, edtOtp5, edtOtp6)
    }

    private fun submitCode() {
        val error = AuthFormValidator.validateCode(otpController.code())
        binding.tvOtpError.visibility = if (error == null) View.GONE else View.VISIBLE
        if (error == null) onCodeAccepted(email) else otpInputs().first { it.text.isNullOrEmpty() }.requestFocus()
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        binding.tvOtpCountdown.isEnabled = false
        if (secondaryResendsCode) {
            binding.tvOtpSecondary.isEnabled = false
            binding.tvOtpSecondary.alpha = 0.45f
        }
        countDownTimer = object : CountDownTimer(45_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvOtpCountdown.text = getString(
                    R.string.auth_resend_countdown_fmt,
                    (millisUntilFinished / 1_000L).toInt()
                )
            }

            override fun onFinish() {
                binding.tvOtpCountdown.setText(R.string.auth_resend_ready)
                binding.tvOtpCountdown.isEnabled = true
                if (secondaryResendsCode) {
                    binding.tvOtpSecondary.isEnabled = true
                    binding.tvOtpSecondary.alpha = 1f
                }
            }
        }.start()
    }

    private fun resendCode() {
        otpController.clear()
        showToast(getString(R.string.auth_code_resent))
        startCountdown()
    }

    protected abstract fun onCodeAccepted(email: String)
    protected open fun onSecondaryAction() = Unit

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
