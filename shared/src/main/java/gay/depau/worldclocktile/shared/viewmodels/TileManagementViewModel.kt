package gay.depau.worldclocktile.shared.viewmodels

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import gay.depau.worldclocktile.shared.MAX_TILE_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class TileManagementState(
    val enabledTileIds: List<Int>,
    val tileSettings: Map<Int, TileSettingsState>,
    val canAddRemoveTiles: Boolean = false,
) {
    val firstAvailableTileId: Int
        get() = (0..MAX_TILE_ID).first { it !in enabledTileIds }
}

class TileManagementViewModel : ViewModel(), gay.depau.worldclocktile.shared.SettingChangeListener {
    private val _state = MutableStateFlow(TileManagementState(emptyList(), emptyMap()))
    val state: StateFlow<TileManagementState> = _state.asStateFlow()

    override fun refreshSettings(settings: gay.depau.worldclocktile.shared.TileSettings) {
        val tileId = settings.tileId ?: return
        _state.update {
            it.copy(
                tileSettings = it.tileSettings + (tileId to TileSettingsState(
                    settings.timezoneId, settings.cityName, settings.time24h, settings.listOrder, settings.colorScheme
                ))
            )
        }
    }

    fun setState(state: TileManagementState) {
        _state.update { state }
    }

    fun setTileEnabled(tileId: Int, enabled: Boolean) {
        _state.update {
            it.copy(
                enabledTileIds = (if (enabled) it.enabledTileIds + tileId
                else it.enabledTileIds - tileId).distinct()
            )
        }
    }

    fun setCanAddRemoveTiles(value: Boolean) {
        _state.update {
            it.copy(canAddRemoveTiles = value)
        }
    }
}