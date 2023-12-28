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

class TileSettings(private val context: Context, val tileId: Int? = null) : SettingsChangeNotifier {
    private val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val listeners = mutableListOf<SettingChangeListener>()

    var dbVersion: Int by settings.delegate("dbVersion", -1)

    private val _modifyVersion: Long by settings.delegate("modifyVersion", 0)
    var modifyVersion: Long
        get() = _modifyVersion
        set(value) {
            // Don't use the delegate here, as it will trigger a refresh
            settings.edit().apply {
                putLong("modifyVersion", value)
                apply()
            }
        }

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

    val isEnabled: Boolean
        get() = settings.contains("tile${tileId}_listOrder")

    fun ensureConfigPopulated() {
        timezoneId = timezoneId
        cityName = cityName
        time24h = time24h
        colorScheme = colorScheme

        if (!isEnabled)
            listOrder = numEnabledTiles(context)
    }

    fun resetTileSettings() {
        settings.edit().apply {
            tileId!!
            remove("tile${tileId}_timezoneId")
            remove("tile${tileId}_cityName")
            remove("tile${tileId}_time24h")
            remove("tile${tileId}_colorScheme")
            remove("tile${tileId}_listOrder")
            apply()
            notifyListeners()
        }
    }

    companion object {
        fun getEnabledTileSettings(context: Context): Map<Int, TileSettings> {
            val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

            return settings.all.filter { it.key.endsWith("_listOrder") }
                .map { it.key.removeSuffix("_listOrder").removePrefix("tile") to it.value }
                .associate { it.first.toInt() to TileSettings(context, it.first.toInt()) }
        }

        fun numEnabledTiles(context: Context): Int {
            val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            return settings.all.filter { it.key.endsWith("_listOrder") }.count()
        }

        fun getFirstAvailableTileId(context: Context): Int {
            val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            for (i in 0..MAX_TILE_ID) {
                if (!settings.contains("tile$i" + "_listOrder"))
                    return i
            }
            throw IllegalStateException("No available tile ID")
        }
    }
}