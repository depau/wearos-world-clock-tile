package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat

// Adapted from: https://stackoverflow.com/a/70853761

internal const val HAPTIC_DISTANCE = 20f

const val ROTARY_SCROLL_TICK = HapticFeedbackConstants.VIRTUAL_KEY
const val ROTARY_SCROLL_LIMIT = HapticFeedbackConstants.LONG_PRESS

internal data class TimestampedDelta(val time: Long, val delta: Float)


fun View.vibrate(feedbackConstant: Int = ROTARY_SCROLL_TICK) {
    if (context.isTouchExplorationEnabled) {
        // Don't mess with a blind person's vibrations
        return
    }
    // Either this needs to be set to true, or android:hapticFeedbackEnabled="true" needs to be set in XML
    isHapticFeedbackEnabled = true

    // Most of the constants are off by default: for example, clicking on a button doesn't cause the phone to vibrate anymore
    // if we still want to access this vibration, we'll have to ignore the global settings on that.
    performHapticFeedback(
        feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
    )
}

private var lazyIsTouchExplorationEnabled: Boolean? = null

val Context.isTouchExplorationEnabled: Boolean
    get() {
        if (lazyIsTouchExplorationEnabled != null) {
            return lazyIsTouchExplorationEnabled!!
        }
        val am = ContextCompat.getSystemService(this, AccessibilityManager::class.java)
        lazyIsTouchExplorationEnabled = am?.isTouchExplorationEnabled ?: false
        return lazyIsTouchExplorationEnabled!!
    }

