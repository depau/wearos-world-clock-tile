package gay.depau.worldclock.mobile.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.graphics.ColorUtils
import gay.depau.worldclocktile.shared.utils.androidColor
import gay.depau.worldclocktile.shared.utils.composeColor
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor


val AndroidColor.foreground: AndroidColor
    @Composable
    get() {
        val contrastLight = ColorUtils.calculateContrast(
            MaterialTheme.colorScheme.onSurface.androidColor.toArgb(),
            toArgb()
        )
        return if (contrastLight <= 1.6) {
            MaterialTheme.colorScheme.onPrimary.androidColor
        } else {
            MaterialTheme.colorScheme.onSurface.androidColor
        }
    }

val ComposeColor.foreground: ComposeColor
    @Composable
    get() = androidColor.foreground.composeColor

val Context.activity: Activity?
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }