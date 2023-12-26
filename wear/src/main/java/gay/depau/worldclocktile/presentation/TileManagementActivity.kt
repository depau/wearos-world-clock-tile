package gay.depau.worldclocktile.presentation

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tiles.TileService.EXTRA_CLICKABLE_ID
import androidx.wear.tooling.preview.devices.WearDevices
import gay.depau.worldclocktile.R
import gay.depau.worldclocktile.WorldClockTileService.Companion.isTileEnabled
import gay.depau.worldclocktile.WorldClockTileService.Companion.setTileEnabled
import gay.depau.worldclocktile.composables.ChipWithDeleteButton
import gay.depau.worldclocktile.composables.MainView
import gay.depau.worldclocktile.composables.ScalingLazyColumnWithRSB
import gay.depau.worldclocktile.composables.YesNoConfirmationDialog
import gay.depau.worldclocktile.presentation.views.AboutView
import gay.depau.worldclocktile.shared.MAX_TILE_ID
import gay.depau.worldclocktile.shared.TileSettings
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.currentTimeAt
import gay.depau.worldclocktile.shared.utils.timezoneSimpleNames
import gay.depau.worldclocktile.shared.viewmodels.TileManagementState
import gay.depau.worldclocktile.shared.viewmodels.TileManagementViewModel
import gay.depau.worldclocktile.shared.viewmodels.TileSettingsState
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


class TileManagementActivity : ComponentActivity() {
    private val mViewModel: TileManagementViewModel by viewModels()
    private lateinit var mSettings: Map<Int, TileSettings>

    private fun openTileSettingsActivity(id: Int) {
        startActivity(Intent(this, TileSettingsActivity::class.java).apply {
            putExtra(EXTRA_CLICKABLE_ID, "tile$id")
        })
    }

    private fun refreshEnabledTiles() {
        lifecycleScope.launch {
            for (i in 0..MAX_TILE_ID) {
                mViewModel.setTileEnabled(
                    i, isTileEnabled(this@TileManagementActivity, i)
                )
            }
            mViewModel.setCanAddRemoveTiles(true)
        }
    }

    override fun onResume() {
        super.onResume()

        for (i in 0..MAX_TILE_ID) {
            mViewModel.refreshSettings(mSettings[i]!!)
        }
        refreshEnabledTiles()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = mutableMapOf<Int, TileSettings>()
        for (i in 0..MAX_TILE_ID) {
            settings[i] = TileSettings(this, i).apply {
                addListener(mViewModel)
                mViewModel.refreshSettings(this)
            }

        }
        mSettings = settings

        refreshEnabledTiles()

        setContent {
            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "main"
            ) {
                composable("main") {
                    val canEnableMoreTiles by mViewModel.canAddTiles()
                    val canDeleteTiles by mViewModel.canRemoveTiles()

                    TileManagementView(mViewModel, setTileEnabled = { id, enabled ->
                        if ((enabled && canEnableMoreTiles) || (!enabled && canDeleteTiles)) {
                            try {
                                mViewModel.setCanAddRemoveTiles(false)
                                setTileEnabled(
                                    this@TileManagementActivity, id, enabled
                                )
                                mViewModel.setTileEnabled(id, enabled)
                                mSettings[id]!!.apply {
                                    resetTileSettings()
                                    listOrder = mViewModel.state.value.enabledTileIds.size - 1
                                }
                            } finally {
                                refreshEnabledTiles()
                                mViewModel.setCanAddRemoveTiles(true)
                            }
                        }
                    }, openTileSettings = { id ->
                        openTileSettingsActivity(id)
                    }, openAbout = {
                        navController.navigate("about")
                    })
                }
                composable("about") {
                    AboutView()
                }
            }
        }
    }
}


@Composable
fun TileManagementViewModel.canAddTiles(): State<Boolean> {
    val stateValue by state.collectAsState()
    return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles) {
        derivedStateOf {
            stateValue.canAddRemoveTiles &&
                    stateValue.enabledTileIds.size <= MAX_TILE_ID
        }
    }
}

@Composable
fun TileManagementViewModel.canRemoveTiles(): State<Boolean> {
    val stateValue by state.collectAsState()
    return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles) {
        derivedStateOf {
            stateValue.canAddRemoveTiles &&
                    stateValue.enabledTileIds.size > 1
        }
    }
}


