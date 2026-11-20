package com.example.auraenglish.practice.utils

import android.os.SystemClock
import android.view.View

private const val DEFAULT_DEBOUNCE_MS = 800L

fun View.setOnClickDebounced(
    debounceMs: Long = DEFAULT_DEBOUNCE_MS,
    onClick: (View) -> Unit
) {
    var lastClick = 0L
    setOnClickListener { view ->
        val now = SystemClock.elapsedRealtime()
        if (now - lastClick < debounceMs) return@setOnClickListener
        lastClick = now
        onClick(view)
    }
}
