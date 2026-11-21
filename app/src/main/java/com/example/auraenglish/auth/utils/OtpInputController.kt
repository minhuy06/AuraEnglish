package com.example.auraenglish.auth.utils

import android.view.KeyEvent
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged

class OtpInputController(
    private val inputs: List<EditText>,
    private val onChanged: () -> Unit
) {
    init {
        inputs.forEachIndexed { index, input ->
            input.doAfterTextChanged { value ->
                onChanged()
                if (value?.length == 1) inputs.getOrNull(index + 1)?.requestFocus()
            }
            input.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    input.text.isNullOrEmpty()
                ) {
                    inputs.getOrNull(index - 1)?.requestFocus()
                }
                false
            }
        }
    }

    fun code(): String = inputs.joinToString(separator = "") { it.text?.toString().orEmpty() }

    fun clear() {
        inputs.forEach { it.text?.clear() }
        inputs.firstOrNull()?.requestFocus()
    }
}