@Composable
fun TileManagementView(
    viewModel: TileManagementViewModel,
    setTileEnabled: (Int, Boolean) -> Unit,
    openTileSettings: (Int) -> Unit,
    openAbout: () -> Unit,
) {
    val listState = rememberScalingLazyListState()
    val state by viewModel.state.collectAsState()
    var tileToDelete by remember { mutableStateOf<Int?>(null) }
    val canRemoveTiles by viewModel.canRemoveTiles()
    val showDeleteDialog by remember { derivedStateOf { tileToDelete != null && canRemoveTiles } }
    val sortedTileIds by remember {
        derivedStateOf {
            state.enabledTileIds.sortedBy { state.tileSettings[it]!!.listOrder }.toList()
        }
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("title") {
                Text(
                    text = "World clock", textAlign = TextAlign.Center, style = MaterialTheme.typography.title2
                )
            }
            item("spacer1") { Spacer(modifier = Modifier.height(8.dp)) }
            items(sortedTileIds, key = { it }) { i ->
                val settings by remember { derivedStateOf { state.tileSettings[i]!! } }

                ChipWithDeleteButton(
                    modifier = Modifier.fillMaxWidth(),
                    deleteEnabled = canRemoveTiles,
                    label = {
                        if ((settings.cityName ?: "") == "") Text(
                            text = "[Not set]", fontStyle = FontStyle.Italic, maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        else Text(
                            text = settings.cityName!!, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        if (settings.timezoneId != null) {
                            val time = currentTimeAt(settings.timezoneId!!).let {
                                if (settings.time24h) it.format(DateTimeFormatter.ofPattern("H:mm"))
                                else it.format(DateTimeFormatter.ofPattern("h:mm a"))
                            }
                            Text(
                                text = "$time, ${timezoneSimpleNames[settings.timezoneId] ?: settings.timezoneId!!}",
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Tap to set up", fontStyle = FontStyle.Italic, maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colorScheme = settings.colorScheme,
                    onClick = { openTileSettings(i) },
                    onDelete = { tileToDelete = i },
                )
            }
            item("spacer2") { Spacer(modifier = Modifier.height(8.dp)) }
            item("addBtn") {
                val canAddTiles by viewModel.canAddTiles()
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    CompactChip(label = {
                        Text(text = "Add city")
                    }, enabled = canAddTiles, onClick = {
                        setTileEnabled(state.firstAvailableTileId, true)
                    }, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_add), contentDescription = "Add city"
                        )
                    })
                }
            }
            item("spacer3") { Spacer(modifier = Modifier.height(8.dp)) }
            item("helpCard") {
                Card(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                ) {
                    Text(
                        text = "Add up to 50 cities here, then add them to your tiles carousel from your watch home.",
                        modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center
                    )
                }
            }
            item("spacer4") { Spacer(modifier = Modifier.height(8.dp)) }
            item("aboutBtn") {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = openAbout, modifier = Modifier.size(40.dp), content = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_info),
                            contentDescription = "About app"
                        )
                    })
                }
            }
        }
    }

    val tileToDeleteName by remember { derivedStateOf { state.tileSettings[tileToDelete]?.cityName } }
    YesNoConfirmationDialog(showDialog = showDeleteDialog, title = {
        Text(text = "Delete tile?")
    }, onYes = {
        setTileEnabled(tileToDelete!!, false)
        tileToDelete = null
    }, dismissDialog = { tileToDelete = null }) {
        Text(
            text = tileToDeleteName ?: "[Not set]", maxLines = 2, overflow = TextOverflow.Ellipsis,
            fontStyle = if (tileToDeleteName == null) FontStyle.Italic else null
        )
    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun TileManagementViewPreview() {
    val viewModel = TileManagementViewModel().apply {
        setState(
            TileManagementState(
                enabledTileIds = listOf(0, 1, 2, 3), tileSettings = mapOf(
                    0 to TileSettingsState.Empty,
                    1 to TileSettingsState(
                        cityName = "New York, NY, USA", timezoneId = "America/New_York",
                        colorScheme = ColorScheme.ALMOND, time24h = true
                    ),
                    2 to TileSettingsState.Empty,
                    3 to TileSettingsState.Empty,
                    4 to TileSettingsState.Empty,
                    5 to TileSettingsState.Empty,
                    6 to TileSettingsState.Empty,
                    7 to TileSettingsState.Empty,
                    8 to TileSettingsState.Empty,
                    9 to TileSettingsState.Empty,
                ), canAddRemoveTiles = true
            )
        )
    }

    TileManagementView(viewModel, setTileEnabled = { id, enabled ->
        viewModel.setTileEnabled(id, enabled)
    }, openTileSettings = { }, openAbout = { })
}