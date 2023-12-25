package gay.depau.worldclocktile

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import gay.depau.worldclocktile.utils.ColorScheme
import gay.depau.worldclocktile.utils.SettingsChangeNotifier
import gay.depau.worldclocktile.utils.delegate

interface SettingChangeListener {
    fun refreshSettings(settings: AppSettings)
}

class AppSettings(context: Context, val tileId: Int? = null) : SettingsChangeNotifier {
    private val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val listeners = mutableListOf<SettingChangeListener>()

    var dbVersion: Int by settings.delegate("dbVersion", -1)
    var timezoneId: String? by settings.delegate("tile${tileId}_timezoneId", null)
    var cityName: String? by settings.delegate("tile${tileId}_cityName", null)
    var time24h: Boolean by settings.delegate("tile${tileId}_time24h", false)
    var colorScheme: ColorScheme by settings.delegate("tile${tileId}_colorScheme", ColorScheme.Default)
    var listOrder: Int by settings.delegate("tile${tileId}_listOrder", 0)

    fun addListener(listener: SettingChangeListener) {
        listeners.add(listener)
    }

    override fun notifyListeners() {
        listeners.forEach { it.refreshSettings(this) }
    }

    fun resetTileSettings() = settings.edit().apply {
        tileId!!
        remove("tile${tileId}_timezoneId")
        remove("tile${tileId}_cityName")
        remove("tile${tileId}_time24h")
        remove("tile${tileId}_colorScheme")
        apply()
        notifyListeners()
    }
}