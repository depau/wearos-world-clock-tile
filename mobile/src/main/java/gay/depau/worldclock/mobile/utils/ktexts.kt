package gay.depau.worldclock.mobile.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


val Context.activity: Activity?
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }