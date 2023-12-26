package gay.depau.worldclocktile.shared

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.SettingsChangeNotifier
import gay.depau.worldclocktile.shared.utils.delegate

interface SettingChangeListener {
    fun refreshSettings(settings: TileSettings)
}

class TileSettings(context: Context, val tileId: Int? = null) : SettingsChangeNotifier {
    private val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val listeners = mutableListOf<SettingChangeListener>()

    var dbVersion: Int by settings.delegate("dbVersion", -1)
    private val _modifyVersion: Int by settings.delegate("modifyVersion", 0)

    var modifyVersion: Int
        get() = _modifyVersion
        set(value) {
            // Don't use the delegate here, as it will trigger a refresh
            settings.edit().apply {
                putInt("modifyVersion", value)
                apply()
            }
        }

    var enabled: Boolean by settings.delegate("tile${tileId}_enabled", false)
    var timezoneId: String? by settings.delegate("tile${tileId}_timezoneId", null)
    var cityName: String? by settings.delegate("tile${tileId}_cityName", null)
    var time24h: Boolean by settings.delegate("tile${tileId}_time24h", false)
    var colorScheme: ColorScheme by settings.delegate("tile${tileId}_colorScheme", ColorScheme.Default)
    var listOrder: Int by settings.delegate("tile${tileId}_listOrder", 0)

    fun addListener(listener: SettingChangeListener) {
        listeners.add(listener)
    }

    override fun notifyListeners() {
        modifyVersion++
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