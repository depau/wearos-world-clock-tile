package gay.depau.worldclocktile.shared.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.ComponentCallbacks
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import kotlin.reflect.KClass


fun <T : ComponentCallbacks> KClass<T>.getComponentEnabled(context: Context): Int {
    val componentName = ComponentName(context.packageName, this.java.name)
    val packageManager = context.packageManager
    return packageManager.getComponentEnabledSetting(componentName)
}

fun <T : ComponentCallbacks> KClass<T>.setComponentEnabled(context: Context, enabled: Boolean) {
    val componentName = ComponentName(context.packageName, this.java.name)
    val packageManager = context.packageManager
    packageManager.setComponentEnabledSetting(
        componentName,
        if (enabled)
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}

fun <T : ComponentCallbacks> KClass<T>.enableComponent(context: Context) {
    setComponentEnabled(context, true)
}

fun <T : ComponentCallbacks> KClass<T>.disableComponent(context: Context) {
    setComponentEnabled(context, false)
}