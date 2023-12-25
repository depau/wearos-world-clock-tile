package gay.depau.worldclocktile.presentation.viewmodels

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import gay.depau.worldclocktile.AppSettings
import gay.depau.worldclocktile.SettingChangeListener
import gay.depau.worldclocktile.WorldClockTileService
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
        get() = (0..WorldClockTileService.MAX_TILE_ID).first { it !in enabledTileIds }
}

class TileManagementViewModel : ViewModel(), SettingChangeListener {
    private val _state = MutableStateFlow(TileManagementState(emptyList(), emptyMap()))
    val state: StateFlow<TileManagementState> = _state.asStateFlow()

    override fun refreshSettings(settings: AppSettings) {
        if (settings.tileId == null) return
        _state.update {
            it.copy(
                tileSettings = it.tileSettings + (settings.tileId to TileSettingsState(
                    settings.timezoneId, settings.cityName, settings.time24h, settings.listOrder, settings.colorScheme
                ))
            )
        }
    }

    @Composable
    fun rememberTileSettings(tileId: Int): State<TileSettingsState> {
        val stateValue by state.collectAsState()
        return remember(stateValue.tileSettings, tileId) {
            derivedStateOf { stateValue.tileSettings[tileId] ?: TileSettingsState.Empty }
        }
    }

    @Composable
    fun canAddTiles(): State<Boolean> {
        val stateValue by state.collectAsState()
        return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles) {
            derivedStateOf {
                stateValue.canAddRemoveTiles &&
                        stateValue.enabledTileIds.size <= WorldClockTileService.MAX_TILE_ID
            }
        }
    }

    @Composable
    fun canRemoveTiles(): State<Boolean> {
        val stateValue by state.collectAsState()
        return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles) {
            derivedStateOf {
                stateValue.canAddRemoveTiles &&
                        stateValue.enabledTileIds.size > 1
            }
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